package br.gov.jfrj.siga.cp.model;

import br.gov.jfrj.siga.cp.CpIdentidade;
import br.gov.jfrj.siga.model.Historico;
import br.gov.jfrj.siga.model.HistoricoSuporte;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class HistoricoAuditavelSuporte<T extends Historico<T>> extends HistoricoSuporte<T> implements HistoricoAuditavel<T> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HIS_IDC_INI")
    private CpIdentidade hisIdcIni;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HIS_IDC_FIM")
    private CpIdentidade hisIdcFim;

    public CpIdentidade getHisIdcIni() {
        return hisIdcIni;
    }

    public void setHisIdcIni(CpIdentidade hisIdcIni) {
        this.hisIdcIni = hisIdcIni;
    }

    public CpIdentidade getHisIdcFim() {
        return hisIdcFim;
    }

    public void setHisIdcFim(CpIdentidade hisIdcFim) {
        this.hisIdcFim = hisIdcFim;
    }
}
