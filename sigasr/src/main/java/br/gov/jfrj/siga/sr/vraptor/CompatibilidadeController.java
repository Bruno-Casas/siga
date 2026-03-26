package br.gov.jfrj.siga.sr.vraptor;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Result;
import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.sr.model.SrSolicitacao;
import br.gov.jfrj.siga.sr.validator.SrValidator;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Controller
@Path("solicitacao")
public class CompatibilidadeController extends SrController {
    
    @Get
    @Path("/exibir")
    public void exibir(Long id) throws Exception {
    	if (id == null || id <=0)
    		throw new AplicacaoException("Número não informado");
    	SrSolicitacao sol = SrSolicitacao.AR.findById(id);
    	result.forwardTo(SolicitacaoController.class).exibir(sol.getSiglaCompacta(), true, false);
    }

}
