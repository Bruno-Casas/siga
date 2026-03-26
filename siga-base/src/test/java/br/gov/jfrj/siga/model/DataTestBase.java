package br.gov.jfrj.siga.model;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class DataTestBase {

	protected static EntityManagerFactory emf;
	protected EntityManager em;

	@BeforeAll
	public static void setupAll() {
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
}
