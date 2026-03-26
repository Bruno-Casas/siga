package br.gov.jfrj.siga.vraptor;


import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.base.Prop;


@Controller
@Path("app/pin")
public class PinController extends VraptorController {
		
	@Get
	@Path("/cadastro")
	public void cadastro() throws Exception {	
		
		if (!cpComp.podeSegundoFatorPin( getCadastrante(), getLotaCadastrante())) {
			throw new AplicacaoException("PIN como Segundo Fator de Autenticação: Acesso não permitido a esse recurso.");
		}
		result.include("baseTeste", Prop.getBool("/siga.base.teste"));
	}
	
	@Get
	@Path("/troca")
	public void troca() throws Exception {	
		if (!cpComp.podeSegundoFatorPin( getCadastrante(), getLotaCadastrante())) {
			throw new AplicacaoException("PIN como Segundo Fator de Autenticação: Acesso não permitido a esse recurso.");
		}
		result.include("baseTeste", Prop.getBool("/siga.base.teste"));
	}
	
	@Get
	@Path("/reset")
	public void reset() throws Exception {	
		if (!cpComp.podeSegundoFatorPin( getCadastrante(), getLotaCadastrante())) {
			throw new AplicacaoException("PIN como Segundo Fator de Autenticação: Acesso não permitido a esse recurso.");
		}
		result.include("baseTeste", Prop.getBool("/siga.base.teste"));
	}
			

}
