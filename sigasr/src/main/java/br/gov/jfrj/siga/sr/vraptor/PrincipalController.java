package br.gov.jfrj.siga.sr.vraptor;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.validator.Validator;
import br.com.caelum.vraptor.view.Results;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.sr.model.SrSolicitacao;
import br.gov.jfrj.siga.sr.validator.SrValidator;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Controller
public class PrincipalController  extends SrController{

	@Path("/app/principal")
	public void principal() throws Exception {
		//Principal
		result.redirectTo(SolicitacaoController.class).buscar(null, null, false, false);
	}
	
	
	@Path("/public/app/selecionar")
    public void selecionarPublico(String sigla, String matricula) throws Exception {
    	try {
    		SrSolicitacao sol = new SrSolicitacao();
    		if (matricula != null) {
    			DpPessoa pes = cpDao.getPessoaFromSigla(matricula);
    			if (pes != null)
    				sol.setLotaTitular(pes.getLotacao());
    		}
    		sol = (SrSolicitacao) sol.selecionar(sigla);
        
	        if (sol != null) {
	        	result.use(Results.http()).body("1;" + sol.getId() + ";" + sol.getSigla() + ";" + "/sigasr/app/solicitacao/exibir/" + sol.getSiglaCompacta());
	        	return;
	        }
    	} catch (Exception ex) {
    		
    	}
    	result.use(Results.http()).body("0");
    }
}