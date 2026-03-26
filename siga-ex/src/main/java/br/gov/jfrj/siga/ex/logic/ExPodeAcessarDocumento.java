package br.gov.jfrj.siga.ex.logic;

import br.gov.jfrj.siga.ex.bl.ExBL;
import br.gov.jfrj.siga.ex.bl.ExDocumentoBL;
import com.crivano.jlogic.And;
import com.crivano.jlogic.CompositeExpressionSupport;
import com.crivano.jlogic.Expression;
import com.crivano.jlogic.Or;

import br.gov.jfrj.siga.cp.logic.CpNaoENulo;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.ex.ExMobil;
import br.gov.jfrj.siga.ex.model.enm.ExTipoDeConfiguracao;

import javax.enterprise.inject.spi.CDI;

public class ExPodeAcessarDocumento extends CompositeExpressionSupport {

    private final ExMobil mob;
    private final DpPessoa titular;
    private final DpLotacao lotaTitular;
    private final ExDocumentoBL docBl;

    public ExPodeAcessarDocumento(ExMobil mob, DpPessoa titular, DpLotacao lotaTitular) {
        this.mob = mob;
        this.titular = titular;
        this.lotaTitular = lotaTitular;
        this. docBl = CDI.current().select(ExDocumentoBL.class).get();
    }

    @Override
    protected Expression create() {
        if (mob.doc().getDnmAcesso() == null || mob.doc().isDnmAcessoMAisAntigoQueODosPais()) {
            docBl.atualizarDnmAcesso(mob.doc());
        }

        return Or.of(
                And.of(
                        new CpNaoENulo(mob.doc().getOrgaoUsuario(), "órgão usuário"),
                        new ExPodePorConfiguracao(titular, lotaTitular)
                                .withIdTpConf(ExTipoDeConfiguracao.ACESSAR)
                                .withOrgaoObjeto(mob.doc().getOrgaoUsuario())
                ),
                new ExPodeAcessarPorConsulta(mob.doc(), titular, lotaTitular)
        );
    }
}
