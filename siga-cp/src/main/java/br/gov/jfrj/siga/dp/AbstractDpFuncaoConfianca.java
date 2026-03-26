/*******************************************************************************
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
 * Criado em  20/12/2005
 *
 */
package br.gov.jfrj.siga.dp;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;

import br.gov.jfrj.siga.cp.CpIdentidade;
import br.gov.jfrj.siga.cp.model.HistoricoAuditavel;
import br.gov.jfrj.siga.model.HistoricoSuporte;

/**
 * Classe que representa uma linha na tabela DP_FUNCAO_CONFIANCA. Você pode
 * customizar o comportamento desta classe editando a classe
 * {@link DpFuncaoConfianca}.
 */
@MappedSuperclass
@NamedQueries({
		@NamedQuery(name = "consultarPorSiglaDpFuncaoConfianca", query = "select fun from DpFuncaoConfianca fun where fun.idFuncao = :idFuncao"),
		@NamedQuery(name = "consultarPorFiltroDpFuncaoConfianca", query = "from DpFuncaoConfianca fun "
				+ "  where ( :nome = null or upper(fun.nmFuncaoConfiancaAI) like upper('%' || :nome || '%'))"
				+ "  	and (:idOrgaoUsu = null or :idOrgaoUsu = 0L or fun.orgaoUsuario.idOrgaoUsu = :idOrgaoUsu)"
				+ "   	and fun.hisDtFim = null"
				+ "   	order by upper(fun.nomeFuncao)"),
		@NamedQuery(name = "consultarPorFiltroDpFuncaoConfiancaInclusiveInativas", query = "from DpFuncaoConfianca funcao "
				+ "  	where  (:idOrgaoUsu = null or :idOrgaoUsu = 0L or funcao.orgaoUsuario.idOrgaoUsu = :idOrgaoUsu)"
				+ "  		and ( :nome = null or upper(funcao.nmFuncaoConfiancaAI) like upper('%' || :nome || '%'))"
				+ "			and exists (select 1 from DpFuncaoConfianca fAux where fAux.hisIdIni = funcao.hisIdIni"
				+ "				group by fAux.hisIdIni having max(fAux.hisDtIni) = funcao.hisDtIni)"
				+ "   	order by upper(funcao.nomeFuncao)"),
		@NamedQuery(name = "consultarQuantidadeDpFuncaoConfianca", query = "select count(fun) from DpFuncaoConfianca fun "
				+ "  where ( :nome = null or upper(fun.nmFuncaoConfiancaAI) like upper('%' || :nome || '%'))"
				+ "  	and (:idOrgaoUsu = null or :idOrgaoUsu = 0L or fun.orgaoUsuario.idOrgaoUsu = :idOrgaoUsu)"
				+ "   	and fun.hisDtFim = null"),
		@NamedQuery(name = "consultarQuantidadeDpFuncaoConfiancaInclusiveInativas", query = "select count(distinct funcao.hisIdIni) from DpFuncaoConfianca funcao "
				+ "  	where  (:idOrgaoUsu = null or :idOrgaoUsu = 0L or funcao.orgaoUsuario.idOrgaoUsu = :idOrgaoUsu)"
				+ "  		and ( :nome = null or upper(funcao.nmFuncaoConfiancaAI) like upper('%' || :nome || '%'))"),
		@NamedQuery(name = "consultarPorNomeOrgaoDpFuncaoConfianca", query = "select fun from DpFuncaoConfianca fun where upper(REMOVE_ACENTO(fun.nomeFuncao)) = upper(REMOVE_ACENTO(:nome)) and fun.orgaoUsuario.idOrgaoUsu = :idOrgaoUsuario")})

public abstract class AbstractDpFuncaoConfianca extends HistoricoSuporte<DpFuncaoConfianca> implements
		Serializable, HistoricoAuditavel<DpFuncaoConfianca>  {

	@Id
	@SequenceGenerator(name = "DP_FUNCAO_CONFIANCA_SEQ", sequenceName = "CORPORATIVO.DP_FUNCAO_CONFIANCA_SEQ")
	@GeneratedValue(generator = "DP_FUNCAO_CONFIANCA_SEQ")
	@Column(name = "ID_FUNCAO_CONFIANCA", unique = true, nullable = false)
	private Long idFuncao;
	
	@Column(name = "CATEGORIA_FUNCAO_CONFIANCA", length = 15)
	private String categoriaFuncao;

	@Column(name = "COD_FOLHA_FUNCAO_CONFIANCA")
	private Integer codFolhaFuncao;
	
	@Column(name = "IDE_FUNCAO_CONFIANCA", length = 256)
	private String ideFuncao;

	@Column(name = "ID_FUNCAO_CONFIANCA_PAI")
	private Long idFuncaoPai;

	@Column(name = "NIVEL_FUNCAO_CONFIANCA")
	private Integer nivelFuncao;

	@Column(name = "NOME_FUNCAO_CONFIANCA", nullable = false, length = 100)
	private String nomeFuncao;
	
	@Column(name = "SIGLA_FUNCAO_CONFIANCA", length = 30)
	private String siglaFuncaoConfianca;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_ORGAO_USU", nullable = false)
	private CpOrgaoUsuario orgaoUsuario;

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="HIS_IDC_INI")
	private CpIdentidade hisIdcIni;

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="HIS_IDC_FIM")
	private CpIdentidade hisIdcFim;

	public CpOrgaoUsuario getOrgaoUsuario() {
		return orgaoUsuario;
	}

	public void setOrgaoUsuario(CpOrgaoUsuario orgaoUsuario) {
		this.orgaoUsuario = orgaoUsuario;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object rhs) {
		if ((rhs == null) || !(rhs instanceof DpFuncaoConfianca))
			return false;
		final DpFuncaoConfianca that = (DpFuncaoConfianca) rhs;

		if ((this.getIdFuncao() == null ? that.getIdFuncao() == null : this
				.getIdFuncao().equals(that.getIdFuncao()))) {
			if ((this.getNomeFuncao() == null ? that.getNomeFuncao() == null
					: this.getNomeFuncao().equals(that.getNomeFuncao())))
				return true;

		}
		return false;

	}

	public String getCategoriaFuncao() {
		return categoriaFuncao;
	}

	public Integer getCodFolhaFuncao() {
		return codFolhaFuncao;
	}

	/**
	 * @return Retorna o atributo idFuncao.
	 */
	public Long getIdFuncao() {
		return idFuncao;
	}

	public Long getIdFuncaoPai() {
		return idFuncaoPai;
	}

	public Integer getNivelFuncao() {
		return nivelFuncao;
	}

	/**
	 * @return Retorna o atributo nomeFuncao.
	 */
	public String getNomeFuncao() {
		return nomeFuncao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = 17;
		int idValue = this.getIdFuncao() == null ? 0 : this.getIdFuncao()
				.hashCode();
		result = result * 37 + idValue;
		idValue = this.getNomeFuncao() == null ? 0 : this.getNomeFuncao()
				.hashCode();
		result = result * 37 + idValue;

		return result;
	}

	public void setCategoriaFuncao(final String categoriaFuncao) {
		this.categoriaFuncao = categoriaFuncao;
	}

	public void setCodFolhaFuncao(final Integer codFolhaFuncao) {
		this.codFolhaFuncao = codFolhaFuncao;
	}

	/**
	 * @param idFuncao
	 *            Atribui a idFuncao o valor.
	 */
	public void setIdFuncao(final Long idFuncao) {
		this.idFuncao = idFuncao;
	}

	public void setIdFuncaoPai(final Long idFuncaoPai) {
		this.idFuncaoPai = idFuncaoPai;
	}

	public void setNivelFuncao(final Integer nivelFuncao) {
		this.nivelFuncao = nivelFuncao;
	}

	/**
	 * @param nomeFuncao
	 *            Atribui a nomeFuncao o valor.
	 */
	public void setNomeFuncao(final String nomeFuncao) {
		this.nomeFuncao = nomeFuncao;
	}

	public String getIdeFuncao() {
		return ideFuncao;
	}

	public void setIdeFuncao(String ideFuncao) {
		this.ideFuncao = ideFuncao;
	}

	/**
	 * @return the siglaFuncaoConfianca
	 */
	public String getSiglaFuncaoConfianca() {
		return siglaFuncaoConfianca;
	}

	/**
	 * @param siglaFuncaoConfianca
	 *            the siglaFuncaoConfianca to set
	 */
	public void setSiglaFuncaoConfianca(String siglaFuncaoConfianca) {
		this.siglaFuncaoConfianca = siglaFuncaoConfianca;
	}

	/**
	 * @return the hisIdcIni
	 */
	public CpIdentidade getHisIdcIni() {
		return hisIdcIni;
	}

	/**
	 * @param hisIdcIni the hisIdcIni to set
	 */
	public void setHisIdcIni(CpIdentidade hisIdcIni) {
		this.hisIdcIni = hisIdcIni;
	}

	/**
	 * @return the hisIdcFim
	 */
	public CpIdentidade getHisIdcFim() {
		return hisIdcFim;
	}

	/**
	 * @param hisIdcFim the hisIdcFim to set
	 */
	public void setHisIdcFim(CpIdentidade hisIdcFim) {
		this.hisIdcFim = hisIdcFim;
	}

	
}
