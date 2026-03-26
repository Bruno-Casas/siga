package br.gov.jfrj.siga.sr.model;

import br.gov.jfrj.siga.model.Historico;
import br.gov.jfrj.siga.model.HistoricoSuporte;
import br.gov.jfrj.siga.model.dao.ModeloDao;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;
import java.util.Date;

@Specializes
public class SrDao extends ModeloDao {

    public <T extends Historico<T>>void salvarComHistorico(T target) throws Exception {
        target.setHisDtIni(new Date());
        target.setHisDtFim(null);
        if (target.getId() == null) {
            gravar(target);
            target.setHisIdIni(target.getId());
        } else {
            em.detach(target);
            T thisAntigo = (T) em.find(target.getClass(), target.getId());

            //Edson: caso a instância esteja fechada, obtém a última
            if (thisAntigo.getHisDtFim() != null) {
                thisAntigo = target.getHistoricoAtual();
            }

            if (thisAntigo.getHisDtFim() == null)
                finalizar(thisAntigo);


            target.setHisIdIni(((Historico) thisAntigo).getHisIdIni());
            target.setId(null);
        }
        gravar(target);
        //this.refresh();
    }
}
