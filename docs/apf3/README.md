# APF3 - Herramientas de Desarrollo: despliegue en la nube

Documentacion del tercer avance del proyecto Wisp (gestion de ISP), curso Herramientas
de Desarrollo - UTP, Semana 15.

## 1. Decisiones tecnicas

| Tema | Decision |
|---|---|
| Aplicacion | Spring Boot 4.0.6, Java 17, Thymeleaf, Spring Security, Spring Data JPA |
| Base de datos | MySQL 8 (contenedor `mysql:8.4`) |
| Registro de imagenes | Docker Hub (`alfonso02mz/wisp-app`) |
| Cloud | Render (Web Service con imagen Docker) |
| Base de datos cloud | Aiven MySQL (plan gratuito) |
| Datos de prueba | Seeder Java idempotente, activado con `WISP_SEED_DEMO=true` |
| CI/CD | GitHub Actions (test + build/push de imagen) |

## 2. Entorno verificado (Fase 0)

| Componente | Version / estado |
|---|---|
| Docker Desktop | 4.99.0 |
| Docker Engine | 29.9.3 |
| Docker Compose | v5.7.0 |
| Docker Hub | Sesion iniciada como `alfonso02mz` |
| Java local | 21 (el proyecto compila con `java.version=17`) |
| Maven | Maven Wrapper incluido en el repositorio |
| GitHub CLI | No instalado |

Nota: el CLI de Docker se instalo en
`%LOCALAPPDATA%\Programs\DockerDesktop\resources\bin` y ya esta agregado al PATH de
usuario. Las terminales abiertas antes de la instalacion deben reiniciarse.

## 3. Distribucion del trabajo

| Integrante | Responsabilidad principal |
|---|---|
| Alumno 1 | Dockerfile optimizado, `.dockerignore`, perfil `prod`, job de CI |
| Alumno 2 | `docker-compose.yml`, variables de entorno, MySQL, publicacion en Docker Hub, infraestructura cloud |
| Alumno 3 | Seeder de datos de prueba, documentacion y evidencias |

Tareas transversales: revision de pull requests, resolucion de conflictos y captura de
evidencias en `docs/apf3/evidencias/`.

## 4. Fases

| Fase | Entregable | Rama sugerida |
|---|---|---|
| 0 | Rama de trabajo, documentacion, incidencias | `feature/apf3-docker` |
| 1 | `Dockerfile`, `.dockerignore`, `application-prod.properties` | `feature/apf3-dockerfile` |
| 2 | `docker-compose.yml`, `.env.example`, `.gitignore` | `feature/apf3-compose` |
| 3 | `DemoDataSeeder.java` | `feature/apf3-seeder` |
| 4 | `.github/workflows/ci-cd.yml` | `feature/apf3-pipeline` |
| 5 | Render + Aiven MySQL | `feature/apf3-cloud` |
| 6 | Evidencias y cierre de documentacion | `feature/apf3-docs` |

Cada fase se integra a `main` mediante Pull Request y un merge, que es el evento que
dispara la publicacion automatica de la imagen.

## 5. Incidencias (crear en GitHub Issues)

| Titulo | Descripcion |
|---|---|
| Dockerfile de la aplicacion | Crear imagen multi-stage optimizada con cache de dependencias y usuario no root |
| Compose con MySQL y variables de entorno | Definir servicios app/db, healthcheck, volumen y `.env` |
| Seeder de datos de prueba | Insertar datos demo idempotentes con flag `WISP_SEED_DEMO` |
| Pipeline CI/CD a Docker Hub | Ejecutar tests y publicar imagen en cada merge a `main` |
| Despliegue en Render + Aiven | Configurar servicio cloud, base de datos y variables |

## 6. Checklist de evidencias

- [ ] Estructura de archivos Docker (`Dockerfile`, `docker-compose.yml`, `.dockerignore`)
- [ ] `Dockerfile` funcional y build exitoso
- [ ] `docker-compose.yml` con aplicacion y base de datos sanas
- [ ] `.env.example` versionado (sin secretos) y `.env` local ignorado
- [ ] Seeder ejecutado y datos visibles en la aplicacion
- [ ] Historial de commits, ramas, pull requests y merges
- [ ] Resolucion de conflictos documentada (si ocurre)
- [ ] Workflow de GitHub Actions y ejecucion exitosa
- [ ] Imagen publicada en `<usuario>/wisp-app` de Docker Hub
- [ ] Servicio de Render desplegado y accesible
- [ ] Base de datos Aiven conectada
- [ ] Capturas en `docs/apf3/evidencias/`
- [ ] README principal actualizado con instrucciones de despliegue

## 7. Seguridad

- No versionar `.env` ni archivos `application-local.properties`.
- El token de Docker Hub se configura unicamente como secret de GitHub
  (`DOCKERHUB_TOKEN`) y nunca se pega en el repositorio ni en la documentacion.
- Si un token se expone, revocarlo en Docker Hub y generar uno nuevo.
