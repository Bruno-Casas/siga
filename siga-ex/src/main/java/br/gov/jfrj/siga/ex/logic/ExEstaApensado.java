package br.gov.jfrj.siga.ex.logic;

import br.gov.jfrj.siga.ex.ExMobil;
import com.crivano.jlogic.Expression;
import com.crivano.jlogic.JLogic;

public class ExEstaApensado implements Expression {
    ExMobil mob;

    public ExEstaApensado(ExMobil mob) {
        this.mob = mob;
    }

    @Override
    public boolean eval() {
        return mob.isApensado();
    }

    @Override
    public String explain(boolean result) {
        return JLogic.explain("está apensado", result);
    }

}
