package br.gov.jfrj.siga.ex.logic;

import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.ex.ExMobil;
import br.gov.jfrj.siga.ex.bl.ExMobilBL;
import com.crivano.jlogic.Expression;
import com.crivano.jlogic.JLogic;

import javax.enterprise.inject.spi.CDI;

public class ExEstaResponsavel implements Expression {

    private ExMobil mob;
    private final DpPessoa titular;
    private final DpLotacao lotaTitular;
    private final ExMobilBL mobBl;

    public ExEstaResponsavel(ExMobil mob, DpPessoa titular, DpLotacao lotaTitular) {
        this.mob = mob;
        mobBl = CDI.current().select(ExMobilBL.class).get();

        if (this.mob != null && this.mob.isGeral()) {
            if (this.mob.doc().isProcesso())
                this.mob = this.mob.doc().getUltimoVolume();
            else {
                for (ExMobil m : this.mob.doc().getExMobilSet()) {
                    if (!m.isGeral() && mobBl.isAtendente(m, titular, lotaTitular)) {
                        this.mob = m;
                        break;
                    }
                }
            }
        }

        this.titular = titular;
        this.lotaTitular = lotaTitular;
    }

    @Override
    public boolean eval() {
        return mobBl.isAtendente(mob, titular, lotaTitular);
    }

    @Override
    public String explain(boolean result) {
        return (titular != null ? titular.getSiglaCompleta() + "/" : "") + lotaTitular.getSiglaCompleta() + (result ? "" : JLogic.NOT)
                + " é responsável por " + mob.getCodigo();
    }
}
