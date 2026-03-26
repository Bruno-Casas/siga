package br.gov.jfrj.siga.ex.util.BIE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.gov.jfrj.siga.ex.ExDocumento;
import br.gov.jfrj.siga.hibernate.ExDao;

import javax.enterprise.inject.spi.CDI;

public class ManipuladorEntrevista {
	
	private ExDocumento docBIE;
	
	public ManipuladorEntrevista(ExDocumento docBIE){
		this.docBIE = docBIE;
	}
	
	public List<ExDocumento> obterDocsMarcados(){
		return obterDocsMarcadosOuDesmarcados("Sim");
	}
	
	public List<ExDocumento> obterDocsNaoMarcados(){
		return obterDocsMarcadosOuDesmarcados("Nao");
	}
	
	private List<ExDocumento> obterDocsMarcadosOuDesmarcados(String marcadoSimOuNao) {
		Map<String, String> form = docBIE.getForm();
		final Pattern p = Pattern.compile("^doc_boletim?([0-9]{1,7})");
		long l;
		List<ExDocumento> list = new ArrayList<ExDocumento>();
		ExDocumento doqueRef = new ExDocumento();

		// TODO: Adequar ao CDI
		ExDao dao = CDI.current().select(ExDao.class).get();
		for (String chave : form.keySet()) {
			final Matcher m = p.matcher(chave);
			if (m.find()) {
				if (m.group(1) != null && form.get(chave).equals(marcadoSimOuNao)) {
					l = new Long(m.group(1));
					doqueRef = dao.consultar(l, ExDocumento.class, false);
					list.add(doqueRef);
				}
			}
		}
		return list;
	}

}
