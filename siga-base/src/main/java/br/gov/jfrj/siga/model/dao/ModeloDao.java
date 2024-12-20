/*-*****************************************************************************
 * Copyright (c) 2006 - 2011 SJRJ.
 *
 *     This file is part of SIGA.
 *
 *     SIGA is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     SIGA is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with SIGA.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/*
 * Criado em  01/12/2005
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package br.gov.jfrj.siga.model.dao;

import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.model.ContextoPersistencia;
import org.jboss.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

public abstract class ModeloDao {

    private static final Logger log = Logger.getLogger(ModeloDao.class);

    protected String cacheRegion = null;

    private BancoDeDados banco = null;

    private static final ThreadLocal<ModeloDao> threadDao = new ThreadLocal<>();

    protected ModeloDao() {
    }

    protected static <T extends ModeloDao> T getInstance(Class<T> clazz) {
        T dao = null;

        try {
            dao = (T) ModeloDao.threadDao.get();
        } catch (Exception e) {
            // quando ocorrer algum problema, recriar o dao.
            System.out.println(e.getStackTrace());
        }

        // Cria um novo Dao se ainda não houver
        if (dao == null) {
            try {
                dao = clazz.newInstance();
            } catch (Exception e) {
                throw new Error(e);
            }
            ModeloDao.threadDao.set(dao);
        }
        return dao;
    }

    public synchronized static void freeInstance() {
        final ModeloDao dao = ModeloDao.threadDao.get();

        // fecha o dao e a seï¿½ï¿½o do hibernate
        if (dao != null) {
            ModeloDao.threadDao.remove();
        }
    }

    public <T> T consultar(final Serializable id, Class<T> clazz, final boolean lock) {

        if (id == null) {
            log.warn("[aConsultar] - O ID recebido para efetuar a consulta é nulo. ID: " + id);
            throw new IllegalArgumentException("O identificador do objeto é nulo ou inválido.");
        }
        T entidade;
        entidade = (T) em().find(clazz, id);
        return entidade;

    }

    public void excluir(final Object entidade) {
        if (em() != null)
            em().remove(entidade);
    }

    public EntityManager em() {
        return ContextoPersistencia.em();
    }

    public void descarregar() {
        ContextoPersistencia.flushTransaction();
    }

    public <T> T gravar(final T entidade) {
        if (em() != null)
            em().persist(entidade);
        return entidade;
    }

    /**
     * Use this inside subclasses as a convenience method.
     */
    protected <T> List<T> findByCriteria(Class<T> clazz) {
        return findAndCacheByCriteria(null, clazz);
    }

    protected <T> List<T> findAndCacheByCriteria(String cacheRegion, Class<T> clazz) {
        final CriteriaBuilder criteriaBuilder = em().getCriteriaBuilder();
        CriteriaQuery<T> crit = criteriaBuilder.createQuery(clazz);

        Root<T> root = crit.from(clazz);
        crit.select(root);
        TypedQuery<T> query = em().createQuery(crit);
        if (cacheRegion != null) {
            query.setHint("org.hibernate.cacheable", true);
            query.setHint("org.hibernate.cacheRegion", cacheRegion);
        }
        return query.getResultList();
    }

    public <T> T consultarPorSigla(final T exemplo) {
        return null;
    }

    public String getCacheRegion() {
        return cacheRegion;
    }

    public void setCacheRegion(String cacheRegion) {
        this.cacheRegion = cacheRegion;
    }

    public static void iniciarTransacao() {
    }

    public static void commitTransacao() throws AplicacaoException {
    }

    public static void rollbackTransacao() {
    }

    /**
     * @return true se a sessão do Hibernate não for nula e estiver aberta.
     */
    public boolean sessaoEstahAberta() {
        EntityManager em = em();
        return em != null && em.isOpen();
    }

    /**
     * @return true se a transacao da sessão do Hibernate estiver ativa
     */
    public boolean transacaoEstaAtiva() {
        EntityManager em = em();
        return em != null && em.isOpen() && em.getTransaction() != null && em.getTransaction().isActive();
    }

    /**
     * @return Tipo do banco de dados utilizado
     */
    private BancoDeDados getBanco() {
        if (banco != null)
            return banco;
        String dialect = System.getProperty("siga.hibernate.dialect");
        if (dialect != null && dialect.contains(BancoDeDados.MYSQL.getDescr()))
            banco = BancoDeDados.MYSQL;
        if (dialect != null && dialect.contains(BancoDeDados.ORACLE.getDescr()))
            banco = BancoDeDados.ORACLE;
        if (dialect != null && dialect.contains(BancoDeDados.POSTGRESQL.getDescr()))
            banco = BancoDeDados.POSTGRESQL;
        return banco;
    }

    public boolean isOracle() {
        return getBanco().equals(BancoDeDados.ORACLE);
    }

    public boolean isMySQL() {
        return getBanco().equals(BancoDeDados.MYSQL);
    }

    public boolean isPostgreSQL() {
        return getBanco().equals(BancoDeDados.POSTGRESQL);
    }
}
