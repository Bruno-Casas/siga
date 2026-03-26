package br.gov.jfrj.siga.vraptor;

import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.base.TipoResponsavelEnum;
import br.gov.jfrj.siga.cp.model.enm.CpSituacaoDeConfiguracaoEnum;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.dp.DpVisualizacao;
import br.gov.jfrj.siga.ex.*;
import br.gov.jfrj.siga.ex.bl.ExBL;
import br.gov.jfrj.siga.ex.bl.ExCompetenciaBL;
import br.gov.jfrj.siga.ex.bl.ExConfiguracaoBL;
import br.gov.jfrj.siga.ex.logic.ExPodeAcessarDocumento;
import br.gov.jfrj.siga.ex.model.enm.ExTipoDeConfiguracao;
import br.gov.jfrj.siga.ex.util.NivelDeAcessoUtil;
import br.gov.jfrj.siga.hibernate.ExDao;
import br.gov.jfrj.siga.util.ExProcessadorModelo;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class ExController extends VraptorController {

    @Inject
    protected HttpServletResponse response;

    @Inject
    protected ServletContext context;

    @Inject
    protected ExDao dao;

    @Inject
    protected ExBL bl;

    @Inject
    protected ExConfiguracaoBL conf;

    @Inject
    protected ExCompetenciaBL comp;

    @Inject
    private NivelDeAcessoUtil nivelDeAcessoUtil;

    @PostConstruct
    private void init() {
        if (bl.getProcessadorModeloJsp() == null) {
            bl.setProcessadorModeloJsp(new ExProcessadorModelo());
        }
    }

    protected void verificaNivelAcesso(ExMobil mob) {
        if (!comp.pode(ExPodeAcessarDocumento.class, getTitular(), getLotaTitular(), mob)) {
            throw new AplicacaoException("Acesso ao documento " + mob.getSigla() + " permitido somente a usuários autorizados. (" + getTitular().getSigla()
                    + "/" + getLotaTitular().getSiglaCompleta() + ")");
        }
    }

    protected String getNomeServidor() {
        return getRequest().getServerName();
    }

    protected String getNomeServidorComPorta() {
        if (getRequest().getServerPort() > 0)
            return getRequest().getServerName() + ":" + getRequest().getServerPort();
        return getRequest().getServerName();
    }

    protected List<ExNivelAcesso> getListaNivelAcesso(ExTipoDocumento exTpDoc, ExFormaDocumento forma, ExModelo exMod, ExClassificacao classif) {
        return nivelDeAcessoUtil.getListaNivelAcesso(exTpDoc, forma, exMod,
                classif, getTitular(), getLotaTitular());
    }

    protected ExNivelAcesso getNivelAcessoDefault(final ExTipoDocumento exTpDoc, final ExFormaDocumento forma, final ExModelo exMod, final ExClassificacao classif) {
        final Date dt = dao.consultarDataEHoraDoServidor();

        final ExConfiguracao config = new ExConfiguracao();
        config.setDpPessoa(getTitular());
        config.setLotacao(getLotaTitular());
        config.setExTipoDocumento(exTpDoc);
        config.setExFormaDocumento(forma);
        config.setExModelo(exMod);
        config.setExClassificacao(classif);
        config.setCpTipoConfiguracao(ExTipoDeConfiguracao.NIVEL_DE_ACESSO);
        config.setCpSituacaoConfiguracao(CpSituacaoDeConfiguracaoEnum.DEFAULT);
        ExConfiguracaoCache exConfig;

        try {
            exConfig = (ExConfiguracaoCache) conf
                    .buscaConfiguracao(config, new int[]{ExConfiguracaoBL.NIVEL_ACESSO}, dt);
        } catch (Exception e) {
            exConfig = null;
        }

        if (exConfig != null) {
            return cpDao.consultar(exConfig.exNivelAcesso, ExNivelAcesso.class, false);
        }

        return null;
    }

    @SuppressWarnings("static-access")
    protected String getDescrDocConfidencial(ExDocumento doc) {
        return bl.descricaoSePuderAcessar(doc, getTitular(), getLotaTitular());
    }

    protected List<ExTipoDocumento> getTiposDocumento() throws AplicacaoException {
        return dao.listarExTiposDocumento();
    }

    protected ExDocumento daoDoc(long id) {
        return dao.consultar(id, ExDocumento.class, false);
    }

    protected ExMovimentacao daoMov(long id) {
        return dao.consultar(id, ExMovimentacao.class, false);
    }

    protected ExMobil daoMob(long id) {
        return dao.consultar(id, ExMobil.class, false);
    }

    protected List<ExEstadoDoc> getEstados() throws AplicacaoException {
        return dao.listarExEstadosDoc();
    }

    protected Map<Integer, String> getListaTipoResp() {
        return TipoResponsavelEnum.getListaMatriculaLotacao();
    }

    protected List<String> getListaAnos() {
        final ArrayList<String> lst = new ArrayList<String>();
        // map.add("", "[Vazio]");
        final Calendar cal = Calendar.getInstance();
        for (int ano = cal.get(Calendar.YEAR); ano >= 1990; ano--)
            lst.add(Integer.toString(ano));
        return lst;
    }

    protected void assertAcesso(String pathServico) throws AplicacaoException {
        super.assertAcesso("DOC:Módulo de Documentos;" + pathServico);
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    protected ServletContext getContext() {
        return context;
    }

    protected Boolean podeVisualizarDocumento(final ExMobil mob, DpPessoa titular, Long idVisualizacao) throws Exception {
        return podeVisualizarDocumento(getCadastrante(), titular, idVisualizacao, mob);
    }

    private boolean podeVisualizarDocumento(DpPessoa cadastrante, DpPessoa titular, Long idVisualizacao, final ExMobil mob) throws Exception {
        boolean retorno = Boolean.FALSE;

        if (conf.podePorConfiguracao(
                cadastrante, cadastrante.getLotacao(), ExTipoDeConfiguracao.DELEGAR_VISUALIZACAO
        )
        ) {
            if (idVisualizacao != null && !idVisualizacao.equals(0L)) {
                DpVisualizacao vis = dao.consultar(idVisualizacao, DpVisualizacao.class, false);

                if (vis.getDelegado().equals(titular)) {
                    if (comp.pode(ExPodeAcessarDocumento.class, vis.getTitular(), vis.getTitular().getLotacao(),
                            mob)) {
                        retorno = Boolean.TRUE;
                    }
                }
            }
        }

        return retorno;
    }
}
