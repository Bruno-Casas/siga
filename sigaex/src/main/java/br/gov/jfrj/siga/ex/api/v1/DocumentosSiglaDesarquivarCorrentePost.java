package br.gov.jfrj.siga.ex.api.v1;

import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.ex.ExMobil;
import br.gov.jfrj.siga.ex.api.v1.IExApiV1.IDocumentosSiglaDesarquivarCorrentePost;
import br.gov.jfrj.siga.ex.bl.Ex;
import br.gov.jfrj.siga.ex.logic.ExPodeDesarquivarCorrente;
import br.gov.jfrj.siga.vraptor.Transacional;

@Transacional
public class DocumentosSiglaDesarquivarCorrentePost implements IDocumentosSiglaDesarquivarCorrentePost {

    @Override
    public void run(Request req, Response resp, ExApiV1Context ctx) throws Exception {
        DpPessoa cadastrante = ctx.getCadastrante();
        DpLotacao lotaCadastrante = cadastrante.getLotacao();
        DpLotacao lotaTitular = ctx.getLotaTitular();

        ExMobil mob = ctx.buscarEValidarMobil(req.sigla, req, resp, "Documento a Desarquivar");

        Ex.getInstance().getComp().afirmar(
                "O documento " + mob.getSigla() + " não pode ser desarquivado do corrente por "
                        + cadastrante.getSiglaCompleta() + "/" + lotaTitular.getSiglaCompleta(),
                ExPodeDesarquivarCorrente.class, cadastrante, lotaTitular, mob);

        ctx.assertAcesso(mob, cadastrante, lotaTitular);

        Ex.getInstance().getBL().desarquivarCorrente(cadastrante, lotaCadastrante, mob, null, cadastrante);

        resp.sigla = mob.doc().getCodigo();
        resp.status = "OK";
    }

    @Override
    public String getContext() {
        return "Desarquivar do Corrente";
    }

}
