package br.gov.jfrj.siga.wf.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.BatchSize;

import com.crivano.jflow.model.TaskDefinitionVariable;
import com.crivano.jflow.model.enm.VariableEditingKind;
import com.crivano.jflow.model.enm.VariableKind;

import br.gov.jfrj.siga.cp.model.HistoricoAuditavelSuporte;
import br.gov.jfrj.siga.model.SincronizavelSimples;
import br.gov.jfrj.siga.wf.model.enm.WfTipoDeAcessoDeVariavel;
import br.gov.jfrj.siga.wf.model.enm.WfTipoDeVariavel;
import br.gov.jfrj.siga.wf.util.NaoSerializar;

@Entity
@BatchSize(size = 500)
@Table(name = "sigawf.wf_def_variavel")
public class WfDefinicaoDeVariavel extends HistoricoAuditavelSuporte
		implements TaskDefinitionVariable, SincronizavelSimples {
	@Id
	@GeneratedValue
	@Column(name = "DEFV_ID", unique = true, nullable = false)
	private Long id;

	@NotNull
	@NaoSerializar
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DEFT_ID")
	private WfDefinicaoDeTarefa definicaoDeTarefa;

	@NotNull
	@Column(name = "DEFV_CD", length = 32)
	private String identificador;

	@NotNull
	@Column(name = "DEFV_NM", length = 256)
	private String nome;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "DEFV_TP")
	WfTipoDeVariavel tipo;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "DEFV_TP_ACESSO")
	WfTipoDeAcessoDeVariavel acesso;

	@javax.persistence.Transient
	private java.lang.String hisIde;

	@Column(name = "DEFV_NR_ORDEM")
	private int ordem;

	public String getIdExterna() {
		return this.getHisIde();
	}

	@Override
	public String getIdSincronizacao() {
		String idExt = getIdExterna();
		if (idExt == null || "null".equals(idExt) || idExt.isEmpty()) {
			return (getHisIdIni() != null) ? "DB:" + getHisIdIni() : "NEW:" + System.identityHashCode(this);
		}
		return "EXT:" + idExt;
	}

	public java.lang.String getHisIde() {
		return hisIde;
	}

	public void setHisIde(java.lang.String hisIde) {
		this.hisIde = hisIde;
	}

	public WfDefinicaoDeVariavel() {
		super();
	}

	public WfDefinicaoDeVariavel(WfDefinicaoDeVariavel toClone) {
		super();
		this.identificador = toClone.identificador;
		this.nome = toClone.nome;
		this.tipo = toClone.tipo;
		this.acesso = WfTipoDeAcessoDeVariavel.READ_WRITE;
	}

	//
	// Solução para não precisar criar HIS_ATIVO em todas as tabelas que herdam
	// de HistoricoSuporte.
	//
	@Column(name = "HIS_ATIVO")
	private Integer hisAtivo;

	@Override
	public Integer getHisAtivo() {
		this.hisAtivo = super.getHisAtivo();
		return this.hisAtivo;
	}

	@Override
	public void setHisAtivo(Integer hisAtivo) {
		super.setHisAtivo(hisAtivo);
		this.hisAtivo = getHisAtivo();
	}

	public WfTipoDeAcessoDeVariavel getAcesso() {
		return acesso;
	}

	@Override
	public VariableEditingKind getEditingKind() {
		return acesso.getKind();
	}

	public Long getId() {
		return id;
	}

	public String getIdentificador() {
		return identificador;
	}

	@Override
	public String getIdentifier() {
		return identificador;
	}

	@Override
	public VariableKind getKind() {
		return tipo.getKind();
	}

	public String getNome() {
		return nome;
	}

	public WfTipoDeVariavel getTipo() {
		return tipo;
	}

	@Override
	public String getTitle() {
		return nome;
	}

	public void setAcesso(WfTipoDeAcessoDeVariavel acesso) {
		this.acesso = acesso;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setTipo(WfTipoDeVariavel tipo) {
		this.tipo = tipo;
	}

	public boolean isRequired() {
		return this.getAcesso() == WfTipoDeAcessoDeVariavel.READ_WRITE_REQUIRED;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	public WfDefinicaoDeTarefa getDefinicaoDeTarefa() {
		return definicaoDeTarefa;
	}

	public void setDefinicaoDeTarefa(WfDefinicaoDeTarefa definicaoDeTarefa) {
		this.definicaoDeTarefa = definicaoDeTarefa;
	}

}
