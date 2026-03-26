package br.gov.jfrj.siga.sr.bl;

import br.gov.jfrj.siga.sr.model.SrConfiguracaoBL;
import br.gov.jfrj.siga.sr.model.SrDao;
import br.gov.jfrj.siga.sr.model.SrSolicitacao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

@ApplicationScoped
public class SrBL {
    @Inject
    private EntityManager em;

    @Inject
    private SrDao dao;

    @Inject
    private SrConfiguracaoBL conf;

    private static final Long SEM_NUMERACAO = 0L;
    private static final Long NUMERACAO_INICIAL = 1L;

    public Long getProximoNumeroSolicitacao(SrSolicitacao solicitacao) {
        if (solicitacao.getOrgaoUsuario() == null || solicitacao.getAnoEmissao() == null)
            return SEM_NUMERACAO;

        TypedQuery<Long> query = em.createQuery("select max(numSolicitacao) + 1 from SrSolicitacao "
                + "where orgaoUsuario.idOrgaoUsu = :idOrgaoUsu and year(dtReg) = :ano and numSequencia is null and numSolicitacao < 90000", Long.class);
        query.setParameter("idOrgaoUsu", solicitacao.getOrgaoUsuario().getIdOrgaoUsu());
        query.setParameter("ano", Integer.valueOf(solicitacao.getAnoEmissao()));
        Long resultado = query.getSingleResult();
        if (resultado == null)
            return NUMERACAO_INICIAL;
        return resultado;
    }

}
