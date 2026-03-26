package br.gov.jfrj.siga.api.v1;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import br.gov.jfrj.siga.context.ApiContextSupport;
import br.gov.jfrj.siga.cp.bl.CpConfiguracaoBL;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.util.SigaStarter;

public class SigaApiV1Context extends ApiContextSupport {

	@Inject
	private CpDao dao;

	@Inject
	private CpConfiguracaoBL conf;


	public void atualizarCacheDeConfiguracoes() throws Exception {
		conf.limparCacheSeNecessario();
	}

	public CpDao inicializarDao() {
		return dao;
	}

	public EntityManager criarEntityManager() {
		return SigaStarter.emf.createEntityManager();
	}

}
