package br.gov.jfrj.siga.model;

import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@MappedSuperclass
public abstract class HistoricoSuporte<T extends Historico<T>> extends Objeto implements Historico<T> {
    @Column(name = "HIS_ID_INI")
    private Long hisIdIni;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "HIS_DT_INI")
    private Date hisDtIni;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "HIS_DT_FIM")
    private Date hisDtFim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HIS_ID_INI", referencedColumnName = "ID")
    private T historicoInicial;

    @OneToMany(mappedBy = "historicoInicial", fetch = FetchType.LAZY)
    @Where(clause = "HIS_DT_FIM IS NULL")
    private List<T> historicoAtual;

    @Transient
    private Integer hisAtivoFake;

    /**
     * Atribui o hisAtivo já que o mesmo é sempre calculado
     */
    public void updateAtivo() {
        this.hisAtivoFake = this.hisDtFim == null ? 1 : 0;
    }

    public Long getHisIdIni() {
        return hisIdIni;
    }

    public void setHisIdIni(Long hisIdIni) {
        this.hisIdIni = hisIdIni;
    }

    public Date getHisDtIni() {
        return hisDtIni;
    }

    public void setHisDtIni(Date hisDtIni) {
        this.hisDtIni = hisDtIni;
    }

    public Date getHisDtFim() {
        this.updateAtivo();
        return hisDtFim;
    }

    public void setHisDtFim(Date hisDtFim) {
        this.hisDtFim = hisDtFim;
        this.updateAtivo();
    }

    public Integer getHisAtivo() {
        this.updateAtivo();
        return hisAtivoFake;
    }

    public boolean isAtivo() {
        return this.hisDtFim == null;
    }

    public void setHisAtivo(Integer hisAtivo) {
        this.updateAtivo();
    }

    public boolean equivale(Object other) {
        if (other == null)
            return false;
        return this.getHisIdIni().equals(((Historico<?>) other).getIdInicial());
    }

    public Long getIdInicial() {
        return getHisIdIni();
    }

    /**
     * Verifica se estava ativo em uma determinada data
     *
     * @param dt data em que se quer verificar se estava ativo
     * @return true or false
     */
    public boolean ativoNaData(Date dt) {
        if (dt == null)
            return this.getHisDtFim() == null;

        if (this.getHisDtIni() != null && dt.before(this.getHisDtIni()))
            return false;

        if (this.getHisDtFim() == null)
            return true;

        return dt.before(this.getHisDtFim());
    }
    
    @Override
    public T getHistoricoInicial() {
        return historicoInicial;
    }


    @SuppressWarnings("unchecked")
    public T getHistoricoAtual() {
        if (this.equals(historicoInicial) || this.historicoInicial == null) {
            return (this.getHisDtFim() == null) ? (T) this : historicoAtual.get(0);
        }

        return this.historicoInicial.getHistoricoAtual();
    }
}