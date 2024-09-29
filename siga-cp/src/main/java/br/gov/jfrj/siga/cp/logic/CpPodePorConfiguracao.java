package br.gov.jfrj.siga.cp.logic;

import br.gov.jfrj.siga.cp.bl.Cp;
import br.gov.jfrj.siga.cp.model.enm.CpSituacaoDeConfiguracaoEnum;
import br.gov.jfrj.siga.cp.model.enm.ITipoDeConfiguracao;
import br.gov.jfrj.siga.dp.*;
import com.crivano.jlogic.Expression;
import com.crivano.jlogic.JLogic;

public class CpPodePorConfiguracao implements Expression {

    private final DpLotacao dpLotacao;
    private final DpPessoa dpPessoa;
    private ITipoDeConfiguracao idTpConf;
    private DpPessoa pessoaObjeto;
    private DpLotacao lotacaoObjeto;
    private DpCargo cargoObjeto;
    private DpFuncaoConfianca funcaoConfiancaObjeto;
    private CpOrgaoUsuario orgaoObjeto;

    public CpPodePorConfiguracao(DpPessoa titular, DpLotacao lotaTitular) {
        super();
        this.dpPessoa = titular;
        this.dpLotacao = lotaTitular;
    }

    @Override
    public boolean eval() {
        CpSituacaoDeConfiguracaoEnum situacao = Cp.getInstance().getConf().situacaoPorConfiguracao(null, null, null,
                null, dpLotacao, dpPessoa, null, idTpConf, pessoaObjeto, lotacaoObjeto,
                null, cargoObjeto, funcaoConfiancaObjeto, orgaoObjeto);

        if (situacao == null)
            return false;
        if (situacao == CpSituacaoDeConfiguracaoEnum.PODE)
            return true;
        if (situacao == CpSituacaoDeConfiguracaoEnum.DEFAULT)
            return true;

        return situacao == CpSituacaoDeConfiguracaoEnum.OBRIGATORIO;
    }

    @Override
    public String explain(boolean result) {
        return JLogic.explain(
                "pode" + (idTpConf != null ? " " + idTpConf.getDescr().toLowerCase() : "") + " por configuração",
                result);
    }

    public CpPodePorConfiguracao withPessoaObjeto(DpPessoa pessoaObjeto) {
        this.pessoaObjeto = pessoaObjeto;
        return this;
    }

    public CpPodePorConfiguracao withIdTpConf(ITipoDeConfiguracao idTpConf) {
        this.idTpConf = idTpConf;
        return this;
    }

    public CpPodePorConfiguracao withLotacaoObjeto(DpLotacao lotacaoObjeto) {
        this.lotacaoObjeto = lotacaoObjeto;
        return this;
    }

    public CpPodePorConfiguracao withCargoObjeto(DpCargo cargoObjeto) {
        this.cargoObjeto = cargoObjeto;
        return this;
    }

    public CpPodePorConfiguracao withFuncaoConfiancaObjeto(DpFuncaoConfianca funcaoConfiancaObjeto) {
        this.funcaoConfiancaObjeto = funcaoConfiancaObjeto;
        return this;
    }

    public CpPodePorConfiguracao withOrgaoObjeto(CpOrgaoUsuario orgaoObjeto) {
        this.orgaoObjeto = orgaoObjeto;
        return this;
    }

}