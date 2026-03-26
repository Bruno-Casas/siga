package br.gov.jfrj.siga.ex.util;

import br.gov.jfrj.siga.dp.CpFeriado;
import br.gov.jfrj.siga.hibernate.ExDao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class Feriados {

    @Inject
    private ExDao dao;

    private static List<CpFeriado> listaFeriados;

    public List<CpFeriado> getListaFeriados() {
        if (listaFeriados == null || listaFeriados.isEmpty()) {
            listaFeriados = dao.listarFeriados();
        }
        return listaFeriados;
    }

    public CpFeriado verificarPorData(Date dt) {

        for (CpFeriado f : getListaFeriados())
            if (f.abrange(dt))
                return f;
        return null;
    }
}
