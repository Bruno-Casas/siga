package br.gov.jfrj.siga.wf.model.enm;

import java.util.Date;

import br.gov.jfrj.siga.cp.CpPerfil;
import br.gov.jfrj.siga.cp.model.enm.CpParamCfg;
import br.gov.jfrj.siga.cp.model.enm.CpSituacaoDeConfiguracaoEnum;
import br.gov.jfrj.siga.cp.model.enm.CpTipoDeConfiguracao;
import br.gov.jfrj.siga.cp.model.enm.ITipoDeConfiguracao;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.wf.model.WfDefinicaoDeProcedimento;

public enum WfTipoDeConfiguracao implements ITipoDeConfiguracao {

	INSTANCIAR_PROCEDIMENTO(100, "Iniciar Procedimento",
			"Selecione órgão, lotação, pessoa, cargo ou função que tem permissão para iniciar um determinado diagrama, além das indicadas no próprio diagrama.",
			new Enum[] { CpParamCfg.PESSOA, CpParamCfg.LOTACAO, CpParamCfg.CARGO, CpParamCfg.FUNCAO, CpParamCfg.ORGAO,
					WfParamCfg.DEFINICAO_DE_PROCEDIMENTO },
			new Enum[] { WfParamCfg.DEFINICAO_DE_PROCEDIMENTO, CpParamCfg.SITUACAO },
			new CpSituacaoDeConfiguracaoEnum[] { CpSituacaoDeConfiguracaoEnum.PODE, CpSituacaoDeConfiguracaoEnum.NAO_PODE }, CpSituacaoDeConfiguracaoEnum.NAO_PODE, true),
	EDITAR_DEFINICAO_DE_PROCEDIMENTO(102, "Editar Diagrama",
			"Selecione órgão, lotação, pessoa, cargo ou função que tem permissão para editar um determinado diagrama, além das indicadas no próprio diagrama.",
			new Enum[] { CpParamCfg.PESSOA, CpParamCfg.LOTACAO, CpParamCfg.CARGO, CpParamCfg.FUNCAO, CpParamCfg.ORGAO,
					WfParamCfg.DEFINICAO_DE_PROCEDIMENTO },
			new Enum[] { CpParamCfg.SITUACAO },
			new CpSituacaoDeConfiguracaoEnum[] { CpSituacaoDeConfiguracaoEnum.PODE, CpSituacaoDeConfiguracaoEnum.NAO_PODE }, CpSituacaoDeConfiguracaoEnum.NAO_PODE, true);

	
	public static final long TIPO_CONFIG_DESIGNAR_TAREFA = 101;

	private final int id;
	private final String descr;
	private final String explicacao;
	private final Enum[] params;
	private final Enum[] obrigatorios;
	private final CpSituacaoDeConfiguracaoEnum[] situacoes;
	private final CpSituacaoDeConfiguracaoEnum situacaoDefault;
	private final boolean editavel;

	WfTipoDeConfiguracao(int id, String descr, String explicacao, Enum[] params, Enum[] obrigatorios,
			CpSituacaoDeConfiguracaoEnum[] situacoes, CpSituacaoDeConfiguracaoEnum situacaoDefault, boolean editavel) {
		this.id = id;
		this.descr = descr;
		this.explicacao = explicacao;
		this.params = params;
		this.obrigatorios = obrigatorios;
		this.situacoes = situacoes;
		this.situacaoDefault = situacaoDefault;
		this.editavel = editavel;
	}

	public int getId() {
		return id;
	}

	public String getDescr() {
		return this.descr;
	}

	public String getExplicacao() {
		return this.explicacao;
	}

	public static ITipoDeConfiguracao getById(Integer id) {
		if (id == null)
			return null;
		return CpTipoDeConfiguracao.getById(id);
	}

	public CpSituacaoDeConfiguracaoEnum[] getSituacoes() {
		return situacoes;
	}

	public Enum[] getObrigatorios() {
		return obrigatorios;
	}

	@Override
	public Enum[] getParams() {
		return params;
	}

	@Override
	public CpSituacaoDeConfiguracaoEnum getSituacaoDefault() {
		return situacaoDefault;
	}

	@Override
	public boolean isEditavel() {
		return editavel;
	}
}
