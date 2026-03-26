package br.gov.jfrj.siga.gc.vraptor;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Result;
import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.vraptor.VraptorController;
import br.gov.jfrj.siga.vraptor.SigaObjects;


@RequestScoped
public class GcController extends VraptorController {

	public void assertAcesso(String pathServico) throws AplicacaoException {
		so.assertAcesso("GC:Módulo de Gestão de Conhecimento;" + pathServico);
	}
}
