package br.gov.jfrj.siga.ex.logic;

import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.ex.ExDocumento;
import br.gov.jfrj.siga.ex.model.enm.ExTipoDeConfiguracao;
import br.gov.jfrj.siga.ex.model.enm.ExTipoDeMovimentacao;
import com.crivano.jlogic.*;

public class ExPodeAutenticarDocumento extends CompositeExpressionSupport {

    private final ExDocumento doc;
    private final DpPessoa titular;
    private final DpLotacao lotaTitular;

    /*
     * Retorna se pode autenticar um documento que só foi assinado com senha.
     *
     * @param titular
     *
     * @param lotaTitular
     *
     * @param mob
     *
     * @return
     *
     * @throws Exception
     */
    public ExPodeAutenticarDocumento(ExDocumento doc, DpPessoa titular, DpLotacao lotaTitular) {
        this.doc = doc;
        this.titular = titular;
        this.lotaTitular = lotaTitular;
    }

    @Override
    protected Expression create() {
        return And.of(
                new ExEEletronico(doc),
                Not.of(new ExEstaAutenticadoComTokenOuSenha(doc)),
                new ExPodePorConfiguracao(titular, lotaTitular)
                        .withExMod(doc.getExModelo())
                        .withExFormaDoc(doc.getExFormaDocumento()).withPessoaObjeto(doc.getSubscritor())
                        .withIdTpConf(ExTipoDeConfiguracao.MOVIMENTAR)
                        .withExTpMov(ExTipoDeMovimentacao.CONFERENCIA_COPIA_DOCUMENTO),
                Or.of(
                        new ExEExternoCapturado(doc),
                        new ExEInternoCapturado(doc),
                        new ExEstaAssinadoComSenha(doc)
                )
        );
    }
}
