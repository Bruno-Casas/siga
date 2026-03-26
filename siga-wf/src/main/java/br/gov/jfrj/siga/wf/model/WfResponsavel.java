package br.gov.jfrj.siga.wf.model;

import br.gov.jfrj.siga.cp.model.HistoricoAuditavelSuporte;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.model.SincronizavelSimples;
import com.crivano.jflow.model.Responsible;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@BatchSize(size = 500)
@Table(name = "sigawf.wf_responsavel")
public class WfResponsavel extends HistoricoAuditavelSuporte
        implements Responsible, Serializable, SincronizavelSimples {

    @Id
    @GeneratedValue
    @Column(name = "RESP_ID", unique = true, nullable = false)
    private java.lang.Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEFR_ID")
    private WfDefinicaoDeResponsavel definicaoDeResponsavel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PESS_ID")
    private DpPessoa pessoa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOTA_ID")
    private DpLotacao lotacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGU_ID")
    private CpOrgaoUsuario orgaoUsuario;

    @Transient
    private java.lang.String hisIde;

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
        this.hisAtivo = hisAtivo;
    }

    public WfResponsavel() {
        super();
    }

    public WfResponsavel(DpPessoa pessoa, DpLotacao lotacao) {
        super();
        this.pessoa = pessoa;
        this.lotacao = lotacao;
    }

    @Override
    public String getInitials() {
        if (pessoa != null)
            return pessoa.getSigla();
        if (lotacao != null)
            return lotacao.getSiglaCompleta();
        return null;
    }

    public String getCodigo() {
        if (pessoa != null)
            return pessoa.getSiglaCompleta();
        if (lotacao != null)
            return "@" + lotacao.getSiglaCompleta();
        return null;
    }

    public CpOrgaoUsuario getOrgaoUsuario() {
        return orgaoUsuario;
    }

    public void setOrgaoUsuario(CpOrgaoUsuario orgaoUsuario) {
        this.orgaoUsuario = orgaoUsuario;
    }

    public java.lang.Long getId() {
        return id;
    }

    public void setId(java.lang.Long id) {
        this.id = id;
    }

    public WfDefinicaoDeResponsavel getDefinicaoDeResponsavel() {
        return definicaoDeResponsavel;
    }

    public void setDefinicaoDeResponsavel(WfDefinicaoDeResponsavel definicaoDeResponsavel) {
        this.definicaoDeResponsavel = definicaoDeResponsavel;
    }

    public DpPessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(DpPessoa pessoa) {
        this.pessoa = pessoa;
    }

    public DpLotacao getLotacao() {
        return lotacao;
    }

    public void setLotacao(DpLotacao lotacao) {
        this.lotacao = lotacao;
    }

    public java.lang.String getHisIde() {
        return hisIde;
    }

    public void setHisIde(java.lang.String hisIde) {
        this.hisIde = hisIde;
    }

    @Override
    public String getIdSincronizacao() {
        return criarIdExterna();
    }

    @PostLoad
    public void postLoad() {
        this.setHisIde(criarIdExterna());
    }

    public String criarIdExterna() {
        return Long.toString(this.getDefinicaoDeResponsavel().getIdInicial()) + "-"
                + Long.toString(this.getOrgaoUsuario().getId());
    }

    public String getTooltip() {
        if (pessoa != null)
            return pessoa.getNomePessoa();
        if (lotacao != null)
            return lotacao.getNomeLotacao();
        return null;
    }

}
