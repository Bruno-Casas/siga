package br.gov.jfrj.siga.wf.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;

import com.crivano.jflow.support.ProcessInstanceVariable;

import br.gov.jfrj.siga.base.Data;

@Entity
@BatchSize(size = 500)
@Table(name = "sigawf.wf_variavel")
public class WfVariavel implements ProcessInstanceVariable, Comparable<WfVariavel> {
	@Id
	@GeneratedValue
	@Column(name = "VARI_ID", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PROC_ID")
	private WfProcedimento procedimento;

	@Column(name = "VARI_NM", length = 256)
	private String nome;

	@Column(name = "VARI_TX")
	String string;

	@Column(name = "VARI_DF")
	Date date;

	@Column(name = "VARI_FG")
	Boolean bool;

	@Column(name = "VARI_NR")
	Double number;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public String getIdentifier() {
		return getNome();
	}

	@Override
	public void setIdentifier(String identifier) {
		this.setNome(identifier);
	}

	@Override
	public String getString() {
		return string;
	}

	@Override
	public void setString(String string) {
		this.string = string;
	}

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public Boolean getBool() {
		return bool;
	}

	@Override
	public void setBool(Boolean bool) {
		this.bool = bool;
	}

	@Override
	public Double getNumber() {
		return number;
	}

	@Override
	public void setNumber(Double number) {
		this.number = number;
	}

	public Long getId() {
		return this.id;
	}

	@Override
	public int compareTo(WfVariavel o) {
		if (o == null)
			return 1;
		if (this.nome != null && o.nome != null) {
			int i = this.nome.compareTo(o.nome);
			if (i != 0)
				return i;
		} else if (this.nome != null) {
			return 1;
		} else if (o.nome != null) {
			return -1;
		}

		Object v1 = this.getValor();
		Object v2 = o.getValor();

		if (v1 != null && v2 != null) {
			if (v1 instanceof Comparable && v2.getClass().isInstance(v1)) {
				@SuppressWarnings("unchecked")
				int i = ((Comparable<Object>) v1).compareTo(v2);
				if (i != 0)
					return i;
			} else {
				int i = v1.toString().compareTo(v2.toString());
				if (i != 0)
					return i;
			}
		} else if (v1 != null) {
			return 1;
		} else if (v2 != null) {
			return -1;
		}

		if (this.id != null && o.id != null)
			return this.id.compareTo(o.id);

		return 0;
	}

	public WfProcedimento getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(WfProcedimento procedimento) {
		this.procedimento = procedimento;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Object getValor() {
		if (string != null)
			return string;
		if (date != null)
			return date;
		if (bool != null)
			return bool;
		if (number != null)
			return number;
		return null;
	}

	public Object getValorAsString() {
		if (string != null)
			return string;
		if (date != null)
			return Data.formatDDMMYY_AS_HHMMSS(date);
		if (bool != null)
			return bool ? "Sim" : "Não";
		if (number != null)
			return number.toString();
		return null;
	}

}
