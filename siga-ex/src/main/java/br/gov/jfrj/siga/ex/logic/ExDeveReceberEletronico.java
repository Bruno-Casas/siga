package br.gov.jfrj.siga.ex.logic;

import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.ex.ExMobil;
import br.gov.jfrj.siga.ex.ExMovimentacao;
import br.gov.jfrj.siga.ex.model.enm.ExTipoDeMovimentacao;
import com.crivano.jlogic.*;

public class ExDeveReceberEletronico extends CompositeExpressionSupport {

    private final ExMobil mob;
    private final DpPessoa titular;
    private final DpLotacao lotaTitular;

    public ExDeveReceberEletronico(ExMobil mob, DpPessoa titular, DpLotacao lotaTitular) {
        if (mob.isGeralDeProcesso() && mob.doc().isFinalizado())
            mob = mob.doc().getUltimoVolume();
        this.mob = mob;
        this.titular = titular;
        this.lotaTitular = lotaTitular;
    }

    // Deve receber só se o usuário não acabou de fazer um trâmite para sua própria
    // lotação
    @Override
    protected Expression create() {
        return And.of(
                Not.of(new ExEstaEmTransitoExterno(mob)),
                new ExPodeReceberEletronico(mob, titular, lotaTitular),

                new Expression() {

                    @Override
                    public String explain(boolean result) {
                        return JLogic.explain("acabou de ser transferido para a própria lotação", result);
                    }

                    @Override
                    public boolean eval() {
                        ExMovimentacao ultMov = mob.getUltimaMovimentacaoNaoCancelada();
                        return !(
                                ultMov.getExTipoMovimentacao() == ExTipoDeMovimentacao.TRANSFERENCIA
                                        && ultMov.getCadastrante() != null
                                        && ultMov.getCadastrante().equivale(titular)
                                        && ultMov.getLotaResp() != null
                                        && ultMov.getLotaResp().equivale(lotaTitular)
                        );
                    }
                }
        );
    }

}
