package br.gov.jfrj.siga.dp;

import br.gov.jfrj.siga.cp.model.HistoricoAuditavelSuporte;
import br.gov.jfrj.siga.model.HistoricoSuporte;

public abstract class DpResponsavel<T extends HistoricoSuporte<T>> extends HistoricoAuditavelSuporte<T> {
	abstract public String getSigla();

	abstract public String getSiglaCompleta();
	
	abstract public String getSiglaDePessoaEOuLotacao();
}
