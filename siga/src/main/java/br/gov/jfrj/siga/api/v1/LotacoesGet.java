package br.gov.jfrj.siga.api.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.crivano.swaggerservlet.SwaggerException;

import br.gov.jfrj.siga.api.v1.ISigaApiV1.ILotacoesGet;
import br.gov.jfrj.siga.api.v1.ISigaApiV1.Lotacao;
import br.gov.jfrj.siga.api.v1.ISigaApiV1.Orgao;
import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.base.util.Texto;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.dp.dao.CpOrgaoUsuarioDaoFiltro;
import br.gov.jfrj.siga.dp.dao.DpLotacaoDaoFiltro;

import javax.inject.Inject;

public class LotacoesGet implements ILotacoesGet {
	
	@Inject
	CpDao dao;
	
	@Override
	public void run(Request req, Response resp, SigaApiV1Context ctx) throws Exception {
		if ((((req.texto != null ? 1 : 0) + (req.hisIdIni != null ? 1 : 0)
				+ (req.siglaOrgaoQuery != null ? 1 : 0)) > 1)) {
			throw new AplicacaoException("Pesquisa permitida somente por um dos argumentos.");
		}

		if (req.siglaOrgaoQuery != null && !req.siglaOrgaoQuery.isEmpty()) {
			resp.list = pesquisarPorOrgao(req, resp);
			return;
		}

		if (req.texto != null && !req.texto.isEmpty()) {
			resp.list = pesquisarPorTexto(req, resp);
			return;
		}

		if (req.hisIdIni != null && !req.hisIdIni.isEmpty()) {
			resp.list = pesquisarLotacaoAtualPorIdIni(req, resp);
			return;
		}

		throw new AplicacaoException("Não foi fornecido nenhum parâmetro.");
	}

	private List<Lotacao> pesquisarPorOrgao(Request req, Response resp) {
		final DpLotacaoDaoFiltro flt = new DpLotacaoDaoFiltro();
		final CpOrgaoUsuarioDaoFiltro fltOrg = new CpOrgaoUsuarioDaoFiltro();
		fltOrg.setSigla(req.siglaOrgaoQuery);
		CpOrgaoUsuario org = (CpOrgaoUsuario) dao.consultarPorSigla(fltOrg);
		if (org != null)
			flt.setIdOrgaoUsu(Long.valueOf(org.getId()));
		else
			throw new AplicacaoException("Órgão não encontrado.");

		List<DpLotacao> l;
		l = dao.consultarLotacaoPorOrgao(org);
		return l.stream().map(this::lotacaoToResultadoPesquisa).collect(Collectors.toList());
	}

	private List<Lotacao> pesquisarPorTexto(Request req, Response resp) {
		final DpLotacaoDaoFiltro flt = new DpLotacaoDaoFiltro();
		flt.setNome(Texto.removeAcentoMaiusculas(req.texto));
		List<DpLotacao> l;
		l = dao.consultarPorFiltro(flt);
		return l.stream().map(this::lotacaoToResultadoPesquisa).collect(Collectors.toList());
	}

	private List<Lotacao> pesquisarLotacaoAtualPorIdIni(Request req, Response resp) throws SwaggerException {
		try {
			DpLotacao Lotacao = new DpLotacao();
			Lotacao.setHisIdIni(Long.valueOf(req.hisIdIni));
			DpLotacao lotacaoAtual = dao.obterLotacaoAtual(Lotacao);
			List<Lotacao> l = new ArrayList<>();
			l.add(lotacaoToResultadoPesquisa(lotacaoAtual));
			return l;
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw e;
		}
	}

	private Lotacao lotacaoToResultadoPesquisa(DpLotacao lota) {
		Lotacao rp = new Lotacao();
		Orgao orgao = new Orgao();

		rp.siglaLotacao = lota.getSigla();
		rp.idLotacao = lota.getId().toString();
		rp.hisIdIni = lota.getHisIdIni().toString();
		rp.nome = lota.getNomeLotacao();
		rp.sigla = lota.getSiglaCompleta();
		// Orgao Pessoa
		CpOrgaoUsuario o = lota.getOrgaoUsuario();
		orgao.idOrgao = o.getId().toString();
		orgao.nome = o.getNmOrgaoAI();
		orgao.sigla = o.getSigla();

		rp.orgao = orgao;
		return rp;
	}

	@Override
	public String getContext() {
		return "selecionar lotações";
	}

}
