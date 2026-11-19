# Evidencias del APF3

Guardar aqui las capturas usadas en la sustentacion. Nombres sugeridos:

| Archivo | Que capturar |
|---|---|
| `01-build-docker.png` | Salida final de `docker compose up -d --build` o `docker build` |
| `02-compose-ps.png` | `docker compose ps` con `app` arriba y `db` healthy |
| `03-login-local.png` | `http://localhost:8090/login` en el navegador |
| `04-datos-seeder.png` | Modulo Clientes con los 5 registros de prueba |
| `05-tests.png` | Resumen de `mvnw.cmd -B verify` (68 tests, 0 fallos) |
| `06-workflow.png` | GitHub Actions: jobs `Tests` y `Publicar imagen` en verde |
| `07-dockerhub.png` | Tags `latest` y `sha-*` en Docker Hub |
| `08-render-servicio.png` | Panel de Render con estado Live y variables (ocultar secretos) |
| `09-aiven.png` | Aiven MySQL con estado Running (ocultar contrasena) |
| `10-app-nube.png` | `https://wisp-app-latest.onrender.com` con el dashboard |
| `11-ramas-prs.png` | Lista de ramas y Pull Requests mergeados |
| `12-issues.png` | Issues #17 a #21 cerrados |

Reglas:

- Nunca incluir contrasenas, tokens ni cadenas de conexion completas.
- Si aparece un secreto, difuminarlo antes de guardar la captura.
- Las capturas no deben versionar datos personales reales; el seeder usa datos ficticios.
