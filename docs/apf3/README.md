# APF3 - Herramientas de Desarrollo: despliegue en la nube

Documentacion del tercer avance del proyecto Wisp (gestion de ISP), curso Herramientas
de Desarrollo - UTP, Semana 15.

## 1. Estado del avance

| Componente | Estado | Evidencia |
|---|---|---|
| Contenerizacion con Docker | Completado | `Dockerfile`, `docker-compose.yml`, build y login local |
| Publicacion automatica de imagen | Completado | GitHub Actions + Docker Hub (`latest`, `sha-65f33f4`) |
| Infraestructura cloud | Completado | Render + MySQL de Aiven |
| Documentacion y evidencias | En curso | Este documento + capturas pendientes |

Aplicacion desplegada: **https://wisp-app-latest.onrender.com**
Imagen publica: **https://hub.docker.com/r/alfonso02mz/wisp-app**

## 2. Decisiones tecnicas

| Tema | Decision |
|---|---|
| Aplicacion | Spring Boot 4.0.6, Java 17, Thymeleaf, Spring Security, Spring Data JPA |
| Base de datos local | MySQL 8 (`mysql:8.4`) en Docker |
| Base de datos cloud | Aiven MySQL 8.4 (plan gratuito) |
| Registro de imagenes | Docker Hub (`alfonso02mz/wisp-app`) |
| Cloud | Render Web Service (imagen Docker, plan gratuito) |
| Datos de prueba | Seeder Java idempotente con `WISP_SEED_DEMO=true` |
| CI/CD | GitHub Actions (tests + build/push) |
| CD a la nube | Deploy Hook de Render invocado al final del workflow |

## 3. Entorno verificado

### Local

| Componente | Version / estado |
|---|---|
| Docker Desktop | 4.99.0 |
| Docker Engine | 29.9.3 |
| Docker Compose | v5.7.0 |
| Docker Hub | Sesion iniciada como `alfonso02mz` |
| Java local | 21 (el proyecto compila y ejecuta en Java 17) |
| Maven | Maven Wrapper incluido en el repositorio |
| Pruebas | 68 tests, 0 fallos, 0 errores |

### Cloud

| Componente | Version / estado |
|---|---|
| Render | Servicio web activo, health check `/login` |
| Aiven MySQL | 8.4.11, base `defaultdb` |
| Perfil Spring | `prod` (`server.port=${PORT:8090}`) |
| Usuario inicial | `ADMIN` creado por `DataInitializer` |

## 4. Arquitectura del despliegue

```
Pull Request -> main
      |
      v
GitHub Actions
  job test: ./mvnw -B verify (68 tests)
  job docker: build + push a Docker Hub
      |
      v
Docker Hub: alfonso02mz/wisp-app:latest + sha-<commit>
      |
      v
Deploy Hook de Render
      |
      v
Render (contenedor Spring Boot)  --->  Aiven MySQL (SSL obligatorio)
```

## 5. Distribucion del trabajo

| Integrante | Responsabilidad |
|---|---|
| Alumno 1 | Dockerfile, `.dockerignore`, perfil `prod`, job de tests del pipeline |
| Alumno 2 | Compose, variables de entorno, MySQL, Docker Hub y despliegue en Render |
| Alumno 3 | Seeder de datos de prueba, documentacion y capturas de evidencia |

## 6. Evidencias por fase

| Fase | Rama | Commit / PR / Issue | Resultado |
|---|---|---|---|
| 0. Preparacion | `feature/apf3-docker` | `a9b2d88` | Documentacion y checklist |
| 1. Dockerfile | `feature/apf3-dockerfile` | `0e98895`, PR #13, issue #17 | Imagen multi-stage, usuario no root |
| 2. Compose y entorno | `feature/apf3-compose` | `721efb7`, PR #14, issue #18 | `db` healthy, `app` conectada, login local |
| 3. Seeder | `feature/apf3-seeder` | `ed2a601`, PR #15, issue #19 | 3 planes, 2 redes, 5 clientes, 3 pagos, etc. |
| 4. CI/CD | `feature/apf3-pipeline` | `04a9e7d`, PR #16, issue #20 | Run 44513424929: tests y publicacion en success |
| 5. Cloud | `feature/apf3-cloud` | PR #22, issue #21 | Render + Aiven operativos |

Ramas remotas del avance: `feature/apf3-docker`, `feature/apf3-dockerfile`,
`feature/apf3-compose`, `feature/apf3-seeder`, `feature/apf3-pipeline`,
`feature/apf3-cloud`.

## 7. Archivos del avance

| Archivo | Funcion |
|---|---|
| `Dockerfile` | Build multi-stage (Maven + JRE 17), cache de dependencias, usuario `app`, perfil `prod` |
| `.dockerignore` | Reduce el contexto y excluye `.env`, tests y artefactos |
| `docker-compose.yml` | Servicios `app` y `db`, healthcheck, volumen `db-data` |
| `.env.example` | Plantilla de variables sin secretos |
| `.env` | Valores locales (ignorado por git) |
| `src/main/resources/application-prod.properties` | SQL logging off, cache Thymeleaf, puerto `${PORT}` |
| `src/main/java/com/wisp/app/config/DemoDataSeeder.java` | Datos demo idempotentes |
| `.github/workflows/ci-cd.yml` | Tests, build/push a Docker Hub y deploy a Render |

## 8. Variables de entorno

| Variable | Uso |
|---|---|
| `DB_URL` | URL JDBC de MySQL |
| `DB_USERNAME` | Usuario de la base de datos |
| `DB_PASSWORD` | Contrasena de la base de datos |
| `WISP_ADMIN_USERNAME` | Usuario administrador inicial |
| `WISP_ADMIN_PASSWORD` | Contrasena del administrador inicial |
| `WISP_SEED_DEMO` | Activa los datos de prueba |
| `SPRING_PROFILES_ACTIVE` | `prod` en contenedor/nube |
| `JAVA_TOOL_OPTIONS` | Limite de memoria JVM en el plan gratuito |
| `PORT` | Puerto asignado por Render (lo usa `application-prod.properties`) |

Las credenciales no se versionan. En GitHub Actions se usan los secrets
`DOCKERHUB_USERNAME`, `DOCKERHUB_TOKEN` y `RENDER_DEPLOY_HOOK_URL`.

## 9. Comandos de verificacion

```powershell
# Local
docker compose up -d --build
docker compose ps
docker compose logs -f app
docker compose down        # conserva datos
docker compose down -v     # reinicia datos y ejecuta el seeder

# Imagen publicada
docker pull alfonso02mz/wisp-app:latest

# Pruebas
mvnw.cmd -B verify
```

Verificacion funcional: `http://localhost:8090` (local) y
`https://wisp-app-latest.onrender.com` (nube), login con el usuario administrador,
dashboard con metricas y modulos con datos de prueba.

## 10. DevOps: repositorio y control de versiones

- Ramas de trabajo por componente, subidas a GitHub.
- Commits por fase y Pull Requests #13, #14, #15, #16 y #22 mergeados a `main`.
- El PR #16 se cerro accidentalmente y se reabrio antes del merge (historial visible).
- Incidencias registradas en GitHub Issues #17 a #21 y cerradas como completadas.
- No se produjeron conflictos reales de merge durante el avance; el flujo usado es
  merge local de `main` en la rama, resolucion manual y push.
- CI en cada PR y push a `main`; CD publica la imagen y dispara el deploy de Render.

## 11. Limitaciones y recomendaciones

- El plan gratuito de Render suspende la instancia por inactividad; el primer acceso
  puede tardar hasta un minuto.
- La base MySQL gratuita de Aiven tiene 1 GB, requiere SSL (`ssl-mode=REQUIRED`) y se recomienda un respaldo antes de la demo.
- Las credenciales compartidas durante el desarrollo deben rotarse: token de Docker Hub
  (secret `DOCKERHUB_TOKEN`) y contrasena de Aiven (`DB_PASSWORD` en Render).
- Si la tabla `usuarios` ya tiene registros, `DataInitializer` no cambia la contrasena
  del administrador; un cambio de `WISP_ADMIN_PASSWORD` solo aplica en una base vacia.

## 12. Checklist de evidencias

- [x] Estructura de archivos Docker
- [x] `Dockerfile` con build exitoso (local y CI)
- [x] `docker-compose.yml` con aplicacion y base de datos sanas
- [x] `.env.example` versionado y `.env` ignorado
- [x] Seeder ejecutado y datos visibles en la aplicacion
- [x] Historial de commits, ramas, pull requests y merges
- [x] Incidencias creadas y cerradas
- [x] Workflow CI/CD y run exitoso
- [x] Imagen publicada en Docker Hub (`latest` + `sha`)
- [x] Servicio de Render desplegado y accesible
- [x] Base de datos Aiven conectada
- [ ] Capturas de pantalla en `docs/apf3/evidencias/` (responsable: Alumno 3)
- [x] Documentacion del proceso (este archivo y README principal)
