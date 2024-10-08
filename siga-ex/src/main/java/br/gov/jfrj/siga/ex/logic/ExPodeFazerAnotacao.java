package br.gov.jfrj.siga.ex.logic;

import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.ex.ExMobil;
import br.gov.jfrj.siga.ex.model.enm.ExTipoDeMovimentacao;
import com.crivano.jlogic.And;
import com.crivano.jlogic.CompositeExpressionSupport;
import com.crivano.jlogic.Expression;
import com.crivano.jlogic.Not;

public class ExPodeFazerAnotacao extends CompositeExpressionSupport {

    private final ExMobil mob;
    private final DpPessoa titular;
    private final DpLotacao lotaTitular;

    public ExPodeFazerAnotacao(ExMobil mob, DpPessoa titular, DpLotacao lotaTitular) {
        this.mob = mob;
        this.titular = titular;
        this.lotaTitular = lotaTitular;
    }

    /**
     * Retorna se é possível fazer anotação no móbil. Basta o móbil não estar
     * eliminado, não estar em trânsito, não ser geral e não haver configuração
     * impeditiva, o que significa que, tendo acesso a um documento não eliminado
     * fora de trânsito, qualquer usuário pode fazer anotação.
     */
    @Override
    protected Expression create() {
        return And.of(

                Not.of(new ExEstaEmTransitoInterno(mob)),

                Not.of(new ExEstaEliminado(mob)),

                Not.of(new ExEMobilGeral(mob)),

                new ExPodeMovimentarPorConfiguracao(ExTipoDeMovimentacao.ANOTACAO, titular,
                        lotaTitular));
    }
}
