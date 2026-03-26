package br.gov.jfrj.siga.vraptor;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import br.gov.jfrj.siga.cp.bl.CpCompetenciaBL;
import br.gov.jfrj.siga.cp.bl.CpConfiguracaoBL;
import br.gov.jfrj.siga.dp.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.jfrj.siga.acesso.ConheceUsuario;
import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.cp.CpIdentidade;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.dp.dao.CpOrgaoUsuarioDaoFiltro;
import br.gov.jfrj.siga.dp.dao.DpLotacaoDaoFiltro;
import br.gov.jfrj.siga.dp.dao.DpPessoaDaoFiltro;
import br.gov.jfrj.siga.model.ContextoPersistencia;

@RequestScoped
public class SigaObjects implements ConheceUsuario {
	private static final Log log = LogFactory.getLog(SigaObjects.class);

	public static Log getLog() {
		return SigaObjects.log;
	}

	@Inject
	private HttpServletRequest request;

	private DpPessoa cadastrante;

	private DpPessoa titular;

	private DpLotacao lotaTitular;

	private CpIdentidade identidadeCadastrante;

	private String mensagem;

	@Inject
	private CpDao dao;

	@Inject
	private CpConfiguracaoBL conf;

	@Inject
	private CpCompetenciaBL comp;

	public SigaObjects() throws Exception {
		this(null);
	}

	@Inject
	public SigaObjects(HttpServletRequest request) throws Exception {
		super();
		this.request = request;
		carregaPerfil();
	}

	public void assertAcesso(String pathServico) throws AplicacaoException {
		String servico = "SIGA:Sistema Integrado de Gestão Administrativa;"
				+ pathServico;
		if (!conf.podeUtilizarServicoPorConfiguracao(getTitular(),
						getLotaTitular(), servico)) {

			String siglaUsuario = getTitular() == null ? "Indefinido"
					: getTitular().getSigla();
			String siglaLotacao = getLotaTitular() == null ? "Indefinida"
					: getLotaTitular().getSiglaCompleta();

			throw new AplicacaoException("Acesso negado. Serviço: '" + servico
					+ "' usuário: " + siglaUsuario + " lotação: "
					+ siglaLotacao);
		}
	}

	protected void carregaPerfil() throws Exception {
		if (ContextoPersistencia.getUserPrincipal() == null)
			return;

		String principal = ContextoPersistencia.getUserPrincipal();

		CpIdentidade id = dao.consultaIdentidadeCadastrante(principal, true);
		carregarUsuario(id);
	}

	public DpPessoa getCadastrante() {
		return cadastrante;
	}

	public CpIdentidade getIdentidadeCadastrante() {
		return identidadeCadastrante;
	}

	public DpLotacao getLotaTitular() {
		return lotaTitular;
	}

	public String getMensagem() {
		return mensagem;
	}

	public List<DpSubstituicao> getMeusTitulares() throws Exception {
		if (getCadastrante() == null)
			return null;
		DpSubstituicao dpSubstituicao = new DpSubstituicao();
		dpSubstituicao.setSubstituto(getCadastrante());
		dpSubstituicao.setLotaSubstituto(getCadastrante().getLotacao());
		return dao.consultarSubstituicoesPermitidas(dpSubstituicao);
	}

	public List<DpVisualizacao> getMeusDelegados() throws Exception {
		if (getCadastrante() == null)
			return null;
		DpVisualizacao dpVisualizacao = new DpVisualizacao();
		dpVisualizacao.setDelegado(getCadastrante());
		return dao.consultarVisualizacoesPermitidas(dpVisualizacao);
	}

	public DpPessoa getTitular() {
		return titular;
	}

	public void setCadastrante(final DpPessoa cadastrante) {
		this.cadastrante = cadastrante;
	}

	public void setIdentidadeCadastrante(CpIdentidade identidadeCadastrante) {
		this.identidadeCadastrante = identidadeCadastrante;
	}

	public void setLotaTitular(DpLotacao lotaTitular) {
		this.lotaTitular = lotaTitular;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public void setTitular(DpPessoa titular) {
		this.titular = titular;
	}

	public DpPessoa daoPes(long id) {
		return dao.consultar(id, DpPessoa.class, false);
	}

	public DpPessoa daoPes(String sigla) {
		DpPessoaDaoFiltro flt = new DpPessoaDaoFiltro();
		flt.setSigla(sigla);
		return (DpPessoa) dao.consultarPorSigla(flt);
	}

	public DpLotacao daoLot(long id) {
		return dao.consultar(id, DpLotacao.class, false);
	}

	public DpLotacao daoLot(String sigla) {
		DpLotacaoDaoFiltro flt = new DpLotacaoDaoFiltro();
		flt.setSigla(sigla);

		return (DpLotacao) dao.consultarPorSigla(flt);
	}

	public CpOrgaoUsuario daoOU(String sigla) {
		CpOrgaoUsuarioDaoFiltro fltOrgao = new CpOrgaoUsuarioDaoFiltro();
		fltOrgao.setSigla(sigla);
		return (CpOrgaoUsuario) dao.consultarPorSigla(fltOrgao);
	}

	private void carregarUsuario(CpIdentidade id)
			throws AplicacaoException, SQLException {
		Date dt = dao.consultarDataEHoraDoServidor();
		if (!id.ativaNaData(dt)) {
			CpIdentidade idAtual = dao.obterIdentidadeAtual(id);
			if (!id.getId().equals(idAtual.getId())) {
				dao.invalidarCache(id);
				id = idAtual;
			}
			if (!id.ativaNaData(dt))
				throw new AplicacaoException("O acesso não será permitido porque identidade está inativa desde '"+ id.getDtExpiracaoDDMMYYYY() + "'.");
		}
		if (comp.isIdentidadeBloqueada(id)) {
			throw new AplicacaoException("O acesso não será permitido porque esta identidade está bloqueada.");
		}

		setIdentidadeCadastrante(id);
		setCadastrante(dao.obterPessoaAtual(id.getDpPessoa()));

		CpPersonalizacao per = dao.consultarPersonalizacao(getCadastrante());

		if ((per != null)
				&& ((per.getPesSubstituindo() != null) || (per
				.getLotaSubstituindo() != null))) {

			DpSubstituicao dpSubstituicao = new DpSubstituicao();
			dpSubstituicao.setSubstituto(getCadastrante());
			dpSubstituicao.setLotaSubstituto(getCadastrante().getLotacao());

			setTitular(per.getPesSubstituindo());
			setLotaTitular(per.getLotaSubstituindo());

		}

		if (getLotaTitular() == null && getTitular() != null)
			setLotaTitular(getTitular().getLotacao());
		if (getTitular() == null)
			setTitular(getCadastrante());
		if (getLotaTitular() == null)
			setLotaTitular(getTitular().getLotacao());
	}
}
