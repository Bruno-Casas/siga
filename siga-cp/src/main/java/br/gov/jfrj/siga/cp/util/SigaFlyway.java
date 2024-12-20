package br.gov.jfrj.siga.cp.util;

import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.base.SigaHTTP;
import org.flywaydb.core.Flyway;
import org.jboss.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.management.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.SocketTimeoutException;
import java.util.HashMap;

public class SigaFlyway {
    private final static org.jboss.logging.Logger log = Logger.getLogger(SigaFlyway.class);

    public static void migrate(String dataSource, String location, boolean waitCorp) throws NamingException {
        if ("true".equals(System.getProperty("siga.flyway.mysql.migrate", "false"))) {
            if (waitCorp) {
                int sleep = 1;
                while (!isCorpMigrationComplete())
                    try {
                        log.info("AGUARDANDO MIGRATION");
                        Thread.sleep(sleep * 1000L);
                        if (sleep < 4)
                            sleep *= 2;
                    } catch (InterruptedException ignored) {
                    }
            }
            log.info("MIGRANDO " + dataSource);

            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:");
            DataSource ds = (DataSource) envContext.lookup(dataSource);
            Flyway flyway = Flyway.configure()
                    .dataSource(ds)
                    .locations(location)
                    .placeholderReplacement(false)
                    .outOfOrder(true)
                    .load();

            if ("true".equals(System.getProperty("siga.flyway.mysql.repair", "true"))) {
                flyway.repair();
            }

            flyway.migrate();
        }
    }

    public static boolean isMigrationComplete(String url) {
        try {
            JSONObject obj = getAsJson(url, null, 1000, null);
            JSONArray dependencies = obj.getJSONArray("dependencies");

            for (int i = 0; i < dependencies.length(); i++) {
                JSONObject o = dependencies.getJSONObject(i);
                if (o.getString("service").endsWith("migration"))
                    return o.getBoolean("available");
            }
            throw new Exception(
                    "Teste para verificar se a migração não encontrou dependência terminada com 'migration'");
        } catch (Exception ex) {
            Throwable e = ex;
            while (e.getCause() != null && e.getCause() != e)
                e = e.getCause();
            if (e instanceof SocketTimeoutException)
                log.info("Teste se migração está completa não conseguiu se conectar na url " + url
                        + ", tentando novamente...");
            else
                log.info("Erro testando se migração está completa na url " + url, ex);
            return false;
        }
    }

    public static boolean isCorpMigrationComplete() {
        String property = System.getProperty("siga.base.local.url");
        if (property == null)
            property = "http://localhost:8080";
        String url = property + "/siga/api/v1/test";
        return isMigrationComplete(url);
    }

    public static JSONObject getAsJson(String URL, HashMap<String, String> header, Integer timeout, String payload)
            throws AplicacaoException {
        try {
            return new JSONObject(new SigaHTTP().getNaWeb(URL, header, timeout, payload));
        } catch (Exception ioe) {
            throw new RuntimeException("Erro obtendo recurso externo", ioe);
        }
    }

    public static void stopJBoss() {
        try {
            MBeanServerConnection mbeanServerConnection = ManagementFactory.getPlatformMBeanServer();
            ObjectName mbeanName = new ObjectName("jboss.as:management-root=server");
            Object[] args = {false};
            String[] sigs = {"java.lang.Boolean"};
            mbeanServerConnection.invoke(mbeanName, "shutdown", args, sigs);
        } catch (InstanceNotFoundException | MBeanException | ReflectionException | IOException
                 | MalformedObjectNameException e) {
            log.error("Erro interrompendo o JBoss", e);
        }
    }

}
