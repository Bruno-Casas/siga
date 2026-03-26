package br.gov.jfrj.siga.model.dao;

import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.base.ServerClock;
import br.gov.jfrj.siga.model.Historico;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public abstract class ModeloDao {

    @Inject
    ServerClock requestClock;

    @Inject
    protected EntityManager em;

    private static final Logger log = Logger.getLogger(ModeloDao.class);

    protected ModeloDao() {
    }

    public <T> T consultar(final Serializable id, Class<T> clazz, final boolean lock) {

        if (id == null) {
            log.warn("[aConsultar] - O ID recebido para efetuar a consulta é nulo. ID: " + id);
            throw new IllegalArgumentException("O identificador do objeto é nulo ou inválido.");
        }
        T entidade;
        entidade = (T) em.find(clazz, id);
        return entidade;

    }

    public void excluir(final Object entidade) {
        if (em != null)
            em.remove(entidade);
    }

    public <T> T gravar(final T entidade) {
        if (em != null)
            em.persist(entidade);
        return entidade;
    }

    public <T extends Historico<T>> void finalizar(T entidade) throws Exception {
        entidade.setHisDtFim(new Date());
        this.gravar(entidade);
    }

    protected <T> List<T> findByCriteria(Class<T> clazz) {
        return findAndCacheByCriteria(null, clazz);
    }

    protected <T> List<T> findAndCacheByCriteria(String cacheRegion, Class<T> clazz) {
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> crit = criteriaBuilder.createQuery(clazz);

        Root<T> root = crit.from(clazz);
        crit.select(root);
        TypedQuery<T> query = em.createQuery(crit);
        if (cacheRegion != null) {
            query.setHint("org.hibernate.cacheable", true);
            query.setHint("org.hibernate.cacheRegion", cacheRegion);
        }
        return query.getResultList();
    }

    public <T> T consultarPorSigla(final T exemplo) {
        return null;
    }

    public Date dt() throws AplicacaoException {
        return consultarDataEHoraDoServidor();
    }

    public Date consultarDataEHoraDoServidor() {
        return requestClock.getDataAtual();
    }
}
