package br.gov.jfrj.siga.cp.logic;

import br.gov.jfrj.siga.cp.model.enm.CpTipoDeConfiguracao;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import com.crivano.jlogic.CompositeExpressionSupport;
import com.crivano.jlogic.Expression;

public class CpExibirEmCampoDePesquisa extends CompositeExpressionSupport {

    private final DpPessoa pessoa;
    private final DpLotacao lotacao;
    private final CpOrgaoUsuario orgao;

    public CpExibirEmCampoDePesquisa(final DpPessoa pessoa, final DpLotacao lotacao, final CpOrgaoUsuario orgao) {
        this.pessoa = pessoa;
        this.lotacao = lotacao;
        this.orgao = orgao;
    }

    @Override
    protected Expression create() {
        return new CpPodePorConfiguracao(pessoa, lotacao)
                .withIdTpConf(CpTipoDeConfiguracao.EXIBIR_EM_CAMPO_DE_BUSCA)
                .withPessoaObjeto(pessoa)
                .withLotacaoObjeto(lotacao)
                .withCargoObjeto(pessoa.getCargo())
                .withFuncaoConfiancaObjeto(pessoa.getFuncaoConfianca())
                .withOrgaoObjeto(orgao);
    }

}