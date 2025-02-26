package br.gov.jfrj.siga.ex.logic;

import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.ex.ExMobil;
import br.gov.jfrj.siga.ex.model.enm.ExTipoDeConfiguracao;
import br.gov.jfrj.siga.ex.model.enm.ExTipoDeMovimentacao;
import com.crivano.jlogic.*;

public class ExPodeNotificar extends CompositeExpressionSupport {

    private final ExMobil mob;
    private final DpPessoa titular;
    private final DpLotacao lotaTitular;

    public ExPodeNotificar(ExMobil mob, DpPessoa titular, DpLotacao lotaTitular) {
        this.mob = mob;
        this.titular = titular;
        this.lotaTitular = lotaTitular;
    }

    @Override
    protected Expression create() {

        return And.of(

                new ExPodeAcessarDocumento(mob, titular, lotaTitular),

                Not.of(new ExEstaPendenteDeAnexacao(mob)),

                Or.of(new ExEMobilVia(mob), new ExEMobilVolume(mob)),

                Not.of(new ExEstaJuntado(mob)),

                Not.of(new ExEstaApensadoAVolumeDoMesmoProcesso(mob)),

                Not.of(new ExEstaArquivado(mob)),

                Or.of(

                        Not.of(new ExEstaPendenteDeAssinatura(mob.doc())),

                        new ExEExterno(mob.doc()),

                        And.of(

                                new ExEProcesso(mob.doc()),

                                new ExEInternoFolhaDeRosto(mob.doc()))),

                Not.of(new ExEstaSobrestado(mob)),

                Not.of(new ExEstaEmEditalDeEliminacao(mob)),

                Not.of(new ExEstaSemEfeito(mob.doc())),

                new ExPodePorConfiguracao(titular, lotaTitular).withIdTpConf(ExTipoDeConfiguracao.MOVIMENTAR)
                        .withExTpMov(ExTipoDeMovimentacao.NOTIFICACAO));
    }
}
