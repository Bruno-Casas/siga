package br.gov.jfrj.siga.ex.api.v1;

import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.cp.CpToken;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.ex.ExMobil;
import br.gov.jfrj.siga.ex.ExMovimentacao;
import br.gov.jfrj.siga.ex.ExNivelAcesso;
import br.gov.jfrj.siga.ex.logic.ExPodeGerarLinkPublicoDoProcesso;
import br.gov.jfrj.siga.ex.model.enm.ExTipoDeMovimentacao;
import br.gov.jfrj.siga.hibernate.ExDao;
import br.gov.jfrj.siga.persistencia.ExMobilDaoFiltro;
import br.gov.jfrj.siga.vraptor.Transacional;

import java.util.Date;
import java.util.Set;

@Transacional
public class DocumentosSiglaGerarLinkPublicoPost implements IExApiV1.IDocumentosSiglaGerarLinkPublicoPost {

    @Override
    public void run(Request req, Response resp, ExApiV1Context ctx) throws Exception {
        DpPessoa cadastrante = ctx.getCadastrante();
        DpPessoa titular = ctx.getTitular();
        DpLotacao lotaTitular = ctx.getLotaTitular();

        final ExMobilDaoFiltro filter = new ExMobilDaoFiltro();
        filter.setSigla(req.sigla);
        ExMobil mob = dao.consultarPorSigla(filter);
        if (mob == null)
            throw new AplicacaoException(
                    "Não foi possível encontrar o documento a partir da sigla " + req.sigla);

        Set<ExMovimentacao> movs = mob.getMovsNaoCanceladas(ExTipoDeMovimentacao.GERAR_LINK_PUBLICO_PROCESSO);
        if (!movs.isEmpty())
            throw new AplicacaoException("Link público do documento " + mob.getSigla() + " já foi gerado anteriormente.");

        Ex.getInstance().getComp().afirmar(
                "Não é possível gerar o link para o documento " + mob.getSigla(),
                ExPodeGerarLinkPublicoDoProcesso.class, cadastrante, lotaTitular, mob
        );

        Date dtMov = dao.dt();

        //Gera o link público
        CpToken cpToken = this.bl.gerarUrlPermanente(mob.getDoc().getIdDoc());
        String link = this.bl.obterURLPermanente(cpToken.getIdTpToken().toString(), cpToken.getToken());

        // Redefinição do nível de acesso para público
        ExNivelAcesso nivelAcessoPublico = dao.consultar(ExNivelAcesso.ID_PUBLICO, ExNivelAcesso.class, false);
        if (mob.getDoc().getExNivelAcessoAtual() != nivelAcessoPublico) {
            this.bl.redefinirNivelAcesso(cadastrante, lotaTitular, mob.getDoc(), dtMov, lotaTitular,
                    cadastrante, cadastrante, cadastrante, null, nivelAcessoPublico);
        }

        this.bl.gravarMovimentacaoLinkPublico(cadastrante, titular, lotaTitular, mob);

        resp.link = link;
    }

    @Override
    public String getContext() {
        return "gerar link público do documento";
    }

}
