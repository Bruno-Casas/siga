/*******************************************************************************
 * Copyright (c) 2006 - 2011 SJRJ.
 *
 *     This file is part of SIGA.
 *
 *     SIGA is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     SIGA is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with SIGA.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package br.gov.jfrj.siga.relatorio;
import br.gov.jfrj.relatorio.dinamico.AbstractRelatorioBaseBuilder;
import br.gov.jfrj.relatorio.dinamico.RelatorioRapido;
import br.gov.jfrj.relatorio.dinamico.RelatorioTemplate;
import br.gov.jfrj.siga.cp.CpConfiguracao;
import br.gov.jfrj.siga.cp.CpConfiguracaoCache;
import br.gov.jfrj.siga.cp.CpPerfil;
import br.gov.jfrj.siga.cp.CpServico;
import br.gov.jfrj.siga.cp.bl.CpConfiguracaoBL;
import br.gov.jfrj.siga.cp.model.enm.CpTipoDeConfiguracao;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.dp.dao.CpDao;

import ar.com.fdvs.dj.domain.builders.DJBuilderException;

import javax.enterprise.inject.spi.CDI;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.*;

public class AlteracaoDireitosRelatorio extends RelatorioTemplate {
    private Date dtInicio;
    private Date dtFim;
    private CpOrgaoUsuario cpOrgaoUsuario;
    private CpDao dao;
    private CpConfiguracaoBL conf;

    public AlteracaoDireitosRelatorio(Map<String, String> parametros)
            throws DJBuilderException {
        super(parametros);
        if (parametros.get("dataInicio") == null) {
            throw new DJBuilderException(
                    "Parâmetro data de início não informada!");
        }
        if (parametros.get("dataFim") == null) {
            throw new DJBuilderException("Parâmetro data de fim não informada!");
        }
        String t_strDataHoraIni = (String) parametros.get("dataInicio")
                + " 00:00:00";
        Date t_dtaDataHoraIni;
        try {
            t_dtaDataHoraIni = converteParaData(t_strDataHoraIni);
        } catch (Exception e) {
            throw new DJBuilderException(
                    "Data de início tem de estar no formato 'DD/MM/AAAA'!");
        }
        String t_strDataHoraFim = (String) parametros.get("dataFim")
                + " 23:59:59";
        Date t_dtaDataHoraFim;
        try {
            t_dtaDataHoraFim = converteParaData(t_strDataHoraFim);
        } catch (Exception e) {
            throw new DJBuilderException(
                    "Data de fim tem de estar no formato 'DD/MM/AAAA'!");
        }

        String pathBrasao;
        try {
            Long idOrg = Long.parseLong(parametros.get("idOrgaoUsuario"));
            setCpOrgaoUsuario(dao.consultar(idOrg, CpOrgaoUsuario.class, false));
            pathBrasao = obterUrlBrasao(idOrg);
        } catch (Exception e) {
            throw new DJBuilderException("Orgao Usuario inválido ! erro:"
                    + e.getMessage());
        }

        setDtInicio(t_dtaDataHoraIni);
        setDtFim(t_dtaDataHoraFim);
        parametros.put("titulo", "SIGA");
        parametros.put("subtitulo", "Sistema de Gestão Administrativa");
        parametros.put("secaoUsuario", "");
        parametros.put("brasao", pathBrasao);
        
        this.dao = CDI.current().select(CpDao.class).get();
        this.conf = CDI.current().select(CpConfiguracaoBL.class).get();
    }

    @Override
    public AbstractRelatorioBaseBuilder configurarRelatorio()
            throws DJBuilderException {// jar:file:/fullpath/main.jar!/a.resource
        //this.setTemplateFile(null);
        this.setTitle("Alteracao de Direitos - de "
                + parametros.get("dataInicio") + " até "
                + parametros.get("dataFim"));
        this.addColuna("Pessoa", 40, RelatorioRapido.ESQUERDA, false, false);
        this.addColuna("Serviço", 40, RelatorioRapido.ESQUERDA, false, false);
        this.addColuna("Situação Inicial", 10, RelatorioRapido.ESQUERDA, false,
                false);
        this.addColuna("Situação Final", 10, RelatorioRapido.ESQUERDA, false,
                false);
        this.addColuna("Data Início", 10, RelatorioRapido.ESQUERDA, false,
                false);
        this.addColuna("Data Fim", 10, RelatorioRapido.ESQUERDA, false,
                false);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection processarDados() {
        ArrayList<String> dados = new ArrayList<String>();
        try {
            List<AlteracaoDireitosItem> list = compararDasPessoasDoOrgaoNoPeriodo(
                    getCpOrgaoUsuario(), getDtInicio(), getDtFim());
            for (AlteracaoDireitosItem itm : list) {
                processarItem(itm, dados);
            }
        } catch (Exception e) {
            throw new Error(e);
        }
        // descomentar linha abaixo para testar no JUNIT
        // gerarArquivoDados(dados, "c:\\ArquivoDados.txt");
        return dados;
    }

    @SuppressWarnings("unused")
    private void gerarArquivoDados(ArrayList<String> dados, String nomeArq) {
        final int QUANTOS_CAMPOS = 4;
        File file = new File(nomeArq);
        try {
            Writer output = new BufferedWriter(new FileWriter(file));
            int contaCampos = 0;
            for (String campo : dados) {
                contaCampos++;
                output.write(campo);
                if (contaCampos < QUANTOS_CAMPOS) {
                    output.write("\t");

                } else {
                    output.write("\r\n");
                    contaCampos = 0;
                }
            }
            output.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Preenche os dados com as informações da configuração já formatados
     *
     * @param itm   - Item de alteração de direitos
     * @param dados - coleção de linhas do relatório
     */
    private void processarItem(AlteracaoDireitosItem itm, List<String> dados) {
        try {
            dados.add((itm.getPessoa() != null) ? itm.getPessoa().getDescricao() : "");
        } catch (Exception e) {
            dados.add("");
        }
        try {
            dados.add((itm.getServico() != null) ? itm.getServico().getDescricao() : "");
        } catch (Exception e) {
            dados.add("");
        }
        try {
            dados.add((itm.getSituacaoAntes() != null) ? itm.getSituacaoAntes().getDescr() : "-");
        } catch (Exception e) {
            dados.add("");
        }
        try {
            dados.add((itm.getSituacaoDepois() != null) ? itm.getSituacaoDepois().getDescr() : "-");
        } catch (Exception e) {
            dados.add("");
        }
        try {
            dados.add(itm.getInicio() != null ? printDate(itm.getInicio()) : "");
        } catch (Exception e) {
            dados.add("");
        }
        try {
            dados.add(itm.getFim() != null ? printDate(itm.getFim()) : "");
        } catch (Exception e) {
            dados.add("");
        }
    }

    @SuppressWarnings("unused")
    private String printDate(Date dte) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return df.format(dte);
    }

    private Date converteParaData(String p_strData) throws Exception {
        SimpleDateFormat formatter = new java.text.SimpleDateFormat(
                "dd/MM/yyyy hh:mm:ss");
        Date t_dteData = null;
        t_dteData = formatter.parse(p_strData);
        return t_dteData;
    }

    public static void main(String args[]) throws Exception {
    }

    /**
     * @return the dtInicio
     */
    public Date getDtInicio() {
        return dtInicio;
    }

    /**
     * @param dtInicio the dtInicio to set
     */
    public void setDtInicio(Date dtInicio) {
        this.dtInicio = dtInicio;
    }

    /**
     * @return the dtFim
     */
    public Date getDtFim() {
        return dtFim;
    }

    /**
     * @param dtFim the dtFim to set
     */
    public void setDtFim(Date dtFim) {
        this.dtFim = dtFim;
    }

    /**
     * @return the cpOrgaoUsuario
     */
    public CpOrgaoUsuario getCpOrgaoUsuario() {
        return cpOrgaoUsuario;
    }

    /**
     * @param cpOrgaoUsuario the cpOrgaoUsuario to set
     */
    public void setCpOrgaoUsuario(CpOrgaoUsuario cpOrgaoUsuario) {
        this.cpOrgaoUsuario = cpOrgaoUsuario;
    }

    public SortedSet<AlteracaoDireitosItem> obterDasPessoasDoOrgaoNaData(
            CpTipoDeConfiguracao tipo, List<DpPessoa> pessoas,
            List<CpServico> servicos, Date dtEvn) throws Exception {

        TreeSet<AlteracaoDireitosItem> lista = new TreeSet<>();
        for (DpPessoa pes : pessoas) {
            if (pes.ativaNaData(dtEvn)) {
                for (CpServico srv : servicos) {
                    AlteracaoDireitosItem item = gerar(tipo, null, pes, null,
                            null, srv, dtEvn, false);
                    lista.add(item);
                }
            }
        }
        return lista;
    }

    private AlteracaoDireitosItem gerar(CpTipoDeConfiguracao tipo,
                                               CpPerfil perfil, DpPessoa pessoa, DpLotacao lotacao,
                                               CpOrgaoUsuario orgao, CpServico servico, Date dtEvn, boolean isAntes)
            throws Exception {
        CpConfiguracao cfgFiltro = new CpConfiguracao();
        cfgFiltro.setCpGrupo(perfil);
        cfgFiltro.setDpPessoa(pessoa);
        cfgFiltro.setLotacao(lotacao);
        cfgFiltro.setOrgaoUsuario(orgao);
        cfgFiltro.setCpServico(servico);
        cfgFiltro.setCpTipoConfiguracao(tipo);
        CpConfiguracaoCache cache = conf.buscaConfiguracao(
                cfgFiltro, new int[0], dtEvn);

        CpConfiguracao cfg = null;
        if (Objects.nonNull(cache))
            cfg = dao.consultar(cache.idConfiguracao, CpConfiguracao.class, false);

        AlteracaoDireitosItem itm = new AlteracaoDireitosItem();
        itm.setServico(servico);
        itm.setPessoa(pessoa);
        if (isAntes) {
            itm.setCfgAntes(cfg);
        } else {
            itm.setCfgDepois(cfg);
        }
        return itm;
    }

    private List<AlteracaoDireitosItem> compararDasPessoasDoOrgaoNoPeriodo(
            CpOrgaoUsuario ou, Date dtAntes, Date dtDepois) throws Exception {
        List<DpPessoa> pessoas = (List<DpPessoa>) dao
                .consultarPorOrgaoUsuDpPessoaInclusiveFechadas(ou.getId());
        List<CpServico> servicos = dao.listarServicos();
        CpTipoDeConfiguracao tipo = CpTipoDeConfiguracao.UTILIZAR_SERVICO;

        List<AlteracaoDireitosItem> lista = new ArrayList<>();

        for (DpPessoa pes : pessoas) {
            for (CpServico srv : servicos) {
                AlteracaoDireitosItem itemAntes = gerar(tipo, null, pes, null, null, srv, dtAntes, true);
                AlteracaoDireitosItem itemDepois = gerar(tipo, null, pes, null, null, srv, dtDepois, false);

                if (itemAntes.getCfgAntes() != null || itemDepois.getCfgDepois() != null) {
                    AlteracaoDireitosItem item = new AlteracaoDireitosItem();
                    item.setPessoa(pes);
                    item.setServico(srv);
                    item.setCfgAntes(itemAntes.getCfgAntes());
                    item.setCfgDepois(itemDepois.getCfgDepois());
                    lista.add(item);
                }
            }
        }

        Collections.sort(lista);
        return lista;
    }
}
