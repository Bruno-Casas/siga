package br.gov.jfrj.siga.cp.model;

import br.gov.jfrj.siga.cp.CpIdentidade;
import br.gov.jfrj.siga.model.Historico;

public interface HistoricoAuditavel<T extends Historico<T>> extends Historico<T> {

    CpIdentidade getHisIdcIni();

    void setHisIdcIni(CpIdentidade hisIdcIni);

    CpIdentidade getHisIdcFim();

    void setHisIdcFim(CpIdentidade hisIdcFim);
}
