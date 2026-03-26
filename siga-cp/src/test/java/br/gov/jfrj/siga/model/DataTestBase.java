package br.gov.jfrj.siga.model;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import br.gov.jfrj.siga.cp.CpIdentidade;
import br.gov.jfrj.siga.cp.CpTipoServico;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.dp.DpCargo;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.cp.CpConfiguracao;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import org.flywaydb.core.Flyway;

import java.util.Date;

public abstract class DataTestBase {

	protected static EntityManagerFactory emf;
	protected EntityManager em;

	@BeforeAll
	public static void setupAll() {
		// Executa as migrations Flyway de teste para garantir funções utilitárias no H2
		Flyway.configure()
			.dataSource("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", "sa", "")
			.locations("classpath:db/migration")
			.placeholderReplacement(false)
			.outOfOrder(true)
			.load()
			.migrate();
		emf = Persistence.createEntityManagerFactory("default");
	}

	@AfterAll
	public static void tearDownAll() {
		if (emf != null) {
			emf.close();
		}
	}

	@BeforeEach
	public void setup() {
		em = emf.createEntityManager();
		ContextoPersistencia.setEntityManager(em);
		em.getTransaction().begin();
	}

	@AfterEach
	public void tearDown() {
		if (em != null) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			em.close();
		}
		ContextoPersistencia.setEntityManager(null);
	}

	/**
	 * Cria um usuário ZZ, lotação, cargo e permissões necessárias para testes de permissão.
	 */
	protected DpPessoa criarUsuarioZZComPermissoes() {
		CpTipoServico servico = new CpTipoServico();
		servico.setIdCpTpServico(2);
		servico.setDscTpServico("Config do sistema");
		em.persist(servico);

		// Criação do órgão usuário
        CpOrgaoUsuario orgao = new CpOrgaoUsuario();
        orgao.setSiglaOrgaoUsu("ORGZZ");
		orgao.setSigla("ORGZZ");
        orgao.setNmOrgaoUsu("Órgão ZZ");
        em.persist(orgao);
        em.flush();

        // Criação do cargo
		DpCargo cargo = new DpCargo();
		cargo.setNomeCargo("Cargo Teste");
		cargo.setOrgaoUsuario(orgao);
		em.persist(cargo);

		// Criação da lotação
		DpLotacao lotacao = new DpLotacao();
		lotacao.setSigla("LOTZZ");
		lotacao.setNomeLotacao("Lotação ZZ");
		lotacao.setOrgaoUsuario(orgao);
		lotacao.setHisDtIni(new Date());
		em.persist(lotacao);

		em.flush();

		// Buscar entidades gerenciadas
		DpCargo cargoGerenciado = em.find(DpCargo.class, cargo.getId());
		DpLotacao lotacaoGerenciada = em.find(DpLotacao.class, lotacao.getId());

		// Criação do usuário ZZ
		DpPessoa pessoa = new DpPessoa();
		pessoa.setNomePessoa("ZZ");
		pessoa.setCargo(cargoGerenciado);
		pessoa.setLotacao(lotacaoGerenciada);
		pessoa.setOrgaoUsuario(orgao);
		// Se existir setCpfPessoa e setEmailPessoa, use-os:
		try {
			pessoa.getClass().getMethod("setCpfPessoa", Long.class);
			pessoa.setCpfPessoa(0L);
		} catch (Exception ignored) {}
		try {
			pessoa.getClass().getMethod("setEmailPessoa", String.class);
			pessoa.setEmailPessoa("zz@dominio.com");
		} catch (Exception ignored) {}
		em.persist(pessoa);
		pessoa.setIdInicial(pessoa.getId());
		em.persist(pessoa);

		// Criação das permissões (configurações) necessárias
		CpConfiguracao confOrgao = new CpConfiguracao();
		confOrgao.setDpPessoa(pessoa);
		confOrgao.setLotacao(lotacao);
		confOrgao.setDescrConfiguracao("SIGA:Sistema Integrado de Gestão Administrativa");
		em.persist(confOrgao);

		em.flush();
		return pessoa;
	}

	/**
	 * Cria um usuário ZZ, lotação e cargo, mas sem permissões de configuração.
	 */
	protected DpPessoa criarUsuarioZZSemPermissoes() {
        // Criação do órgão usuário
        CpOrgaoUsuario orgao = new CpOrgaoUsuario();
        orgao.setIdOrgaoUsu(2L);
        orgao.setSiglaOrgaoUsu("ORGZZ");
        orgao.setNmOrgaoUsu("Órgão ZZ");
        em.persist(orgao);
        em.flush();

        // Criação do cargo
        DpCargo cargo = new DpCargo();
        cargo.setNomeCargo("Cargo Teste");
        cargo.setOrgaoUsuario(orgao);
        em.persist(cargo);

        // Criação da lotação
        DpLotacao lotacao = new DpLotacao();
        lotacao.setSigla("LOTZZ");
        lotacao.setNomeLotacao("Lotação ZZ");
        lotacao.setOrgaoUsuario(orgao);
        em.persist(lotacao);

        em.flush();

        // Buscar entidades gerenciadas
        DpCargo cargoGerenciado = em.find(DpCargo.class, cargo.getId());
        DpLotacao lotacaoGerenciada = em.find(DpLotacao.class, lotacao.getId());

        // Criação do usuário ZZ
        DpPessoa pessoa = new DpPessoa();
        pessoa.setNomePessoa("ZZ");
        pessoa.setCargo(cargoGerenciado);
        pessoa.setLotacao(lotacaoGerenciada);
        try {
            pessoa.getClass().getMethod("setCpfPessoa", Long.class);
            pessoa.setCpfPessoa(0L);
        } catch (Exception ignored) {}
        try {
            pessoa.getClass().getMethod("setEmailPessoa", String.class);
            pessoa.setEmailPessoa("zz@dominio.com");
        } catch (Exception ignored) {}
        em.persist(pessoa);
        em.flush();
        return pessoa;
    }
}
