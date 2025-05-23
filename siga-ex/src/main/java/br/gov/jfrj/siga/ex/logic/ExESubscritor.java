package br.gov.jfrj.siga.ex.logic;

import br.gov.jfrj.siga.base.util.Utils;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.ex.ExDocumento;
import com.crivano.jlogic.Expression;
import com.crivano.jlogic.JLogic;

public class ExESubscritor implements Expression {

    private ExDocumento doc;
    private DpPessoa titular;
    private DpLotacao lotaTitular;

    public ExESubscritor(ExDocumento doc, DpPessoa titular) {
        this(doc, titular, null);
    }

    public ExESubscritor(ExDocumento doc, DpLotacao lotaTitular) {
        this(doc, null, lotaTitular);
    }

    public ExESubscritor(ExDocumento doc, DpPessoa titular, DpLotacao lotaTitular) {
        this.doc = doc;
        this.titular = titular;
        this.lotaTitular = lotaTitular;
    }

    @Override
    public boolean eval() {
        return Utils.equivale(doc.getSubscritor(), titular) || Utils.equivale(doc.getLotaSubscritor(), lotaTitular);
    }

    @Override
    public String explain(boolean result) {
        return (titular != null && lotaTitular != null
                ? titular.getSiglaCompleta() + "/" + lotaTitular.getSiglaCompleta()
                : titular != null ? titular.getSiglaCompleta()
                : lotaTitular != null ? lotaTitular.getSiglaCompleta() : "")

                + (result ? "" : JLogic.NOT) + " é subscritor de " + doc.getCodigo();
    }
}
