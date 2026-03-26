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
 * Criado em  21/12/2005
 *
 */
package br.gov.jfrj.siga.dp;

import br.gov.jfrj.siga.cp.CpIdentidade;
import br.gov.jfrj.siga.cp.model.HistoricoAuditavel;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

@MappedSuperclass
@NamedQueries({
        @NamedQuery(
                name = "consultarLotacaoAtualPelaLotacaoInicial",
                query = "from DpLotacao lot "
                        + "      where hisDtIni = "
                        + "      (select max(l.dataInicioLotacao) from DpLotacao l where l.hisIdIni= :hisIdIni) "
                        + "      and lot.hisIdIni= :hisIdIni"
        ),
        @NamedQuery(name = "consultarPorIdDpLotacao", query = "select lot from DpLotacao lot where lot.idLotacao = :idLotacao"),
        @NamedQuery(name = "consultarPorSiglaDpLotacao", query = "select lot from DpLotacao lot where"
                + "      ((lot.siglaLotacao = upper(:siglaLotacao)"
                + "      and (:idOrgaoUsu = null or :idOrgaoUsu = 0L or lot.orgaoUsuario.idOrgaoUsu = :idOrgaoUsu))"
                + "      or (:siglaOrgaoLotacao is not null and lot.siglaLotacao = upper(:siglaOrgaoLotacao)))"
                + "	     and lot.hisDtFim = null"),
        @NamedQuery(
                name = "consultarPorSiglaInclusiveFechadasDpLotacao",
                query = "select lotacao from DpLotacao lotacao where"
                        + "      ((lotacao.siglaLotacao = upper(:siglaLotacao)"
                        + "      and (:idOrgaoUsu = null or :idOrgaoUsu = 0L or lotacao.orgaoUsuario.idOrgaoUsu = :idOrgaoUsu))"
                        + "      or (:siglaOrgaoLotacao is not null and lotacao.siglaLotacao = upper(:siglaOrgaoLotacao)))"
                        + "		 and exists (select 1 from DpLotacao lAux where lAux.hisIdIni= lotacao.hisIdIni"
                        + "			group by lAux.hisIdInihaving max(lAux.dataInicioLotacao) = lotacao.dataInicioLotacao)"
        ),
        @NamedQuery(
                name = "consultarPorSiglaDpLotacaoComLike",
                query = "select lot from DpLotacao lot where"
                        + "        upper(lot.siglaLotacao) like upper('%' || :siglaLotacao || '%') "
                        + "        and (:idOrgaoUsu = null or :idOrgaoUsu = 0L or lot.orgaoUsuario.idOrgaoUsu = :idOrgaoUsu)"
                        + "        and lot.hisDtFim = null"
        ),
        @NamedQuery(
                name = "consultarPorIdInicialDpLotacao",
                query = "select lot from DpLotacao lot where lot.hisIdIni= :hisIdIni and lot.hisDtFim = null"
        ),
        @NamedQuery(
                name = "listarPorIdInicialDpLotacao",
                query = "select lot from DpLotacao lot where lot.hisIdIni= :hisIdIni"
        ),
        // Encontra a lotação se like a sigla, o nome e o órgão é o mesmo do usuário atual, ou se like a sigla prefixada pelo acrônimo ou pela sigla do órgão. Ou seja,
        // se sou do TRF2 e buscar "SG", deve retornar apenas T2-SG. Sem a especificação do órgão, retornaria as SGs de todos os órgãos. Se eu sou do TRF2 e
        // prefixar aconsulta com com JFRJSG, deve retornar apenas RJ-SG.
        @NamedQuery(
                name = "consultarPorFiltroDpLotacao",
                query = "from DpLotacao lot "
                        + "where ((upper(lot.nomeLotacaoAI) like upper('%' || :nome || '%') or upper(lot.siglaLotacao) like upper('%' || :sigla || '%')) "
                        + " and (:idOrgaoUsu = null or :idOrgaoUsu = 0L or lot.orgaoUsuario.idOrgaoUsu = :idOrgaoUsu)"
                        + " or ( :nome != null and (:idOrgaoUsu = null or :idOrgaoUsu = 0L or lot.orgaoUsuario.idOrgaoUsu = :idOrgaoUsu) and (upper(concat(lot.orgaoUsuario.acronimoOrgaoUsu, lot.siglaLotacao)) like upper(:nome || '%')"
                        + " or upper(concat(lot.orgaoUsuario.siglaOrgaoUsu, lot.siglaLotacao)) like upper(:nome || '%'))))"
                        + "	and lot.hisDtFim = null"
                        + "	 order by lot.nomeLotacao"
        ),
        @NamedQuery(
                name = "consultarQuantidadeDpLotacao",
                query = "select count(lot) from DpLotacao lot "
                        + " where ((upper(lot.nomeLotacaoAI) like upper('%' || :nome || '%') or upper(lot.siglaLotacao) like upper('%' || :sigla || '%'))"
                        + "	 and (:idOrgaoUsu = null or :idOrgaoUsu = 0L or lot.orgaoUsuario.idOrgaoUsu = :idOrgaoUsu)"
                        + "  or ( :nome != null and (:idOrgaoUsu = null or :idOrgaoUsu = 0L or lot.orgaoUsuario.idOrgaoUsu = :idOrgaoUsu) and (upper(concat(lot.orgaoUsuario.acronimoOrgaoUsu, lot.siglaLotacao)) like upper(:nome || '%')"
                        + "  or upper(concat(lot.orgaoUsuario.siglaOrgaoUsu, lot.siglaLotacao)) like upper(:nome || '%'))))"
                        + "	 and lot.hisDtFim = null"
        ),
        @NamedQuery(
                name = "consultarPorFiltroDpLotacaoInclusiveFechadas",
                query = "from DpLotacao lotacao"
                        + " where ((upper(lotacao.nomeLotacaoAI) like upper('%' || :nome || '%') or upper(lotacao.siglaLotacao) like upper('%' || :sigla || '%')) "
                        + "	 and (:idOrgaoUsu = null or :idOrgaoUsu = 0L or lotacao.orgaoUsuario.idOrgaoUsu = :idOrgaoUsu)"
                        + "  or ( :nome != null and (:idOrgaoUsu = null or :idOrgaoUsu = 0L or lotacao.orgaoUsuario.idOrgaoUsu = :idOrgaoUsu) and (upper(concat(lotacao.orgaoUsuario.acronimoOrgaoUsu, lotacao.siglaLotacao)) like upper(:nome || '%')"
                        + "  or upper(concat(lotacao.orgaoUsuario.siglaOrgaoUsu, lotacao.siglaLotacao)) like upper(:nome || '%'))))"
                        + "	 and exists (select 1 from DpLotacao lAux where lAux.hisIdIni= lotacao.hisIdIni"
                        + "	 group by lAux.hisIdInihaving max(lAux.dataInicioLotacao) = lotacao.dataInicioLotacao)"
                        + "  order by upper(nomeLotacaoAI)"
        ),
        @NamedQuery(
                name = "consultarQuantidadeDpLotacaoInclusiveFechadas",
                query = "select count(distinct lotacao.hisIdIni) from DpLotacao lotacao"
                        + "  where ((upper(lotacao.nomeLotacaoAI) like upper('%' || :nome || '%') or upper(lotacao.siglaLotacao) like upper('%' || :sigla || '%')) "
                        + "	and (:idOrgaoUsu = null or :idOrgaoUsu = 0L or lotacao.orgaoUsuario.idOrgaoUsu = :idOrgaoUsu)"
                        + " or ( :nome != null and (:idOrgaoUsu = null or :idOrgaoUsu = 0L or lotacao.orgaoUsuario.idOrgaoUsu = :idOrgaoUsu) and (upper(concat(lotacao.orgaoUsuario.acronimoOrgaoUsu, lotacao.siglaLotacao)) like upper(:nome || '%')"
                        + " or upper(concat(lotacao.orgaoUsuario.siglaOrgaoUsu, lotacao.siglaLotacao)) like upper(:nome || '%'))))"
                        + "	and exists (select 1 from DpLotacao lAux where lAux.hisIdIni= lotacao.hisIdIni"
                        + "	 group by lAux.hisIdInihaving max(lAux.hisDtIni) = lotacao.hisDtIni)"
        ),
        @NamedQuery(
                name = "consultarPorNomeOrgaoDpLotacao",
                query = "select lot from DpLotacao lot where upper(REMOVE_ACENTO(lot.nomeLotacao)) = upper(REMOVE_ACENTO(:nome)) and lot.orgaoUsuario.idOrgaoUsu = :idOrgaoUsu"
        )
})
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "consultarQtdeDocCriadosPossePorDpLotacao",
                query = "SELECT count(1) FROM siga.ex_documento doc "
                        + " left join corporativo.dp_lotacao lot on doc.id_lota_cadastrante = lot.id_lotacao "
                        + " left join siga.ex_mobil mob on mob.id_doc = doc.id_doc "
                        + " left join corporativo.cp_marca marca on marca.id_ref = mob.ID_MOBIL"
                        + " where lot.id_lotacao_ini = :idLotacao or marca.ID_LOTACAO_INI = :idLotacao"
        ),
        @NamedNativeQuery(
                name = "consultarQtdeDocCriadosPossePorDpLotacaoECpMarca",
                query = "SELECT count(1) FROM siga.ex_documento doc "
                        + " left join corporativo.dp_lotacao lot on doc.id_lota_cadastrante = lot.id_lotacao "
                        + " left join siga.ex_mobil mob on mob.id_doc = doc.id_doc "
                        + " left join corporativo.cp_marca marca on marca.id_ref = mob.ID_MOBIL"
                        + " where lot.id_lotacao_ini = :idLotacao or (marca.ID_LOTACAO_INI = :idLotacao"
                        + " and marca.ID_MARCADOR not in (:listMarcadores))"
        )
})
public abstract class AbstractDpLotacao extends DpResponsavel<DpLotacao> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ID_LOTACAO", unique = true, nullable = false)
    private Long idLotacao;

    @Column(name = "NOME_LOTACAO", nullable = false, length = 120)
    private String nomeLotacao;

    @Column(name = "SIGLA_LOTACAO", length = 30)
    protected String siglaLotacao;

    @Column(name = "IDE_LOTACAO")
    private String ideLotacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_LOTACAO_INI", insertable = false, updatable = false)
    private DpLotacao lotacaoInicial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_LOTACAO_PAI")
    private DpLotacao lotacaoPai;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "lotacaoInicial")
    private Set<DpLotacao> lotacoesPosteriores = new TreeSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ORGAO_USU", nullable = false)
    protected CpOrgaoUsuario orgaoUsuario;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "lotacaoPai")
    private Set<DpLotacao> dpLotacaoSubordinadosSet = new TreeSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "lotacao")
    @Where(clause = "DATA_FIM_PESSOA is null")
    private Set<DpPessoa> dpPessoaLotadosSet = new TreeSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TP_LOTACAO")
    private CpTipoLotacao cpTipoLotacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_LOCALIDADE")
    private CpLocalidade localidade;

    @Column(name = "IS_EXTERNA_LOTACAO")
    private Integer isExternaLotacao;

    @Column(name = "IS_SUSPENSA")
    private Integer isSuspensa;

    public Integer getIsExternaLotacao() {
        return isExternaLotacao;
    }

    public void setIsExternaLotacao(Integer isExternaLotacao) {
        this.isExternaLotacao = isExternaLotacao;
    }

    /**
     * @return the cpTipoLotacao
     */
    public CpTipoLotacao getCpTipoLotacao() {
        return cpTipoLotacao;
    }

    /**
     * @param cpTipoLotacao the cpTipoLotacao to set
     */
    public void setCpTipoLotacao(CpTipoLotacao cpTipoLotacao) {
        this.cpTipoLotacao = cpTipoLotacao;
    }

    public String getIdeLotacao() {
        return ideLotacao;
    }

    public void setIdeLotacao(String ideLotacao) {
        this.ideLotacao = ideLotacao;
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public boolean equals(final Object rhs) {
        if (!(rhs instanceof DpLotacao))
            return false;
        final DpLotacao that = (DpLotacao) rhs;

        if ((this.getIdLotacao() == null ? that.getIdLotacao() == null : this
                .getIdLotacao().equals(that.getIdLotacao()))) {
            return this.getNomeLotacao() == null ? that.getNomeLotacao() == null
                    : this.getNomeLotacao().equals(that.getNomeLotacao());

        }
        return false;

    }

    /**
     * @return Retorna o atributo idLotacao.
     */
    public Long getIdLotacao() {
        return idLotacao;
    }

    /**
     * @return Retorna o atributo lotacaoPai.
     */
    public DpLotacao getLotacaoPai() {
        return lotacaoPai;
    }

    /**
     * @return Retorna o atributo nomeLotacao.
     */
    public String getNomeLotacao() {
        return nomeLotacao;
    }

    public String getSiglaLotacao() {
        return siglaLotacao;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = 17;
        int idValue = this.getIdLotacao() == null ? 0 : this.getIdLotacao()
                .hashCode();
        result = result * 37 + idValue;
        idValue = this.getNomeLotacao() == null ? 0 : this.getNomeLotacao()
                .hashCode();
        result = result * 37 + idValue;

        return result;
    }

    /**
     * @param idLotacao Atribui a idLotacao o valor.
     */
    public void setIdLotacao(final Long idLotacao) {
        this.idLotacao = idLotacao;
    }

    /**
     * @param lotacaoPai Atribui a lotacaoPai o valor.
     */
    public void setLotacaoPai(final DpLotacao lotacaoPai) {
        this.lotacaoPai = lotacaoPai;
    }

    /**
     * @param nomeLotacao Atribui a nomeLotacao o valor.
     */
    public void setNomeLotacao(final String nomeLotacao) {
        this.nomeLotacao = nomeLotacao;
    }

    public void setSiglaLotacao(final String siglaLotacao) {
        this.siglaLotacao = siglaLotacao;
    }

    public CpOrgaoUsuario getOrgaoUsuario() {
        return orgaoUsuario;
    }

    public void setOrgaoUsuario(CpOrgaoUsuario orgaoUsuario) {
        this.orgaoUsuario = orgaoUsuario;
    }

    public Set<DpLotacao> getLotacoesPosteriores() {
        return lotacoesPosteriores;
    }

    public void setLotacoesPosteriores(Set<DpLotacao> lotacoesPosteriores) {
        this.lotacoesPosteriores = lotacoesPosteriores;
    }

    public DpLotacao getLotacaoInicial() {
        return lotacaoInicial;
    }

    public void setLotacaoInicial(DpLotacao lotacaoInicial) {
        this.lotacaoInicial = lotacaoInicial;
    }

    public Set<DpLotacao> getDpLotacaoSubordinadosSet() {
        return dpLotacaoSubordinadosSet;
    }

    public void setDpLotacaoSubordinadosSet(
            Set<DpLotacao> dpLotacaoSubordinadosSet) {
        this.dpLotacaoSubordinadosSet = dpLotacaoSubordinadosSet;
    }

    public Set<DpPessoa> getDpPessoaLotadosSet() {
        return dpPessoaLotadosSet;
    }

    public CpLocalidade getLocalidade() {
        return localidade;
    }

    public void setLocalidade(CpLocalidade localidade) {
        this.localidade = localidade;
    }

    public Integer getIsSuspensa() {
        return isSuspensa;
    }

    public void setIsSuspensa(Integer isSuspensa) {
        this.isSuspensa = isSuspensa;
    }

}
