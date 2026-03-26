package br.gov.jfrj.siga.sr.interceptor;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;
import br.gov.jfrj.siga.model.ContextoPersistencia;
import br.gov.jfrj.siga.sr.model.SrConfiguracaoBL;

//@RequestScoped
//@Intercepts(before = { 	InterceptorMethodParametersResolver.class, JPATransactionInterceptor.class })
public class ContextInterceptor  {
	@Inject
	private SrConfiguracaoBL conf;

	private EntityManager em;
	
	/**
	 * @deprecated CDI eyes only
	 */
	public ContextInterceptor() {
		super();
	}
	
//	@Inject
	public ContextInterceptor(EntityManager em, Result result)  {
		this.em = em;
	}

//	@Accepts
	public boolean accepts(ControllerMethod method) {
		return true;
	}

//	@AroundCall
	public void intercept(SimpleInterceptorStack stack) throws Exception  {
		ContextoPersistencia.setEntityManager(em);
		conf.limparCacheSeNecessario();
		stack.next();
	}

}