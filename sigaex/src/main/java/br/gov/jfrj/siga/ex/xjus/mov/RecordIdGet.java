package br.gov.jfrj.siga.ex.xjus.mov;

import br.gov.jfrj.siga.base.HtmlToPlainText;
import br.gov.jfrj.siga.base.Prop;
import br.gov.jfrj.siga.ex.ExDocumento;
import br.gov.jfrj.siga.ex.ExMovimentacao;
import br.gov.jfrj.siga.ex.bl.ExAcesso;
import br.gov.jfrj.siga.ex.bl.ExBL;
import br.gov.jfrj.siga.ex.model.enm.ExTipoDeMovimentacao;
import br.gov.jfrj.siga.ex.util.PdfToPlainText;
import br.gov.jfrj.siga.hibernate.ExDao;
import br.jus.trf2.xjus.record.api.IXjusRecordAPI;
import br.jus.trf2.xjus.record.api.IXjusRecordAPI.Facet;
import br.jus.trf2.xjus.record.api.IXjusRecordAPI.Field;
import br.jus.trf2.xjus.record.api.XjusRecordAPIContext;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;

public class RecordIdGet implements IXjusRecordAPI.IRecordIdGet {

    @Inject
    private ExDao dao;

    @Inject
    private ExBL bl;

    @Override
    public void run(Request req, Response resp, XjusRecordAPIContext ctx) throws Exception {
        long primaryKey;
        try {
            primaryKey = Long.parseLong(req.id);
        } catch (NumberFormatException nfe) {
            throw new RuntimeException("REMOVED");
        }
        ExMovimentacao mov = dao.consultar(primaryKey, ExMovimentacao.class, false);

        if (mov == null || mov.isCancelada()) {
            throw new RuntimeException("REMOVED");
        }

        ExDocumento doc = mov.getExDocumento();

        if (doc == null || doc.isCancelado()) {
            throw new RuntimeException("REMOVED");
        }

        resp.id = req.id;
        resp.url = Prop.get("/xjus.permalink.url") + doc.getCodigoCompacto() + "/" + mov.getIdMov();
        resp.acl = "PUBLIC";
        resp.refresh = "NEVER";
        resp.code = doc.getCodigo();
        resp.title = doc.getDescrDocumento();
        resp.field = new ArrayList<>();
        resp.facet = new ArrayList<>();
        resp.refresh = "NEVER";

        addMetadataForMov(doc, mov, resp);
        addAclForDoc(doc, resp);

        String html = mov.getHtml();
        if (html != null) {
            resp.content = HtmlToPlainText.getText(html).trim();
            return;
        }

        byte[] pdf = mov.getPdf();
        if (pdf != null) {
            resp.content = PdfToPlainText.getText(pdf);
        }
    }

    private void addAclForDoc(ExDocumento doc, Response resp) {
        if (doc.getDnmAcesso() == null || doc.isDnmAcessoMAisAntigoQueODosPais()) {
            this.bl.atualizarDnmAcesso(doc);
        }
        String sAcessos = doc.getDnmAcesso();
        if (sAcessos == null) {
            Date dt = dao.dt();
            ExAcesso acesso = new ExAcesso(dao);
            sAcessos = acesso.getAcessosString(doc, dt);
            if (sAcessos == null || sAcessos.trim().isEmpty())
                throw new RuntimeException("Não foi possível calcular os acesos de " + doc.getSigla());
        }
        resp.acl = sAcessos.replace(ExAcesso.ACESSO_PUBLICO, "PUBLIC");
    }

    public void addField(Response resp, String name, String value) {
        Field fld = new Field();
        fld.kind = "TEXT";
        fld.name = name;
        fld.value = value;
        resp.field.add(fld);
    }

    public void addFacet(Response resp, String name, String value) {
        Facet facet = new Facet();
        facet.kind = "KEYWORD";
        facet.name = name;
        facet.value = value;
        resp.facet.add(facet);
    }

    public void addFieldAndFacet(Response resp, String name, String value) {
        addField(resp, name, value);
        addFacet(resp, name, value);
    }

    private void addMetadataForMov(ExDocumento doc, ExMovimentacao mov, Response resp) {
        addFacet(resp, "tipo", "Documento");
        addFieldAndFacet(resp, "orgao", doc.getOrgaoUsuario().getAcronimoOrgaoUsu());
        addField(resp, "codigo", doc.getCodigo() + ":" + mov.getIdMov());
        if (doc.getExTipoDocumento() != null) {
            addFieldAndFacet(resp, "origem",
                    mov.getExTipoMovimentacao().equals(ExTipoDeMovimentacao.ANEXACAO) ? "Anexo"
                            : "Despacho Curto");

        }
        if (doc.getDnmExNivelAcesso() != null)
            addField(resp, "acesso", doc.getDnmExNivelAcesso().getNmNivelAcesso());
        if (mov.getDtMovYYYYMMDD() != null) {
            addField(resp, "data", mov.getDtMovYYYYMMDD());
            addFacet(resp, "ano", mov.getDtMovYYYYMMDD().substring(0, 4));
            addFacet(resp, "mes", mov.getDtMovYYYYMMDD().substring(5, 7));
        }
        if (mov.getLotaSubscritor() != null)
            addFieldAndFacet(resp, "subscritor_lotacao", mov.getLotaSubscritor().getSiglaLotacao());
        if (mov.getSubscritor() != null)
            addField(resp, "subscritor", mov.getSubscritor().getNomePessoa());
    }

    public String getContext() {
        return "obter a lista de índices";
    }
}
