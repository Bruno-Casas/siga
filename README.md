# Siga - Sistema de Gestão Administrativa

## Overview
Siga is a comprehensive administrative management system. It's composed of several modules:
- **Siga-Doc**: Document management (Gestão Documental). High adherence to e-Arq requirements. Supports digital and physical documents, administrative processes, and digital signatures.
- **Siga-WF**: Workflow management.
- **Siga-GC**: Knowledge management (Gestão de Conhecimento).
- **Siga-SR**: Service requests and tickets (Serviços e Tickets).
- **Siga-tp**: Transport requests.
- **Siga-CP**: Corporate module (Pessoas, Lotações, etc.).

## Technical Stack
- **Language**: Java 1.8
- **Build Tool**: Maven 3.x
- **Application Server**: Wildfly 26.1.3.Final
- **Frameworks**: VRaptor 4, Hibernate (JPA), Flyway, JasperReports, Apache Lucene.
- **Containerization**: Docker & Docker Swarm.

## Requirements
- **JDK**: 1.8
- **Maven**: 3.x
- **Docker**: Latest version
- **Docker Swarm**: Initialized (`docker swarm init`)

## Setup and Run

### Build
To build all modules and skip tests:
```bash
mvn clean package -DskipTests
```
The build process copies generated `.war` files from modules like `siga`, `sigaex`, `sigawf`, and `sigagc` to the root `target/` directory, stripping version names (e.g., `siga.war`).

To build the Docker image:
```bash
make build
```

To build everything (Maven + Docker) in one command:
```bash
make build-all
```

### Run Development Environment
The development stack can be started using the `Makefile` and `infra/siga-infra.sh`. This deploys the Siga stack and Traefik using Docker Swarm.

To start the development environment:
```bash
make start-dev
```

To stop the development stack:
```bash
make stop-dev
```

## Tests
Run all tests:
```bash
mvn test
```
To run tests for a specific module:
```bash
mvn test -pl <module-name>
```

### Persistence Testing
Most persistence tests extend `br.gov.jfrj.siga.model.DataTestBase` and use an H2 in-memory database.
**H2 Compatibility Note**: Many entities use a `@Formula` calling a SQL function `REMOVE_ACENTO`, which is not native to H2. It must be registered as an alias in H2 during test setup:
```sql
CREATE ALIAS IF NOT EXISTS REMOVE_ACENTO FOR "br.gov.jfrj.siga.dp.dao.CpDaoIntegrationTest.removeAcento"
```

## Project Structure
- `siga-base`: Core models and utilities.
- `siga-cp`: Corporate module (Pessoas, Lotações, etc.).
- `siga-ex`: Document management logic.
- `sigaex`: Document management web module.
- `sigasr`: Service Request module.
- `sigagc`: Knowledge Management module.
- `sigawf`: Workflow module.
- `infra/`: Deployment scripts and configurations.
- `docker/`: Docker Swarm stack definitions.

## Environment Variables
- `BASE_PATH`: Base directory for deployments (defaults to current working directory).
- `BASE_VERSION`: Version of the base image to use in Docker builds (e.g., `latest`, `homolog`).

## Useful Links
- [Project Website (TRF2)](http://linksiga.trf2.jus.br/)
- [User Guide](https://colabore.trf2.jus.br/course/view.php?id=2)
- [EAD Course](https://colabore.trf2.jus.br/enrol/index.php?id=4)
- [Support Forum](https://groups.google.com/forum/#!forum/siga-doc)
- [Javadoc](http://projeto-siga.github.io/artifacts/javadoc/)
- [Docker Version (Legacy)](https://github.com/projeto-siga/siga-docker)

## TODOs
- [ ] Detail all configuration properties in `siga.conf`.
- [ ] Document production deployment procedures.
- [ ] Add contribution guidelines and code style details.

## License
Siga is licensed under the [GNU Affero General Public License v3](LICENSE).

