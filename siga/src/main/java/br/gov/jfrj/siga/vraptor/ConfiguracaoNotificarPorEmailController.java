package br.gov.jfrj.siga.vraptor;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.gov.jfrj.siga.cp.CpConfiguracao;
import br.gov.jfrj.siga.cp.CpServico;
import br.gov.jfrj.siga.cp.model.enm.CpServicosNotificacaoPorEmail;
import br.gov.jfrj.siga.cp.model.enm.CpSituacaoDeConfiguracaoEnum;
import br.gov.jfrj.siga.cp.model.enm.CpTipoDeConfiguracao;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ConfiguracaoNotificarPorEmailController extends GiControllerSupport {

    private static final Boolean UTILIZAVEL = true;

    @Get({"/app/notificarPorEmail/listar", "/public/app/page/usuario/listar"})
    public void listar() throws Exception {
        result.include("itens", colecao());
        result.include("servPai", CpServicosNotificacaoPorEmail.SIGACEMAIL.getSigla());
        result.include("recbObrigatorio", null);
        result.include("pode", CpSituacaoDeConfiguracaoEnum.PODE.getId());
        result.include("naoPode", CpSituacaoDeConfiguracaoEnum.NAO_PODE.getId());
        result.forwardTo("/WEB-INF/page/usuario/configuracaoNotificarEmail.jsp");
    }

    public List<CpConfiguracao> colecao() {
        final List<CpConfiguracao> configuracoes = new ArrayList<>();
        for (CpServicosNotificacaoPorEmail servicoDoUsuario : CpServicosNotificacaoPorEmail.values()) {
            CpServico servico = new CpServico();
            CpConfiguracao configuracao = new CpConfiguracao();
            if (servicoDoUsuario.getCondicao().equals(UTILIZAVEL)) {
                if (this.cpConf.podeUtilizarServicoPorConfiguracao(getCadastrante(), getCadastrante().getLotacao(), servicoDoUsuario.getChave()))
                    configuracao.setCpSituacaoConfiguracao(CpSituacaoDeConfiguracaoEnum.PODE);
                else
                    configuracao.setCpSituacaoConfiguracao(CpSituacaoDeConfiguracaoEnum.NAO_PODE);
            }
            servico.setSigla(servicoDoUsuario.getSigla());
            servico.setDscServico(servicoDoUsuario.getDescricao());
            configuracao.setCpServico(servico);
            configuracoes.add(configuracao);
        }
        return configuracoes;
    }

    @Transacional
    @Post({"/app/notificarPorEmail/editar"})
    public void editar(String siglaServ, Integer idSituacao) throws Exception {
        CpServico servico = null;
        DpPessoa pessoa = null;
        DpLotacao lotacao = null;
        pessoa = daoPes(getCadastrante().getId()).getPessoaInicial();
        lotacao = daoLot(getLotaCadastrante().getId()).getLotacaoInicial();
        servico = cpDao.consultarCpServicoPorChave(siglaServ);
        CpSituacaoDeConfiguracaoEnum situacao = CpSituacaoDeConfiguracaoEnum.getById(idSituacao);
        CpTipoDeConfiguracao tpConf = CpTipoDeConfiguracao.UTILIZAR_SERVICO;
        this.cpBl.configurarAcesso(null, getCadastrante().getOrgaoUsuario(),
                lotacao, pessoa, servico, situacao, tpConf, getIdentidadeCadastrante());
        result.redirectTo(ConfiguracaoNotificarPorEmailController.class).listar();
    }

}
