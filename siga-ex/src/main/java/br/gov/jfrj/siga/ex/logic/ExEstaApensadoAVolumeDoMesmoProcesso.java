package br.gov.jfrj.siga.ex.logic;

import br.gov.jfrj.siga.ex.ExMobil;
import com.crivano.jlogic.Expression;
import com.crivano.jlogic.JLogic;

public class ExEstaApensadoAVolumeDoMesmoProcesso implements Expression {
    ExMobil mob;

    public ExEstaApensadoAVolumeDoMesmoProcesso(ExMobil mob) {
        this.mob = mob;
    }

    @Override
    public boolean eval() {
        return mob.isApensadoAVolumeDoMesmoProcesso();
    }

    @Override
    public String explain(boolean result) {
        return JLogic.explain("está apensado a volume do mesmo processo", result);
    }

}
