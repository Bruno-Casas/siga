package br.gov.jfrj.siga.dp.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.model.DataTestBase;

public class CpDaoIntegrationTest extends DataTestBase {

	@Test
	public void testConsultarOrgao() {
		CpDao dao = null;
		
		// Criar dados de teste
		CpOrgaoUsuario orgao = new CpOrgaoUsuario();
		orgao.setIdOrgaoUsu(1L);
		orgao.setNmOrgaoUsu("Órgão Teste");
		orgao.setSiglaOrgaoUsu("TESTE");
		em.persist(orgao);
		
		// Testar consulta
		CpOrgaoUsuario consultado = dao.consultar(1L, CpOrgaoUsuario.class, false);
		assertNotNull(consultado);
		assertEquals("TESTE", consultado.getSiglaOrgaoUsu());
	}

	@Test
	public void testListarOrgaosUsuarios() {
		CpDao dao = null;

		CpOrgaoUsuario orgao1 = new CpOrgaoUsuario();
		orgao1.setIdOrgaoUsu(1L);
		orgao1.setNmOrgaoUsu("Órgão 1");
		orgao1.setSiglaOrgaoUsu("ORG1");
		em.persist(orgao1);

		CpOrgaoUsuario orgao2 = new CpOrgaoUsuario();
		orgao2.setIdOrgaoUsu(2L);
		orgao2.setNmOrgaoUsu("Órgão 2");
		orgao2.setSiglaOrgaoUsu("ORG2");
		em.persist(orgao2);

		List<CpOrgaoUsuario> lista = dao.listarOrgaosUsuarios();
		assertNotNull(lista);
		assertEquals(2, lista.size());
	}
	
	@Test
	public void testConsultarPorSigla() {
		CpDao dao = null;

		CpOrgaoUsuario orgao = new CpOrgaoUsuario();
		orgao.setIdOrgaoUsu(1L);
		orgao.setNmOrgaoUsu("Órgão Sigla");
		orgao.setSiglaOrgaoUsu("SIGLA1");
		em.persist(orgao);

		CpOrgaoUsuario buscado = new CpOrgaoUsuario();
		buscado.setSiglaOrgaoUsu("SIGLA1");
		
		CpOrgaoUsuario consultado = dao.consultarPorSigla(buscado);
		assertNotNull(consultado);
		assertEquals("Órgão Sigla", consultado.getNmOrgaoUsu());
	}

	public static String removeAcento(String s) {
		return s;
	}
}
