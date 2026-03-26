package br.gov.jfrj.siga.vraptor;

import br.gov.jfrj.siga.base.AplicacaoException;

public class GiControllerSupport extends VraptorController {

	protected void assertAcesso(String pathServico) throws AplicacaoException {
		super.assertAcesso("GI:Módulo de Gestão de Identidade;" + pathServico);
	}

}
