package br.gov.jfrj.siga.ex.logic;

import br.gov.jfrj.siga.cp.model.enm.CpTipoDeConfiguracao;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import com.crivano.jlogic.CompositeExpressionSupport;
import com.crivano.jlogic.Expression;

public class ExPodeUtilizarSegundoFatorPIN extends CompositeExpressionSupport {
    private DpPessoa titular;
    private DpLotacao lotaTitular;

    public ExPodeUtilizarSegundoFatorPIN(DpPessoa titular, DpLotacao lotaTitular) {
        this.titular = titular;
        this.lotaTitular = lotaTitular;
    }

    @Override
    protected Expression create() {
        return new ExPodePorConfiguracao(titular, lotaTitular).withIdTpConf(CpTipoDeConfiguracao.SEGUNDO_FATOR_PIN);
    }
}
