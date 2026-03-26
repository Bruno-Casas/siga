package br.gov.jfrj.siga.ex.logic;

import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.ex.ExMobil;
import br.gov.jfrj.siga.ex.bl.ExMobilBL;
import com.crivano.jlogic.Expression;
import com.crivano.jlogic.JLogic;

import javax.enterprise.inject.spi.CDI;

public class ExEstaEmTransito implements Expression {

    private final ExMobil mob;
    private final DpPessoa titular;
    private final DpLotacao lotaTitular;
    private final ExMobilBL mobBl;

    public ExEstaEmTransito(ExMobil mob, DpPessoa titular, DpLotacao lotaTitular) {
        this.mob = mob;
        this.titular = titular;
        this.lotaTitular = lotaTitular;
        this.mobBl = CDI.current().select(ExMobilBL.class).get();
    }

    @Override
    public boolean eval() {
        return mobBl.isEmTransito(mob, titular, lotaTitular);
    }

    @Override
    public String explain(boolean result) {
        return mob.getCodigo() + (result ? "" : JLogic.NOT) + " está em trânsito";
    }
}
