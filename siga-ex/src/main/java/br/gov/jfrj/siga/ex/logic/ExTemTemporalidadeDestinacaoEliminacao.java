package br.gov.jfrj.siga.ex.logic;

import br.gov.jfrj.siga.ex.ExMobil;
import br.gov.jfrj.siga.ex.bl.ExMobilBL;
import com.crivano.jlogic.Expression;
import com.crivano.jlogic.JLogic;

import javax.enterprise.inject.spi.CDI;

public class ExTemTemporalidadeDestinacaoEliminacao implements Expression {
    private final ExMobil mob;
    private final ExMobilBL mobBl;

    public ExTemTemporalidadeDestinacaoEliminacao(ExMobil mob) {
        this.mob = mob;
        this.mobBl = CDI.current().select(ExMobilBL.class).get();
    }

    @Override
    public boolean eval() {
        return mobBl.isDestinacaoEliminacao(mob);
    }

    @Override
    public String explain(boolean result) {
        return JLogic.explain("temporalidade com destinação de eliminacao", result);
    }

}
