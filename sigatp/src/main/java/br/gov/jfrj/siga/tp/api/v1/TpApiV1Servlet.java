package br.gov.jfrj.siga.tp.api.v1;

import br.gov.jfrj.siga.base.Prop;
import br.gov.jfrj.siga.base.Prop.IPropertyProvider;
import br.gov.jfrj.siga.idp.jwt.AuthJwtFormFilter;
import br.gov.jfrj.siga.model.ContextoPersistencia;
import br.gov.jfrj.siga.tp.model.TpDao;
import com.crivano.swaggerservlet.SwaggerContext;
import com.crivano.swaggerservlet.SwaggerServlet;
import com.crivano.swaggerservlet.dependency.TestableDependency;

import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class TpApiV1Servlet extends SwaggerServlet implements IPropertyProvider {
	private static final long serialVersionUID = 1756711359239182178L;

	@Inject
	private TpDao dao;

	@Override
	public void initialize(ServletConfig config) throws ServletException {
		setAPI(ITpApiV1.class);

		setActionPackage("br.gov.jfrj.siga.api.v1");

		Prop.setProvider(this);
		Prop.defineGlobalProperties();
		defineProperties();

		class HttpGetDependency extends TestableDependency {
			String testsite;

			HttpGetDependency(String category, String service, String testsite, boolean partial, long msMin,
					long msMax) {
				super(category, service, partial, msMin, msMax);
				this.testsite = testsite;
			}

			@Override
			public String getUrl() {
				return testsite;
			}

			@Override
			public boolean test() throws Exception {
				final URL url = new URL(testsite);
				final URLConnection conn = url.openConnection();
				conn.connect();
				return true;
			}
		}

		addDependency(new HttpGetDependency("rest", "www.google.com/recaptcha",
				"https://www.google.com/recaptcha/api/siteverify", false, 0, 10000));

		addDependency(new TestableDependency("database", "sigatpds", false, 0, 10000) {

			@Override
			public String getUrl() {
				return getProperty("datasource.name");
			}

			@Override
			public boolean test() throws Exception {
				try (ApiContext ctx = new ApiContext(true)) {
					return dao.dt() != null;
				}
			}

			@Override
			public boolean isPartial() {
				return false;
			}
		});

//		if (SwaggerServlet.getProperty("redis.password") != null)
//			addDependency(new TestableDependency("cache", "redis", false, 0, 10000) {
//
//				@Override
//				public String getUrl() {
//					return "redis://" + MemCacheRedis.getMasterHost() + ":" + MemCacheRedis.getMasterPort() + "/"
//							+ MemCacheRedis.getDatabase() + " (" + "redis://" + MemCacheRedis.getSlaveHost() + ":"
//							+ MemCacheRedis.getSlavePort() + "/" + MemCacheRedis.getDatabase() + ")";
//				}
//
//				@Override
//				public boolean test() throws Exception {
//					String uuid = UUID.randomUUID().toString();
//					MemCacheRedis mc = new MemCacheRedis();
//					mc.store("test", uuid.getBytes());
//					String uuid2 = new String(mc.retrieve("test"));
//					return uuid.equals(uuid2);
//				}
//			});

	}

	private void defineProperties() {
		addPublicProperty("datasource.name", "java:/jboss/datasources/SigaTpDS");
		addPublicProperty("senha.usuario.expiracao.dias", null);
	}

	@Override
	public String getService() {
		return "sigatp";
	}

	@Override
	public String getUser() {
		return ContextoPersistencia.getUserPrincipal();
	}

//	public static <T> Future<T> submitToExecutor(Callable<T> task) {
//		return executor.submit(task);
//	}

	@Override
	public void invoke(SwaggerContext context) throws Exception {
		try {
			if (!context.getAction().getClass().isAnnotationPresent(AcessoPublico.class)) {
				try {
					String token = AuthJwtFormFilter.extrairAuthorization(context.getRequest());
					Map<String, Object> decodedToken = AuthJwtFormFilter.validarToken(token);
					final long now = System.currentTimeMillis() / 1000L;
					if ((Integer) decodedToken.get("exp") < now + AuthJwtFormFilter.TIME_TO_RENEW_IN_S) {
						// Seria bom incluir o attributo HttpOnly
						String tokenNew = AuthJwtFormFilter.renovarToken(token);
						Map<String, Object> decodedNewToken = AuthJwtFormFilter.validarToken(token);
						Cookie cookie = AuthJwtFormFilter.buildCookie(tokenNew);
						context.getResponse().addCookie(cookie);
					}
					ContextoPersistencia.setUserPrincipal((String) decodedToken.get("sub"));
				} catch (Exception e) {
					if (!context.getAction().getClass().isAnnotationPresent(AcessoPublicoEPrivado.class))
						throw e;
				}
			}
			super.invoke(context);
		} finally {
			ContextoPersistencia.removeUserPrincipal();
		}
	}

	@Override
	public String getProp(String nome) {
		return getProperty(nome);
	}

}
