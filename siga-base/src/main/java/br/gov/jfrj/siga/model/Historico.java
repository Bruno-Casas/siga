package br.gov.jfrj.siga.model;

import java.util.Date;

public interface Historico<T extends Historico<T>> {
    Long getIdInicial();

    boolean equivale(Object other);

    Long getId();

    void setId(Long id);

    Long getHisIdIni();

    void setHisIdIni(Long hisIdIni);

    Date getHisDtIni();

    void setHisDtIni(Date hisDtIni);

    Date getHisDtFim();

    void setHisDtFim(Date hisDtFim);

    Integer getHisAtivo();

    void setHisAtivo(Integer hisAtivo);

    T getHistoricoInicial();

    T getHistoricoAtual();
}