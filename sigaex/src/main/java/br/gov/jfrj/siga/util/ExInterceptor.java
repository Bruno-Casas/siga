package br.gov.jfrj.siga.util;

import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.util.jpa.JPATransactionInterceptor;
import br.gov.jfrj.siga.ex.bl.CurrentRequest;
import br.gov.jfrj.siga.ex.bl.Ex;
import br.gov.jfrj.siga.ex.bl.RequestInfo;
import br.gov.jfrj.siga.hibernate.ExDao;
import br.gov.jfrj.siga.model.ContextoPersistencia;
import br.gov.jfrj.siga.model.dao.ModeloDao;

@Component
@Intercepts(before = JPATransactionInterceptor.class)
public class ExInterceptor implements Interceptor {

	private final EntityManager manager;
	private final Validator validator;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletContext context;

	public ExInterceptor(EntityManager manager, Validator validator,
			ServletContext context, HttpServletRequest request,
			HttpServletResponse response) {
		this.manager = manager;
		this.validator = validator;
		this.request = request;
		this.response = response;
		this.context = context;
	}

	// private final EntityManager manager;
	// private final Validator validator;
	//
	// public JPATransactionInterceptor(EntityManager manager, Validator
	// validator) {
	// this.manager = manager;
	// this.validator = validator;
	// }
	//
	// public void intercept(InterceptorStack stack, ResourceMethod method,
	// Object instance) {
	// EntityTransaction transaction = null;
	// try {
	// transaction = manager.getTransaction();
	// transaction.begin();
	//
	// stack.next(method, instance);
	//
	// if (!validator.hasErrors() && transaction.isActive()) {
	// transaction.commit();
	// }
	// } finally {
	// if (transaction != null && transaction.isActive()) {
	// transaction.rollback();
	// }
	// }
	// }

	public void intercept(InterceptorStack stack, ResourceMethod method,
			Object instance) {

		// EntityManager em = ExStarter.emf.createEntityManager();

		ContextoPersistencia.setEntityManager(this.manager);

		// Inicializa????o padronizada
		CurrentRequest.set(new RequestInfo(this.context, this.request,
				this.response));

		ModeloDao.freeInstance();
		ExDao.getInstance();
		try {
			Ex.getInstance().getConf().limparCacheSeNecessario();
		} catch (Exception e1) {
			throw new RuntimeException(
					"N??o foi poss??vel atualizar o cache de configura????es", e1);
		}

		try {
			stack.next(method, instance);
		} catch (Exception e) {
			throw new InterceptionException(e);
		} finally {
			ContextoPersistencia.setEntityManager(null);
		}
	}

	public boolean accepts(ResourceMethod method) {
		return true; // Will intercept all requests
	}
}