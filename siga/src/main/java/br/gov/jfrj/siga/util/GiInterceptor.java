package br.gov.jfrj.siga.util;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.jpa.JPATransactionInterceptor;
import br.com.caelum.vraptor.validator.Validator;
import br.gov.jfrj.siga.cp.bl.CpConfiguracaoBL;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.model.ContextoPersistencia;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequestScoped
@Intercepts(before = JPATransactionInterceptor.class)
public class GiInterceptor implements Interceptor {

    private final EntityManager manager;
    @Inject
    private CpConfiguracaoBL conf;

    /**
     * @deprecated CDI eyes only
     */
    protected GiInterceptor() {
        this(null, null, null, null, null);
    };

    @Inject
    public GiInterceptor(EntityManager manager, Validator validator, ServletContext context, HttpServletRequest request,
                         HttpServletResponse response) {
        this.manager = manager;
    }

    public void intercept(InterceptorStack stack, ControllerMethod method, Object instance) {

        ContextoPersistencia.setEntityManager(this.manager);

        try {
            conf.limparCacheSeNecessario();
        } catch (Exception e1) {
            throw new RuntimeException(
                    "Não foi possível atualizar o cache de configurações", e1);
        }

        stack.next(method, instance);
    }

    public boolean accepts(ControllerMethod method) {
        return true; // Will intercept all requests
    }
}