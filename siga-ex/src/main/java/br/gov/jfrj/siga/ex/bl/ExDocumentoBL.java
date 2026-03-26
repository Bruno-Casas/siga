package br.gov.jfrj.siga.ex.bl;

import br.gov.jfrj.siga.base.SigaMessages;
import br.gov.jfrj.siga.cp.CpArquivo;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.ex.*;
import br.gov.jfrj.siga.ex.BIE.ExBoletimDoc;
import br.gov.jfrj.siga.ex.model.enm.ExTipoDeMovimentacao;
import br.gov.jfrj.siga.ex.util.AnexoNumeradoComparator;
import br.gov.jfrj.siga.hibernate.ExDao;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import java.util.*;

public class ExDocumentoBL {

    @Inject
    private ExDao exDao;

    private final static Logger log = Logger.getLogger(ExDocumentoBL.class);

    public ExNivelAcesso getNivelAcessoAtualDoc(ExDocumento doc) {
        ExNivelAcesso nivel = doc.getDnmExNivelAcesso();
        if (nivel == null)
            return atualizarDnmNivelAcesso(doc);
        return nivel;
    }

    public Long getIdNivelAcessoDoc(ExDocumento doc) {
        ExNivelAcesso exNivelAcessoAtual = getNivelAcessoAtualDoc(doc);
        if (exNivelAcessoAtual != null) {
            return exNivelAcessoAtual.getIdNivelAcesso();
        }
        return null;
    }

    /**
     * Verifica se o prazo para todos assinarem o documento está vencido.
     */
    public boolean isPrazoDeAssinaturaVencido(ExDocumento doc) {
        Date dtNow = exDao.consultarDataEHoraDoServidor();
        ExMovimentacao mov = doc.getMovPrazoDeAssinatura();

        if (mov != null && mov.getDtParam1().before(dtNow))
            return true;
        return false;
    }

    public List<Object> getListaDeAcessosDoc(ExDocumento doc) {
        if (doc.getDnmAcesso() == null || doc.isDnmAcessoMAisAntigoQueODosPais()) {
            atualizarDnmAcesso(doc);
        }

        if (getNivelAcessoAtualDoc(doc).getIdNivelAcesso().equals(ExNivelAcesso.NIVEL_ACESSO_PUBLICO)
                && ExAcesso.ACESSO_PUBLICO.equals(doc.getDnmAcesso()
        ))
            return null;

        List<Object> l = new ArrayList<>();
        String[] a = doc.getDnmAcesso().split(",");

        for (String s : a) {
            if (s.equals(ExAcesso.ACESSO_PUBLICO)) {
                l.add(s);
                continue;
            }

            if (!StringUtils.startsWithAny(s, "O", "L", "P"))
                continue;

            Long id;
            try {
                id = Long.parseLong(s.substring(1));
            } catch (NumberFormatException | NullPointerException e) {
                continue;
            }

            switch (s.charAt(0)) {
                case 'O':
                    l.add(exDao.consultar(id, CpOrgaoUsuario.class, false));
                    break;
                case 'L':
                    l.add(exDao.consultar(id, DpLotacao.class, false).getHistoricoAtual());
                    break;
                case 'P':
                    l.add(exDao.consultar(id, DpPessoa.class, false).getHistoricoAtual());
            }
        }

        return l;
    }

    public String getListaDeAcessosDocString(ExDocumento doc) {
        String s = "";
        List<Object> l = getListaDeAcessosDoc(doc);

        for (Object o : l) {
            if (s.length() > 0)
                s += ", ";
            if (ExAcesso.ACESSO_PUBLICO.equals(o))
                s += "Público";
            else if (o instanceof CpOrgaoUsuario)
                s += ((CpOrgaoUsuario) o).getSigla();
            else if (o instanceof DpLotacao)
                s += ((DpLotacao) o).getNomeLotacao() + " - " + ((DpLotacao) o).getSiglaCompleta() + "/" + ((DpLotacao) o).getOrgaoUsuario();
            else if (o instanceof DpPessoa)
                s += ((DpPessoa) o).getNomePessoa() + " - " + ((DpPessoa) o).getSiglaCompleta() + "/" + ((DpPessoa) o).getLotacao().getSigla();
            else
                s += o.toString();
        }

        return s;
    }

    public List<ExArquivoNumerado> getArquivosNumeradosDoc(ExDocumento doc, ExMobil mob) {
        List<ExArquivoNumerado> list = new ArrayList<ExArquivoNumerado>();
        return getArquivosNumeradosDoc(doc, mob, list, 0);
    }

    public List<ExDocumento> getDocumentosPublicadosNoBoletim(ExDocumento doc) {
        return exDao.consultarDocsInclusosNoBoletim(doc);
    }

    public ExDocumento getBoletimEmQueDocFoiPublicado(ExDocumento doc) {
        if (doc.isBoletimPublicado()) {
            ExBoletimDoc boletimDoc = exDao
                    .consultarBoletimEmQueODocumentoEstaIncluso(doc);

            if (boletimDoc != null)
                return boletimDoc.getBoletim();
        }

        return null;
    }

    private List<ExArquivoNumerado> getArquivosNumeradosDoc(ExDocumento doc, ExMobil mob,
                                                            List<ExArquivoNumerado> list, int nivel) {

        List<ExArquivoNumerado> listaInicial = list, listaFinal = new ArrayList<>();

        if (mob == null)
            return listaFinal;

        boolean podeAtualizarPaginas = true;

        // Incluir o documento principal
        ExArquivoNumerado anDoc = new ExArquivoNumerado();
        anDoc.setArquivo(doc);
        anDoc.setMobil(mob);
        anDoc.setNivel(nivel);
        listaInicial.add(anDoc);

        getAnexosNumeradosDoc(doc, mob, listaInicial, nivel + 1, false);

        if (doc.podeReordenar() && doc.podeExibirReordenacao() && doc.temOrdenacao()) {
            boolean houveAlteracaoNaOrdenacao = false;
            podeAtualizarPaginas = false;
            String referenciaHtmlCompletoDocPrincipal = anDoc.getReferenciaHtmlCompleto();
            String referenciaPDFCompletoDocPrincipal = anDoc.getReferenciaPDFCompleto();
            String ordenacaoDoc[] = doc.getOrdenacaoDoc().split(";");

            ordenarDocumentos(ordenacaoDoc, listaInicial, listaFinal, referenciaHtmlCompletoDocPrincipal, referenciaPDFCompletoDocPrincipal);

            if (listaInicial.size() > listaFinal.size()) {
                adicionarDocumentosNovosNaOrdenacao(listaInicial, listaFinal);
                houveAlteracaoNaOrdenacao = true;
            }

            if (ordenacaoDoc.length > listaFinal.size())
                houveAlteracaoNaOrdenacao = true;

            if (estaNaOrdemOriginal(listaInicial, listaFinal))
                doc.setOrdenacaoDoc(null);
            else if (houveAlteracaoNaOrdenacao)
                enviarNovaOrdenacaoDosDocumentos(doc, listaFinal);

        } else {
            listaFinal = listaInicial;
        }

        // Numerar as paginas
        if (doc.isNumeracaoUnicaAutomatica() || (SigaMessages.isSigaSP() && doc.mobilPrincipalPossuiJuntadaOuDesentranhamento(mob, listaFinal))) {
            if (mob.getDnmNumPrimeiraPagina() == null) {
                if (mob.isVolume() && mob.getNumSequencia() > 1) {
                    List<ExArquivoNumerado> listVolumeAnterior = getArquivosNumeradosDoc(mob.doc(), mob.doc().getVolume(
                            mob.getNumSequencia() - 1));
                    int i = listVolumeAnterior.get(
                            listVolumeAnterior.size() - 1).getPaginaFinal();
                    mob.setDnmNumPrimeiraPagina(i + 1);
                    exDao.gravar(mob);
                } else {
                    mob.setDnmNumPrimeiraPagina(1);
                }
            }
            int j = mob.getDnmNumPrimeiraPagina();

            montarPaginas(podeAtualizarPaginas ? listaFinal : listaInicial, j);
        }

        return listaFinal;
    }

    /**
     * COMPLETAR A coleção que ordena as movimentações deve respeitar a
     * cronologia, exceto no caso das movimentações de cancelamento de juntada,
     * anexação e despacho, que, quando prossuirem certidôes de exclusão, estas
     * deverão ser inseridas no lugar do documento removido.
     *
     * @param mob
     * @param list
     * @param nivel
     */
    private void getAnexosNumeradosDoc(ExDocumento doc, ExMobil mob, List<ExArquivoNumerado> list,
                                       int nivel, boolean copia) {

        SortedSet<ExMovimentacao> set = new TreeSet<ExMovimentacao>(
                new AnexoNumeradoComparator());

        incluirArquivos(doc.getMobilGeral(), set);
        if (!mob.isGeral())
            incluirArquivos(mob, set);

        // Incluir recursivamente
        for (ExMovimentacao m : set) {
            ExArquivoNumerado an = new ExArquivoNumerado();
            an.setNivel(nivel);
            if (m.getExTipoMovimentacao() == ExTipoDeMovimentacao.JUNTADA) {
                an.setArquivo(m.getExDocumento());
                an.setMobil(m.getExMobil());
                an.setData(m.getData());
                list.add(an);
                getAnexosNumeradosDoc(m.getExDocumento(), m.getExMobil(), list,
                        nivel + 1, copia);
            } else if (m.getExTipoMovimentacao() == ExTipoDeMovimentacao.COPIA) {
                an.setArquivo(m.getExMobilRef().doc());
                an.setMobil(m.getExMobilRef());
                an.setData(m.getData());
                an.setCopia(true);
                list.add(an);
            } else {
                an.setArquivo(m);
                an.setMobil(m.getExMobil());
                list.add(an);
            }
        }

    }

    private void ordenarDocumentos(String[] ordenacaoDoc, List<ExArquivoNumerado> listaInicial, List<ExArquivoNumerado> listaFinal, String referenciaHtmlCompletoDocPrincipal, String referenciaPDFCompletoDocPrincipal) {
        for (String id : ordenacaoDoc) {
            encontrarArquivoNumerado(Long.valueOf(id), listaInicial, listaFinal, referenciaHtmlCompletoDocPrincipal, referenciaPDFCompletoDocPrincipal);
        }
    }

    private void encontrarArquivoNumerado(Long id, List<ExArquivoNumerado> listaInicial, List<ExArquivoNumerado> listaFinal, String referenciaHtmlCompletoDocPrincipal, String referenciaPDFCompletoDocPrincipal) {
        for (ExArquivoNumerado arquivo : listaInicial) {
            if (id.equals(arquivo.getArquivo().getIdDoc())) {
                arquivo.setReferenciaHtmlCompletoDocPrincipal(referenciaHtmlCompletoDocPrincipal);
                arquivo.setReferenciaPDFCompletoDocPrincipal(referenciaPDFCompletoDocPrincipal);
                listaFinal.add(arquivo);
                break;
            }
        }
    }

    private void adicionarDocumentosNovosNaOrdenacao(List<ExArquivoNumerado> listaInicial, List<ExArquivoNumerado> listaFinal) {
        for (ExArquivoNumerado arquivo : listaInicial) {
            if (!listaFinal.contains(arquivo))
                listaFinal.add(arquivo);
        }
    }

    private void enviarNovaOrdenacaoDosDocumentos(ExDocumento doc, List<ExArquivoNumerado> listaArquivoNumerado) {
        String ordenacao = "";
        for (ExArquivoNumerado arquivoNumerado : listaArquivoNumerado) {
            if (ordenacao.length() > 0) ordenacao += ";";
            ordenacao += arquivoNumerado.getArquivo().getIdDoc();
        }
        doc.setOrdenacaoDoc(ordenacao);
    }

    private boolean estaNaOrdemOriginal(List<ExArquivoNumerado> listaInicial, List<ExArquivoNumerado> listaFinal) {
        return listaInicial.equals(listaFinal);
    }

    private void montarPaginas(List<ExArquivoNumerado> arquivos, int j) {
        for (ExArquivoNumerado an : arquivos) {
            an.setPaginaInicial(j);
            j += an.getNumeroDePaginasParaInsercaoEmDossie() - 1;
            an.setPaginaFinal(j);
            j++;
        }
    }

    /**
     * COMPLETAR
     *
     * @param mob
     * @param set
     */
    private void incluirArquivos(ExMobil mob, SortedSet<ExMovimentacao> set) {
        // Incluir os documentos anexos
        if (mob.getExMovimentacaoSet() != null) {
            for (ExMovimentacao m : mob.getExMovimentacaoSet()) {
                if (!m.isCancelada() && m.isPdf() && m.getExTipoMovimentacao() != ExTipoDeMovimentacao.ANEXACAO_DE_ARQUIVO_AUXILIAR) {
                    set.add(m);
                }
            }
        }
        // Incluir copias
        if (mob.getExMovimentacaoSet() != null) {
            for (ExMovimentacao m : mob.getExMovimentacaoSet()) {
                if (!m.isCancelada()) {
                    if (m.getExTipoMovimentacao() == ExTipoDeMovimentacao.COPIA) {
                        set.add(m);
                    }
                }
            }
        }
        // Incluir os documentos juntados
        if (mob.getExMovimentacaoReferenciaSet() != null)
            for (ExMovimentacao m : mob.getExMovimentacaoReferenciaSet()) {
                if (!m.isCancelada()) {
                    if (m.getExTipoMovimentacao() == ExTipoDeMovimentacao.JUNTADA) {
                        set.add(m);
                    } else if (m.getExTipoMovimentacao() == ExTipoDeMovimentacao.CANCELAMENTO_JUNTADA) {
                        set.remove(m.getExMovimentacaoRef());
                        if (m.isPdf())
                            set.add(m);
                    }
                }
            }
    }

    private ExNivelAcesso atualizarDnmNivelAcesso(ExDocumento doc) {
        log.debug("[getExNivelAcesso] - Obtendo nível de acesso atual do documento...");
        ExNivelAcesso nivel = null;
        if (doc.getMobilGeral() != null && doc.getMobilGeral().getUltimaMovimentacaoNaoCancelada() != null)
            nivel = doc.getMobilGeral().getUltimaMovimentacaoNaoCancelada().getExNivelAcesso();
        if (nivel == null)
            nivel = doc.getExNivelAcesso();
        doc.setDnmExNivelAcesso(nivel);
        salvarDocSemSalvarArq(doc);
        return nivel;
    }

    public void atualizarDnmAcesso(ExDocumento doc) {
        atualizarDnmAcesso(doc, null, null);
    }

    public void atualizarDnmAcesso(ExDocumento doc, Object incluirAcesso, Object excluirAcesso) {
        Date dt = exDao.dt();
        String acessoRecalculado = new ExAcesso(exDao, this).getAcessosString(doc, dt, incluirAcesso, excluirAcesso);

        if (doc.getDnmAcesso() == null || !doc.getDnmAcesso().equals(acessoRecalculado)) {
            doc.setDnmAcesso(acessoRecalculado);
            doc.setDnmDtAcesso(dt);
            exDao.gravar(doc);
        }
    }

    public void atualizarVariaveisDenormalizadas(ExDocumento doc, Object incluirAcesso, Object excluirAcesso) {
        atualizarDnmNivelAcesso(doc);
        atualizarDnmAcesso(doc, incluirAcesso, excluirAcesso);
    }

    private ExDocumento salvarDocSemSalvarArq(ExDocumento doc) {
        CpArquivo arqTemp = null;

        if (doc.getCpArquivo() != null && doc.getCpArquivo().getIdArq() == null) {
            arqTemp = doc.getCpArquivo();
            doc.setCpArquivo(null);
        }
        doc = exDao.gravar(doc);
        if (arqTemp != null)
            doc.setCpArquivo(arqTemp);
        return doc;
    }
}
