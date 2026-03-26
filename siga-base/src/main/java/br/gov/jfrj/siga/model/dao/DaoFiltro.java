package br.gov.jfrj.siga.model.dao;


import javax.enterprise.inject.spi.CDI;

public class DaoFiltro {
    protected ModeloDao dao;

    public DaoFiltro() {
        super();

        dao = CDI.current().select(ModeloDao.class).get();
    }
}
