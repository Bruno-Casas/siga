package br.gov.jfrj.siga.ex.logic;

import br.gov.jfrj.siga.ex.bl.ExDocumentoBL;
import com.crivano.jlogic.Expression;
import com.crivano.jlogic.JLogic;

import br.gov.jfrj.siga.ex.ExDocumento;

import javax.enterprise.inject.spi.CDI;

public class ExEstaComPrazoDeAssinaturaVencido implements Expression {
    private final ExDocumento doc;
    private final ExDocumentoBL docBl;

    public ExEstaComPrazoDeAssinaturaVencido(ExDocumento doc) {
        this.doc = doc;
        this.docBl = CDI.current().select(ExDocumentoBL.class).get();
    }

    @Override
    public boolean eval() {
        return docBl.isPrazoDeAssinaturaVencido(doc);
    }

    @Override
    public String explain(boolean result) {
        return JLogic.explain("está com prazo de assinatura vencido", result);
    }

}
