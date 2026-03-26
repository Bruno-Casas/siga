package br.gov.jfrj.siga.base;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Date;
import java.sql.Timestamp;

@RequestScoped
public class ServerClock {

    @Inject
    private EntityManager em;

    private Long dbBaseMillis;

    private long jvmBaseNanos;

    public Date getDataAtual() {
        if (dbBaseMillis == null) {
            inicializarRelogio();
        }

        long deltaNanos = System.nanoTime() - jvmBaseNanos;

        long deltaMillis = deltaNanos / 1_000_000;

        return new Date(dbBaseMillis + deltaMillis);
    }

    private void inicializarRelogio() {
        Object result = em.createNativeQuery("SELECT CURRENT_TIMESTAMP").getSingleResult();

        if (result instanceof Timestamp) {
            this.dbBaseMillis = ((Timestamp) result).getTime();
        } else if (result instanceof Date) {
            this.dbBaseMillis = ((Date) result).getTime();
        } else {
            this.dbBaseMillis = System.currentTimeMillis();
        }

        this.jvmBaseNanos = System.nanoTime();
    }
}