package br.gov.jfrj.siga.wf.logic;

import br.gov.jfrj.siga.wf.bl.WfCompetenciaBL;
import com.crivano.jlogic.Expression;
import com.crivano.jlogic.JLogic;

import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.wf.model.WfDefinicaoDeProcedimento;

import javax.enterprise.inject.spi.CDI;

public class WfPodeIniciarDiagramaPorConfiguracao implements Expression {

	private WfDefinicaoDeProcedimento pd;
	private final DpPessoa titular;
	private final DpLotacao lotaTitular;
	private final WfCompetenciaBL comp;

	public WfPodeIniciarDiagramaPorConfiguracao(WfDefinicaoDeProcedimento pd, DpPessoa titular, DpLotacao lotaTitular) {
		this.pd = pd;
		this.titular = titular;
		this.lotaTitular = lotaTitular;
		this.comp = CDI.current().select(WfCompetenciaBL.class).get();
	}

	@Override
	public boolean eval() {
		return comp.podeInstanciarProcedimentoPorConfiguracao(titular, lotaTitular, pd);
	}

	@Override
	public String explain(boolean result) {
		return JLogic.explain("tem configuração para iniciar", result);
	}
};