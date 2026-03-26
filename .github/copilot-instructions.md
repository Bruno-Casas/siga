# SIGA - Sistema de Gestão Administrativa

SIGA is a Brazilian government document management system built with Java EE, VRaptor MVC framework, and modular architecture. Follow these guidelines when working with this codebase.

## Project Architecture

### Module Structure
The system follows a **multi-module Maven structure** with clear separation of concerns:

- **siga-base**: Core foundation and shared utilities
- **siga-cp**: Common corporate entities and configuration (`CpDao`, `CpConfiguracao`)
- **siga-ex**: Document management module (main module) - handles `ExDocumento`, `ExMovimentacao`, `ExMobil`
- **siga-wf**: Workflow engine module
- **siga-***: Additional modules (GC=Knowledge, SR=Services, TP=Transport, etc.)
- **sigaex/sigawf/etc.**: Web applications that depend on core modules

### Key Technologies
- **Java 8** with **WildFly 26** application server
- **VRaptor 4.3** MVC framework (not Spring) - controllers use `@Controller`, `@Path`, `@Get`/`@Post`
- **Hibernate/JPA** with custom DAO pattern (`ExDao`, `CpDao`)
- **SwaggerServlet** for REST APIs (not Spring Boot)
- **JSP/JSTL** with custom taglibs for views
- **MySQL** database with **Flyway** migrations

## Essential Patterns

### Controller Pattern (VRaptor)
```java
@Controller
@Path("/app/documento")
public class ExDocumentoController extends ExController {
    @Inject
    public ExDocumentoController(HttpServletRequest request, Result result, 
                                CpDao dao, SigaObjects so, EntityManager em) {
        super(request, result, dao, so, em);
    }
    
    @Get("/listar")
    public void listar() {
        // VRaptor auto-forwards to /WEB-INF/jsp/documento/listar.jsp
    }
    
    @Post("/gravar") 
    @Transacional
    public void gravar(ExDocumento doc) {
        dao.gravar(doc);
        result.redirectTo(this).listar();
    }
}
```

### DAO/Entity Pattern
- Entities extend base classes: `ExDocumento extends AbstractExDocumento`
- Use `@Entity`, `@Table(name = "siga.ex_documento")` for all entities
- DAO methods: `dao.consultar()`, `dao.gravar()`, `dao.excluir()`
- **Always use transactions**: `@Transacional` or manual begin/commit

### REST API Pattern (SwaggerServlet)
```java
public class ExApiV1Servlet extends SwaggerServlet {
    // Implements IExApiV1 interface with Request/Response inner classes
    public interface IDocumentosSiglaGet extends ISwaggerMethod {
        public static class Request implements ISwaggerRequest {
            public String sigla;
        }
        public static class Response implements ISwaggerResponse {
            public DocumentoDTO documento;
        }
        public void run(Request req, Response resp, ExApiV1Context ctx);
    }
}
```

## Development Workflows

### Building & Running
```bash
# Full build (from root)
mvn clean package -DskipTests=true

# Development with Docker
make start-dev          # Starts full stack locally
make restart-dev        # Restarts app service
make stop-dev          # Stops all services

# Individual module build
cd sigaex && mvn package
```

### Database & Migrations
- **Flyway migrations** in `src/main/resources/db/migration/`
- Connection via `siga.properties`: `hibernate.connection.datasource_name=sigaexds`
- Schema: `siga` prefix for all tables (`siga.ex_documento`)

### Key Configuration
- **Properties**: `docker/default.properties` for dev, environment variables for prod
- **Module activation**: `isWorkflowEnabled=true`, module permissions via `SIGA;DOC:Módulo de Documentos`
- **APIs**: Each module exposes REST at `/api/v1/` with Swagger docs at `/swagger-ui`

## Business Logic Patterns

### Document Workflow (`ExDocumento`)
- Documents have **mobils** (`ExMobil`) - trackable units that flow through the system
- **Movimentações** (`ExMovimentacao`) represent all document actions (create, transfer, sign, etc.)
- Document **signatures** use Brazilian digital certificates or login/password
- **Tramitação** (routing) between **lotações** (departments) and **pessoas** (users)

### Permissions & Access
- Use `assertAcesso("PERMISSION")` in controllers before operations
- Format: `"MODULE:ACTION"` like `"DOC:Visualizar"` or `"ADM:Administrar"`
- Check with: `f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;DOC')`

### Siglas (Document References)
Documents use **siglas** as business keys: `TRF2-SGI-2024/12345` format
- Generated via: `ExDocumento.getCodigo()` or `ExMobil.getSigla()`
- Used in URLs, APIs, and cross-references between documents

## Common Gotchas

### VRaptor Specifics
- **No Spring** - use VRaptor dependency injection with `@Inject`
- Views auto-resolve: `listar()` method → `/WEB-INF/jsp/controller/listar.jsp`
- Result forwarding: `result.forwardTo(OtherController.class).method()`
- JSON responses: `result.use(Results.json()).from(object).serialize()`

### Entity Management
- **Always check for null** when loading entities: `dao.consultar(id, ExDocumento.class, false)`
- Use **detached** entity pattern for long conversations
- Hibernate **batch loading** configured via `@BatchSize(size = 500)`

### Custom Tags & JSPs
- Extensive custom taglib: `<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>`
- Document models: `<mod:grupo>`, `<mod:texto>`, `<mod:data>` for template building
- Always include: `<%@ include file="/WEB-INF/page/include.jsp" %>`

When implementing new features, follow the existing module structure, use VRaptor patterns consistently, and ensure proper transaction management around all database operations.