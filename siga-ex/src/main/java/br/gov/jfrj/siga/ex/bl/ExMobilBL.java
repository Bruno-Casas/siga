package br.gov.jfrj.siga.ex.bl;

import br.gov.jfrj.siga.base.util.Utils;
import br.gov.jfrj.siga.cp.model.enm.CpMarcadorEnum;
import br.gov.jfrj.siga.dp.CpMarcador;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.ex.*;
import br.gov.jfrj.siga.ex.logic.ExPodeDisponibilizarNoAcompanhamentoDoProtocolo;
import br.gov.jfrj.siga.ex.logic.ExPodeReceber;
import br.gov.jfrj.siga.ex.model.enm.ExTipoDeMovimentacao;
import br.gov.jfrj.siga.hibernate.ExDao;
import br.gov.jfrj.siga.parser.PessoaLotacaoParser;
import br.gov.jfrj.siga.persistencia.ExMobilDaoFiltro;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class ExMobilBL {
    @Inject
    private ExDao exDao;

    @Inject
    private ExCompetenciaBL exComp;

    @Inject
    private ExDocumentoBL docBl;

    /**
     * Informa a sigla de um mobil.
     *
     * @param sigla
     */
    public void setSiglaMobil(ExMobil mob, String sigla) {
        sigla = sigla.trim().toUpperCase();
        if (sigla != null && sigla.contains(":"))
            sigla = sigla.split(":")[0];

        Map<String, CpOrgaoUsuario> mapAcronimo = new TreeMap<String, CpOrgaoUsuario>();
        for (CpOrgaoUsuario ou : exDao.listarOrgaosUsuarios()) {
            mapAcronimo.put(ou.getAcronimoOrgaoUsu(), ou);
            mapAcronimo.put(ou.getSiglaOrgaoUsu(), ou);
        }
        String acronimos = "";
        for (String s : mapAcronimo.keySet()) {
            acronimos += "|" + s;
        }

        final Pattern p2 = Pattern.compile("^TMP-?([0-9]{1,10})");

        // Edson: testes unitários para esta regex:
        // https://regex101.com/r/NJidBr/2
        // Ao acessar, clique em "Switch to unit tests"
        final Pattern p1 = Pattern.compile("^(?<orgao>" + acronimos
                + ")?-?(?<especie>[A-Za-z]{3})?-?(?:(?<sonumero>[0-9]{1,8})|(?:(?<ano>\\d{4}?)/?)(?<numero>[0-9]{1,8})(?<subnumero>\\.?[0-9]{1,3})??)(?:(?<via>(?:-?[a-zA-Z]{1})|(?:-[0-9]{1,2}))|(?:-?V(?<volume>[0-9]{1,2})))?$");
        final Matcher m2 = p2.matcher(sigla);
        final Matcher m1 = p1.matcher(sigla);

        if (mob.getExDocumento() == null) {
            final ExDocumento doc = new ExDocumento();
            mob.setExDocumento(doc);
        }

        if (m2.find()) {
            if (m2.group(1) != null)
                mob.getExDocumento().setIdDoc(new Long(m2.group(1)));
            return;
        }

        if (m1.find()) {
            String orgao = m1.group("orgao");
            String especie = m1.group("especie");
            String ano = m1.group("ano");
            String numero = m1.group("numero");
            String subnumero = m1.group("subnumero");
            String sonumero = m1.group("sonumero");
            String via = m1.group("via");
            String volume = m1.group("volume");

            if (orgao != null && orgao.length() > 0) {
                try {
                    if (mapAcronimo.containsKey(orgao)) {
                        mob.getExDocumento().setOrgaoUsuario(mapAcronimo.get(orgao));
                    } else {
                        CpOrgaoUsuario orgaoUsuario = new CpOrgaoUsuario();
                        orgaoUsuario.setSiglaOrgaoUsu(orgao);

                        orgaoUsuario = exDao.consultarPorSigla(orgaoUsuario);

                        mob.getExDocumento().setOrgaoUsuario(orgaoUsuario);
                    }
                } catch (final Exception ce) {

                }
            }

            if (especie != null) {
                try {
                    ExFormaDocumento formaDoc = new ExFormaDocumento();
                    formaDoc.setSiglaFormaDoc(especie);
                    formaDoc = exDao.consultarPorSigla(formaDoc);
                    if (formaDoc != null)
                        mob.getExDocumento().setExFormaDocumento(formaDoc);
                } catch (final Exception ce) {
                    throw new Error(ce);
                }
            }

            if (ano != null)
                mob.getExDocumento().setAnoEmissao(Long.parseLong(ano));
            // else {
            // Date dt = new Date();
            // getExDocumento().setAnoEmissao((long) dt.getYear());
            // }
            if (numero != null)
                mob.getExDocumento().setNumExpediente(Long.parseLong(numero));
            if (sonumero != null) {
                mob.getExDocumento().setNumExpediente(Long.parseLong(sonumero));
                mob.getExDocumento().setAnoEmissao((long) new Date().getYear() + 1900);

            }

            // Numero de sequencia do documento filho
            //
            if (subnumero != null) {
                String vsNumSubdocumento = subnumero.toUpperCase();
                if (vsNumSubdocumento.contains("."))
                    vsNumSubdocumento = vsNumSubdocumento.substring(vsNumSubdocumento.indexOf(".") + 1);
                Integer vshNumSubdocumento = new Integer(vsNumSubdocumento);
                if (vshNumSubdocumento != 0) {
                    try {
                        String siglaPai = (orgao == null ? (mob.getExDocumento().getOrgaoUsuario() != null
                                ? mob.getExDocumento().getOrgaoUsuario().getAcronimoOrgaoUsu() : "") : orgao)
                                + (especie == null ? "" : especie) + (ano == null ? "" : ano)
                                + ((ano != null && numero != null) ? "/" : "") + (numero == null ? "" : numero);
                        ExMobilDaoFiltro flt = new ExMobilDaoFiltro();
                        flt.setSigla(siglaPai);
                        ExMobil mobPai = null;
                        if (flt.getIdOrgaoUsu() == null) {
                            flt.setIdOrgaoUsu(mob.getExDocumento().getOrgaoUsuario().getId());
                        }
                        mobPai = exDao.consultarPorSigla(flt);
                        ExDocumento docFilho = mobPai.doc().getMobilGeral().getSubdocumento(vshNumSubdocumento);
                        mob.setExDocumento(docFilho);
                    } catch (Exception e) {
                        // e.printStackTrace();
                    }
                }
            }

            // Numero da via
            //
            if (via != null) {
                String vsNumVia = via.toUpperCase();
                if (vsNumVia.contains("-"))
                    vsNumVia = vsNumVia.substring(vsNumVia.indexOf("-") + 1);
                Integer vshNumVia;
                final String alfabeto = "ABCDEFGHIJLMNOPQRSTUZ";
                final int vi = (alfabeto.indexOf(vsNumVia)) + 1;
                if (vi <= 0)
                    vshNumVia = new Integer(vsNumVia);
                else {
                    vshNumVia = (new Integer(vi).intValue());
                    mob.setExTipoMobil(exDao.consultar(ExTipoMobil.TIPO_MOBIL_VIA, ExTipoMobil.class, false));
                }
                mob.setNumSequencia(vshNumVia);
            } else {
                if (volume != null) {
                    String vsNumVolume = volume.toUpperCase();
                    Integer vshNumVolume = new Integer(vsNumVolume);
                    mob.setExTipoMobil(
                            exDao.consultar(ExTipoMobil.TIPO_MOBIL_VOLUME, ExTipoMobil.class, false));
                    mob.setNumSequencia(vshNumVolume);
                } else {
                    mob.setExTipoMobil(
                            exDao.consultar(ExTipoMobil.TIPO_MOBIL_GERAL, ExTipoMobil.class, false));
                }
            }
        }
    }

    /**
     * Verifica se um Mobil está em trânsito. Um Mobil está em trânsito quando
     * ele possui movimentações não canceladas dos tipos: TRANSFERENCIA,
     * DESPACHO_TRANSFERENCIA, DESPACHO_TRANSFERENCIA_EXTERNA ou
     * TRANSFERENCIA_EXTERNA e não possuem movimentação de recebimento.
     * <p>
     * Nato: alterei para sinalizar apenas se existe recebimento pendente para a pessoa em questão. Pois agora temos o trâmite paralelo.
     *
     * @return Verdadeiro se o Mobil está em trânsito e Falso caso contrário.
     */
    public boolean isEmTransito(ExMobil mob, DpPessoa titular, DpLotacao lotaTitular) {
        ExTramiteBL.Pendencias p = mob.calcularTramitesPendentes();

        if (mob.isApensadoAVolumeDoMesmoProcesso() || p.tramitesPendentes.size() == 0)
            return false;
        return exComp.pode(ExPodeReceber.class, titular, lotaTitular, mob);

    }

    public List<ExArquivoNumerado> getArquivosNumerados(ExMobil mob) {
        return docBl.getArquivosNumeradosDoc(mob.getExDocumento(), mob);
    }

    public List<ExArquivoNumerado> filtrarArquivosNumerados(ExMobil mob, ExMovimentacao mov, boolean bCompleto) {
        ExMobil mobPrincipal = mob.getMobilPrincipal();

        List<ExArquivoNumerado> arquivosNumerados = null;

        arquivosNumerados = docBl.getArquivosNumeradosDoc(mobPrincipal.getExDocumento(), mobPrincipal);

        boolean teveReordenacao = (mobPrincipal.getDoc() != null &&
                mobPrincipal.getDoc().podeReordenar() &&
                mobPrincipal.getDoc().podeExibirReordenacao() &&
                mobPrincipal.getDoc().temOrdenacao());

        List<ExArquivoNumerado> ans = new ArrayList<ExArquivoNumerado>();
        int i = 0;
        if (mov != null) {
            // Se for uma movimentacao, remover todos os arquivos alem da
            // movimentacao
            for (ExArquivoNumerado an : arquivosNumerados) {
                if (an.getArquivo() instanceof ExMovimentacao) {
                    if (((ExMovimentacao) an.getArquivo()).getIdMov().equals(mov.getIdMov())) {
                        ans.add(an);
                        break;
                    }
                }
            }
        } else if (mobPrincipal != mob) {
            // Se for um documento juntado, remover todos os documentos que alem
            // dele e de seus anexos
            ExArquivoNumerado an;
            for (i = 0; i < arquivosNumerados.size(); i++) {
                an = arquivosNumerados.get(i);
                if (an.getArquivo() instanceof ExDocumento) {
                    if (((ExDocumento) an.getArquivo()).getIdDoc().equals(mob.getExDocumento().getIdDoc())
                            && an.getMobil().equals(mob)) {
                        ans.add(an);
                        break;
                    }
                }
            }
        } else {
            if (mobPrincipal == mob && teveReordenacao) {
                for (ExArquivoNumerado arquivo : arquivosNumerados) {
                    if (mobPrincipal.getDoc().getIdDoc().equals(arquivo.getArquivo().getIdDoc())) {
                        ans.add(arquivo);
                        break;
                    }
                }
            } else {
                ans.add(arquivosNumerados.get(0));
            }
        }

        if (bCompleto && teveReordenacao) {
            ans.clear();
            for (ExArquivoNumerado arquivo : arquivosNumerados) {
                ans.add(arquivo);
            }
        } else if (bCompleto && i != -1) {
            for (int j = i + 1; j < arquivosNumerados.size(); j++) {
                ExArquivoNumerado anSub = arquivosNumerados.get(j);
                if (anSub.getNivel() <= arquivosNumerados.get(i).getNivel())
                    break;
                ans.add(anSub);
            }
        }
        return ans;
    }

    public int getTotalDePaginas(ExMobil mob) {
        int totalDePaginas = 0;

        for (ExArquivoNumerado arquivo : getArquivosNumerados(mob)) {

            totalDePaginas += arquivo.getNumeroDePaginasParaInsercaoEmDossie();
        }

        return totalDePaginas;
    }

    public int getTotalDePaginasSemAnexosDoMobilGeral(ExMobil mob) {
        int totalDePaginasDoGeral = 0;

        if (mob.getDoc().getMobilGeral().temAnexos()) {
            totalDePaginasDoGeral = getTotalDePaginas(mob.getDoc().getMobilGeral());
        }

        return getTotalDePaginas(mob) - totalDePaginasDoGeral;
    }

    public SortedSet<ExMarca> getExMarcaSetAtivas(ExMobil mob) {
        SortedSet<ExMarca> finalSet = new TreeSet<>();
        Date dt = new Date();

        Set<ExMarca> marcas = mob.getExMarcaSet();
        if (Objects.isNull(marcas))
            return finalSet;

        for (ExMarca m : marcas) {
            if (!((m.getDtIniMarca() == null || m.getDtIniMarca().before(dt))
                    && (m.getDtFimMarca() == null || m.getDtFimMarca().after(dt))))
                continue;
            CpMarcador marcador = exDao.obterAtual(m.getCpMarcador());
            if (marcador == null || !marcador.isAtivo())
                continue;
            finalSet.add(m);
        }
        return finalSet;
    }

    /**
     * Retorna a descrição dos marcadores relacionado ao Mobil atual.
     *
     * @return Descrição dos marcadores relacionado ao Mobil atual.
     */
    public String getMarcadores(ExMobil mob) {
        StringBuilder sb = new StringBuilder();
        for (ExMarca mar : getExMarcaSetAtivas(mob)) {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(mar.getCpMarcador().getDescrMarcador());
        }
        if (sb.length() == 0)
            return null;
        return sb.toString();
    }

    public boolean isModeloIncluso(ExMobil mob, Long idModelo, Date depoisDaData) {
        ExModelo mod = exDao.consultar(idModelo, ExModelo.class, false);

        for (ExMovimentacao m : mob.getExMovimentacaoReferenciaSet()) {
            if (m.getExMovimentacaoCanceladora() != null)
                continue;
            if (depoisDaData != null && depoisDaData.after(m.getDtIniMov()))
                continue;
            if (m.getExTipoMovimentacao() != ExTipoDeMovimentacao.JUNTADA)
                continue;
            if (m.getExMobilRef() == mob && m.getExMobil() != null
                    && m.getExMobil().doc().getExModelo().getIdInicial().equals(mod.getIdInicial()))
                return true;
        }
        return false;
    }

    public boolean isAtendente(ExMobil mob, DpPessoa pessoa, DpLotacao lotacao) {
        Set<PessoaLotacaoParser> set = mob.getAtendente();

        for (Iterator<PessoaLotacaoParser> i = set.iterator(); i.hasNext();) {
            PessoaLotacaoParser pp = i.next();

            boolean estaTranferindo = false;
            boolean emCaixaDeEntrada = false;
            for (ExMarca marca : getExMarcaSetAtivas(mob)) {
                if (marca.getCpMarcador().getId() == CpMarcadorEnum.EM_TRANSITO_ELETRONICO.getId())
                    estaTranferindo = true;
                else if (marca.getCpMarcador().getId() == CpMarcadorEnum.CAIXA_DE_ENTRADA.getId())
                    emCaixaDeEntrada = true;
            }

            if (!estaTranferindo && !emCaixaDeEntrada)
                break;

            if ((Objects.nonNull(pessoa) && Objects.equals(pp.getLotacao(), lotacao)) && !Objects.equals(pp.getPessoa(), pessoa))
                i.remove();
        }

        return equivalePessoaOuLotacaoPreferencialmentePelaLotacao(pessoa, lotacao, set);
    }

    public boolean isNotificado(ExMobil mob, DpPessoa pessoa, DpLotacao lotacao) {
        Set<PessoaLotacaoParser> set = mob.getNotificados();
        return equivalePessoaOuLotacaoPreferencialmentePelaLotacao(pessoa, lotacao, set);
    }

    public boolean isRecebido(ExMobil mob, DpPessoa pessoa, DpLotacao lotacao) {
        Set<PessoaLotacaoParser> set = mob.getRecebidos();
        return equivalePessoaOuLotacaoPreferencialmentePelaLotacao(pessoa, lotacao, set);
    }

    public boolean isAReceber(ExMobil mob, DpPessoa pessoa, DpLotacao lotacao) {
        Set<PessoaLotacaoParser> set = mob.getAReceber();
        return equivalePessoaOuLotacaoPreferencialmentePelaLotacao(pessoa, lotacao, set);
    }

    /**
     * Retorna se a destinação final do móbil é guarda permanente. Essa
     * avaliação considera todos os móbiles juntados e a existência de indicação
     * para guarda permanente.
     *
     * @return
     */
    public boolean isDestinacaoGuardaPermanente(ExMobil mob) {
        ExTipoDestinacao dest = getExDestinacaoFinalEfetiva(mob);
        return dest != null && dest.getIdTpDestinacao().equals(ExTipoDestinacao.TIPO_DESTINACAO_GUARDA_PERMANENTE);
    }

    /**
     * Retorna se a destinação final do móbil é eliminação. Essa avaliação
     * considera todos os móbiles juntados e a existência de indicação para
     * guarda permanente.
     *
     * @return
     */
    public boolean isDestinacaoEliminacao(ExMobil mob) {
        ExTipoDestinacao dest = getExDestinacaoFinalEfetiva(mob);
        return dest != null && dest.getIdTpDestinacao().equals(ExTipoDestinacao.TIPO_DESTINACAO_ELIMINACAO);
    }

    /**
     * Retorna a destinação final deste móbil conforme o PCTT, considerando a
     * destinação de todos os outros móbiles da árvore de juntados, predominando
     * a guarda permanente sobre a eliminação.
     *
     * @return
     */
    public ExTipoDestinacao getExDestinacaoFinalEfetiva(ExMobil mobil) {
        ExTipoDestinacao destinacaoPredominante = null;
        for (ExMobil mob : mobil.getArvoreMobilesParaAnaliseDestinacao())
            if (mob.getExDestinacaoFinal() != null && mob.getExDestinacaoFinal().getIdTpDestinacao()
                    .equals(ExTipoDestinacao.TIPO_DESTINACAO_GUARDA_PERMANENTE))
                return mob.getExDestinacaoFinal();
            else if (mob.isindicadoGuardaPermanente())
                return exDao.consultar(ExTipoDestinacao.TIPO_DESTINACAO_GUARDA_PERMANENTE, ExTipoDestinacao.class, false);
            else
                destinacaoPredominante = mob.getExDestinacaoFinal();
        return destinacaoPredominante;
    }

    private boolean equivalePessoaOuLotacaoPreferencialmentePelaLotacao(DpPessoa pessoa, DpLotacao lotacao, Set<PessoaLotacaoParser> set) {
        for (PessoaLotacaoParser pl : set) {
            if (pl.getLotacao() != null) {
                if (Utils.equivale(pl.getLotacao(), lotacao))
                    return true;
            } else if (pl.getPessoa() != null && Utils.equivale(pl.getPessoa(), pessoa))
                return true;
        }
        return false;
    }

    /**
     * Verifica se exibe o conteudo do documento no histórico do acompanhamento do protocolo
     *
     * @return
     */
    public boolean isExibirNoAcompanhamento(ExMobil mob) {
        return podeExibirNoAcompanhamento(mob, null, null);
    }

    public boolean podeExibirNoAcompanhamento(ExMobil mob, DpPessoa pessoa, DpLotacao lotacao) {
        Set<ExMovimentacao> movs = mob.getMovsNaoCanceladas(ExTipoDeMovimentacao
                .EXIBIR_NO_ACOMPANHAMENTO_DO_PROTOCOLO);
        if (!movs.isEmpty())
            return exComp.pode(ExPodeDisponibilizarNoAcompanhamentoDoProtocolo.class, pessoa, lotacao, mob.getDoc());
        return false;
    }

}
