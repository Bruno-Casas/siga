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
package br.gov.jfrj.siga.wf.dao;

import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.cp.model.enm.ITipoDeConfiguracao;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.model.ContextoPersistencia;
import br.gov.jfrj.siga.model.Historico;
import br.gov.jfrj.siga.model.dao.ModeloDao;
import br.gov.jfrj.siga.wf.model.*;
import br.gov.jfrj.siga.wf.util.SiglaUtils;
import br.gov.jfrj.siga.wf.util.SiglaUtils.SiglaDecodificada;
import br.gov.jfrj.siga.wf.util.WfDefinicaoDeProcedimentoDaoFiltro;
import br.gov.jfrj.siga.wf.util.WfProcedimentoDaoFiltro;
import br.gov.jfrj.siga.wf.util.WfTarefa;
import org.jboss.logging.Logger;

import javax.enterprise.inject.Specializes;
import javax.persistence.PrePersist;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;

/**
 * Classe que representa o DAO do sistema de workflow.
 */
@Specializes
public class WfDao extends CpDao implements com.crivano.jflow.Dao<WfProcedimento> {

    public static final String CACHE_WF = "wf";

    private static final Logger log = Logger.getLogger(WfDao.class);

    public WfDefinicaoDeProcedimento consultarWfDefinicaoDeProcedimentoPorNome(String nome) {
        CriteriaQuery<WfDefinicaoDeProcedimento> q = cb().createQuery(WfDefinicaoDeProcedimento.class);
        Root<WfDefinicaoDeProcedimento> c = q.from(WfDefinicaoDeProcedimento.class);
        q.select(c);
        q.where(cb().equal(c.get("nome"), nome), cb().equal(c.get("hisAtivo"), 1));
        try {
            return em.createQuery(q).getSingleResult();
        } catch (Exception ex) {
            throw new RuntimeException("Não foi possível localizar definição de procedimento com nome '" + nome + "'",
                    ex);
        }
    }

    public List<WfDefinicaoDeProcedimento> consultarWfDefinicoesDeProcedimentoPorNome(String nome) {
        CriteriaQuery<WfDefinicaoDeProcedimento> q = cb().createQuery(WfDefinicaoDeProcedimento.class);
        Root<WfDefinicaoDeProcedimento> c = q.from(WfDefinicaoDeProcedimento.class);
        q.select(c);
        if (nome != null)
            nome = nome.replace(' ', '%');
        else
            nome = "";

        q.where(cb().like(c.get("nome"), "%" + nome + "%"), cb().equal(c.get("hisAtivo"), 1));

        try {
            return em.createQuery(q).getResultList();
        } catch (Exception ex) {
            throw new RuntimeException("Não foi possível localizar definições de procedimento com nome '" + nome + "'",
                    ex);
        }
    }

    public SortedSet<WfTarefa> consultarTarefasDeLotacao(DpLotacao lotaTitular) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<WfProcedimento> consultarProcedimentosAtivosPorEvento(String evento) {
        String sql = "from WfProcedimento p where p.eventoNome like :eventoNome";
        TypedQuery<WfProcedimento> query = em.createQuery(sql, WfProcedimento.class);
        query.setParameter("eventoNome", evento + "%");
        List<WfProcedimento> result = query.getResultList();
        return result;
    }

    public List<WfProcedimento> consultarProcedimentosAtivosPorPrincipal(String principal) {
        String sql = "select p from WfProcedimento p where p.hisDtFim is null and p.principal is not null and p.principal <> '' and p.principal like :principal";
        javax.persistence.Query query = em.createQuery(sql);
        query.setParameter("principal", principal + "%");
        List<WfProcedimento> result = query.getResultList();
        return result;
    }

    public void gravarInstanciaDeProcedimento(WfProcedimento pi) {
        SortedSet<WfVariavel> setDepois = new TreeSet<>();
        SortedSet<WfVariavel> setAntes = new TreeSet<>(pi.getVariaveis());

        if (pi.getVariavelMap() != null) {
            for (String k : pi.getVariavelMap().keySet()) {
                WfVariavel v = new WfVariavel();
                v.setProcedimento(pi);
                v.setNome(k);

                Object o = pi.getVariavelMap().get(k);
                if (o != null) {
                    if (o instanceof Date)
                        v.setDate((Date) o);
                    else if (o instanceof Double)
                        v.setNumber((Double) o);
                    else if (o instanceof Boolean)
                        v.setBool((Boolean) o);
                    else
                        v.setString((String) o);
                }
                setDepois.add(v);
            }
        }

        // Compara os dois conjuntos ordenados para determinar inclusões e exclusões.
        // Itens em setDepois que não estão em setAntes devem ser incluídos.
        // Itens em setAntes que não estão em setDepois devem ser excluídos (cancelados).
        Iterator<WfVariavel> iNovo = setDepois.iterator();
        Iterator<WfVariavel> iAntigo = setAntes.iterator();

        WfVariavel oNovo = iNovo.hasNext() ? iNovo.next() : null;
        WfVariavel oAntigo = iAntigo.hasNext() ? iAntigo.next() : null;

        while (oNovo != null || oAntigo != null) {
            if (oAntigo == null || (oNovo != null && oNovo.compareTo(oAntigo) < 0)) {
                pi.getVariaveis().add(oNovo);
                gravar(oNovo);
                oNovo = iNovo.hasNext() ? iNovo.next() : null;
            } else if (oNovo == null || (oAntigo != null && oAntigo.compareTo(oNovo) < 0)) {
                pi.getVariaveis().remove(oAntigo);
                excluir(oAntigo);
                oAntigo = iAntigo.hasNext() ? iAntigo.next() : null;
            } else {
                oAntigo.setBool(oNovo.getBool());
                oAntigo.setDate(oNovo.getDate());
                oAntigo.setNumber(oNovo.getNumber());
                oAntigo.setString(oNovo.getString());
                gravar(oAntigo);
                oNovo = iNovo.hasNext() ? iNovo.next() : null;
                oAntigo = iAntigo.hasNext() ? iAntigo.next() : null;
            }
        }

        if (pi.getAno() != null)
            return;

        pi.setAno(dt().getYear() + 1900);
        Query qry = em.createQuery(
                "select max(numero) from WfProcedimento pi where ano = :ano and orgaoUsuario.idOrgaoUsu = :ouid");
        qry.setParameter("ano", pi.getAno());
        qry.setParameter("ouid", pi.getOrgaoUsuario().getId());
        Integer i = (Integer) qry.getSingleResult();
        pi.setNumero((i == null ? 0 : i) + 1);

        gravar(pi);
    }

    @Override
    public void persist(WfProcedimento pi) {
        gravarInstanciaDeProcedimento((WfProcedimento) pi);
    }

    @Override
    public List<WfProcedimento> listByEvent(String event) {
        List<WfProcedimento> l = new ArrayList<>();
        l.addAll(consultarProcedimentosAtivosPorEvento(event));
        return l;
    }

    public WfConhecimento consultarConhecimento(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<WfProcedimento> consultarProcedimentosPorPessoaOuLotacao(DpPessoa titular, DpLotacao lotaTitular) {
        String sql = "select p from WfProcedimento p left join p.eventoPessoa pes left join p.eventoLotacao lot where pes.hisIdIni = :idPessoaIni or lot.hisIdIni = :idLotacaoIni";
        javax.persistence.Query query = em.createQuery(sql);
        query.setParameter("idPessoaIni", titular.getHisIdIni());
        query.setParameter("idLotacaoIni", lotaTitular.getHisIdIni());
        List<WfProcedimento> result = query.getResultList();
        List<WfProcedimento> l = new ArrayList<>();
        result.stream().forEach(i -> l.add(i));
        l.sort(null);
        return l;
    }

    public List<WfProcedimento> consultarProcedimentosAtivosPorDiagrama(WfDefinicaoDeProcedimento pd) {
        String sql = "select p from WfProcedimento p join p.definicaoDeProcedimento pd where pd.hisIdIni = :idIni and p.status not in ('FINISHED', 'INACTIVE')";
        TypedQuery<WfProcedimento> query = em.createQuery(sql, WfProcedimento.class);
        query.setParameter("idIni", pd.getHisIdIni());

        List<WfProcedimento> result = query.getResultList();
        result.sort(null);
        return result;
    }

    public List<WfResponsavel> consultarResponsaveisPorDefinicaoDeResponsavel(WfDefinicaoDeResponsavel dr) {
        String sql = "from WfResponsavel o where o.definicaoDeResponsavel.hisIdIni = :idIni and o.hisDtFim is null";
        javax.persistence.Query query = em.createQuery(sql);
        query.setParameter("idIni", dr.getHisIdIni());
        List<WfResponsavel> result = query.getResultList();
        if (result == null || result.size() == 0)
            return null;
        return result;
    }

    public WfResponsavel consultarResponsavelPorOrgaoEDefinicaoDeResponsavel(CpOrgaoUsuario ou,
                                                                             WfDefinicaoDeResponsavel dr) {
        String sql = "from WfResponsavel o where o.orgaoUsuario.idOrgaoUsu = :idOrgaoUsu and o.definicaoDeResponsavel.hisIdIni = :idIni and o.hisDtFim is null";
        javax.persistence.Query query = em.createQuery(sql);
        query.setParameter("idOrgaoUsu", ou.getIdOrgaoUsu());
        query.setParameter("idIni", dr.getHisIdIni());
        WfResponsavel result = (WfResponsavel) query.getSingleResult();
        return result;
    }

    public List<WfProcedimento> consultarProcedimentosParaEstatisticas(WfDefinicaoDeProcedimento pd, Date dataInicialDe,
                                                                       Date dataInicialAte, Date dataFinalDe, Date dataFinalAte) {
        String sql = "select p from WfProcedimento p inner join p.definicaoDeProcedimento pd where pd.hisIdIni = :hisIdIni and p.hisDtFim is not null "
                + " and p.hisDtIni >= :dataInicialDe and p.hisDtIni <= :dataInicialAte "
                + " and p.hisDtFim >= :dataFinalDe and p.hisDtFim <= :dataFinalAte";
        javax.persistence.Query query = em.createQuery(sql);
        query.setParameter("hisIdIni", pd.getHisIdIni());
        query.setParameter("dataInicialDe", dataInicialDe);
        query.setParameter("dataInicialAte", dataInicialAte);
        query.setParameter("dataFinalDe", dataFinalDe);
        query.setParameter("dataFinalAte", dataFinalAte);
        List<WfProcedimento> result = query.getResultList();
        return result;
    }

    public List<WfProcedimento> consultarProcedimentosParaEstatisticasPorPrincipal(WfDefinicaoDeProcedimento pd,
                                                                                   Date dataInicialDe, Date dataInicialAte, Date dataFinalDe, Date dataFinalAte) {
        String sql = "select p from WfProcedimento p inner join p.definicaoDeProcedimento pd where pd.hisIdIni = :hisIdIni and p.hisDtFim is not null "
                + " and p.hisDtIni >= :dataInicialDe and p.hisDtIni <= :dataInicialAte "
                + " and p.hisDtFim >= :dataFinalDe and p.hisDtFim <= :dataFinalAte "
                + " and p.principal is not null and p.principal <> '' order by p.principal";
        javax.persistence.Query query = em.createQuery(sql);
        query.setParameter("hisIdIni", pd.getHisIdIni());
        query.setParameter("dataInicialDe", dataInicialDe);
        query.setParameter("dataInicialAte", dataInicialAte);
        query.setParameter("dataFinalDe", dataFinalDe);
        query.setParameter("dataFinalAte", dataFinalAte);
        List<WfProcedimento> result = query.getResultList();
        return result;
    }

    public List<WfProcedimento> consultarProcedimentosParaEstatisticasPorPrincipalInclusiveNaoFinalizados(
            WfDefinicaoDeProcedimento pd, Date dataInicialDe, Date dataInicialAte, Date dataFinalDe,
            Date dataFinalAte) {
        String sql = "select p from WfProcedimento p inner join p.definicaoDeProcedimento pd where pd.hisIdIni = :hisIdIni "
                + " and p.hisDtIni >= :dataInicialDe and p.hisDtIni <= :dataInicialAte "
                + " and ((p.hisDtFim >= :dataFinalDe and p.hisDtFim <= :dataFinalAte) or p.hisDtFim is null) "
                + " and p.principal is not null and p.principal <> '' order by p.principal";
        javax.persistence.Query query = em.createQuery(sql);
        query.setParameter("hisIdIni", pd.getHisIdIni());
        query.setParameter("dataInicialDe", dataInicialDe);
        query.setParameter("dataInicialAte", dataInicialAte);
        query.setParameter("dataFinalDe", dataFinalDe);
        query.setParameter("dataFinalAte", dataFinalAte);
        List<WfProcedimento> result = query.getResultList();
        return result;
    }

    public WfDefinicaoDeProcedimento consultarPorSigla(WfDefinicaoDeProcedimentoDaoFiltro flt) {
        return consultarPorSigla(flt.getSigla(), WfDefinicaoDeProcedimento.class, flt.ouDefault);
    }

    public WfProcedimento consultarPorSigla(WfProcedimentoDaoFiltro flt) {
        return consultarPorSigla(flt.getSigla(), WfProcedimento.class, flt.ouDefault);
    }

    public <T> T consultarPorSigla(String sigla, Class<T> clazz, CpOrgaoUsuario ouDefault) {
        String acronimo = null;
        if (clazz.isAssignableFrom(WfProcedimento.class)) {
            acronimo = "WF";
        } else if (clazz.isAssignableFrom(WfDefinicaoDeProcedimento.class)) {
            acronimo = "DP";
        } else {
            throw new RuntimeException("Não é permitido consultar por sigla registros da classe " + clazz.getName());
        }
        SiglaDecodificada d = SiglaUtils.parse(sigla, acronimo, ouDefault);
        Integer ano = d.ano;
        Integer numero = d.numero;
        CpOrgaoUsuario orgaoUsuario = d.orgaoUsuario;

        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> q = criteriaBuilder.createQuery(clazz);
        Root<T> c = q.from(clazz);
        Join<T, CpOrgaoUsuario> joinOrgao = c.join("orgaoUsuario", JoinType.INNER);
        // if (clazz.isAssignableFrom(Historico.class))
        if (Historico.class.isAssignableFrom(clazz))
            q.where(cb().equal(c.get("numero"), numero), cb().equal(c.get("ano"), ano),
                    cb().equal(c.get("hisAtivo"), 1), cb().equal(joinOrgao.get("idOrgaoUsu"), orgaoUsuario.getId()));
        else
            q.where(cb().equal(c.get("numero"), numero), cb().equal(c.get("ano"), ano),
                    cb().equal(joinOrgao.get("idOrgaoUsu"), orgaoUsuario.getId()));
        return em.createQuery(q).getSingleResult();
    }

    public List<WfConfiguracao> consultar(final WfConfiguracao exemplo) {
        ITipoDeConfiguracao tpConf = exemplo.getCpTipoConfiguracao();
        CpOrgaoUsuario orgao = exemplo.getOrgaoUsuario();

        StringBuffer sbf = new StringBuffer();

        sbf.append("select * from sigawf.wf_configuracao wf inner join " + "corporativo"
                + ".cp_configuracao cp on wf.conf_id = cp.id_configuracao ");

        sbf.append("" + "where 1 = 1");

        if (tpConf != null) {
            sbf.append(" and cp.id_tp_configuracao = ");
            sbf.append(exemplo.getCpTipoConfiguracao().getId());
        }

        if (orgao != null && orgao.getId() != null && orgao.getId() != 0) {
            sbf.append(" and (cp.id_orgao_usu = ");
            sbf.append(orgao.getId());
            sbf.append(" or cp.id_lotacao in (select id_lotacao from " + "corporativo"
                    + ".dp_lotacao lot where lot.id_orgao_usu= ");
            sbf.append(orgao.getId());
            sbf.append(")");
            sbf.append(" or cp.id_pessoa in (select id_pessoa from " + "corporativo"
                    + ".dp_pessoa pes where pes.id_orgao_usu = ");
            sbf.append(orgao.getId());
            sbf.append(")");
            sbf.append(" or cp.id_cargo in (select id_cargo from " + "corporativo"
                    + ".dp_cargo cr where cr.id_orgao_usu = ");
            sbf.append(orgao.getId());
            sbf.append(")");
            sbf.append(" or cp.id_funcao_confianca in (select id_funcao_confianca from " + "corporativo"
                    + ".dp_funcao_confianca fc where fc.id_orgao_usu = ");
            sbf.append(orgao.getId());
            sbf.append(")");
            sbf.append(
                    " or (cp.id_orgao_usu is null and cp.id_lotacao is null and cp.id_pessoa is null and cp.id_cargo is null and cp.id_funcao_confianca is null");
            sbf.append(")");
            sbf.append(")");
            sbf.append("order by wf.conf_id");

        }

        Query query = em.createNativeQuery(sbf.toString(), WfConfiguracao.class);
        query.setHint("org.hibernate.cacheable", true);
        query.setHint("org.hibernate.cacheRegion", "query.ExConfiguracao");
        return query.getResultList();

    }

    public List<WfDefinicaoDeProcedimento> listarDefinicoesDeProcedimentos() {
        return listarAtivos(WfDefinicaoDeProcedimento.class, "nome");
    }

    public WfDefinicaoDeProcedimento defProcedimentoBySigla(String sigla) {
        SiglaDecodificada d = SiglaUtils.parse(sigla, "DP", null);

        WfDefinicaoDeProcedimento info = null;

        if (d.id != null) {
            info = AR.findById(d.id);
        } else if (d.numero != null) {
            info = AR.find("ano = ?1 and numero = ?2 and ou.idOrgaoUsu = ?3", d.ano, d.numero, d.orgaoUsuario.getId())
                    .first();
        }

        if (info == null) {
            throw new AplicacaoException("Não foi possível encontrar uma definição de procedimento com o código "
                    + sigla + ". Favor verificá-lo.");
        } else
            return info;
    }

}