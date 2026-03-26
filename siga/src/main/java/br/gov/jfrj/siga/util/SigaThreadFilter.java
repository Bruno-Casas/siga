package br.gov.jfrj.siga.util;

import br.gov.jfrj.siga.cp.bl.CpConfiguracaoBL;
import br.gov.jfrj.siga.model.ContextoPersistencia;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.*;
import java.io.IOException;

public class SigaThreadFilter implements Filter {

    @Inject
    private CpConfiguracaoBL conf;

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        EntityManager em = SigaStarter.emf.createEntityManager();
        ContextoPersistencia.setEntityManager(em);

        try {
            conf.limparCacheSeNecessario();
        } catch (Exception e1) {
            throw new RuntimeException(
                    "Não foi possível atualizar o cache de configurações", e1);
        }

        em.getTransaction().begin();

        try {
            chain.doFilter(request, response);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();

            throw new ServletException(e);
        } finally {
            em.close();
            ContextoPersistencia.setEntityManager(null);
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub

    }
}