package br.gov.jfrj.siga.ex.logic;

import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.ex.ExMobil;
import com.crivano.jlogic.Expression;
import com.crivano.jlogic.JLogic;

public class ExEstaEmTransito implements Expression {

    private ExMobil mob;
    private DpPessoa titular;
    private DpLotacao lotaTitular;

    public ExEstaEmTransito(ExMobil mob, DpPessoa titular, DpLotacao lotaTitular) {
        this.mob = mob;
        this.titular = titular;
        this.lotaTitular = lotaTitular;
    }

    @Override
    public boolean eval() {
        return mob.isEmTransito(titular, lotaTitular);
    }

    @Override
    public String explain(boolean result) {
        return mob.getCodigo() + (result ? "" : JLogic.NOT) + " está em trânsito";
    }
}