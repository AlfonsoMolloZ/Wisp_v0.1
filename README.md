# WispJR - Sistema de Gestion de Red ISP

Sistema web para la gestion integral de una empresa de servicios de internet inalambrico (WISP). Permite administrar clientes, instalaciones, pagos, redes WAN, inventario de stock, averias y usuarios con control de acceso por roles.

---

## Tech Stack

| Capa | Tecnologia | Version |
|------|-----------|---------|
| Backend | Spring Boot | 4.0.6 |
| Lenguaje | Java | 17 |
| Template Engine | Thymeleaf | - |
| Seguridad | Spring Security | 6 |
| ORM | Spring Data JPA + Hibernate | - |
| Base de datos | MySQL | 8+ |
| Frontend | HTML5, CSS3, JavaScript (vanilla) |
| Iconos | Phosphor Icons | 2.1.2 |
| Fuentes | Rajdhani + DM Sans (Google Fonts) |
| Build | Maven | - |

---

## Prerrequisitos

- **Java 17** o superior
- **MySQL 8** o superior
- **Maven 3.6+** (incluido via Maven Wrapper)
- **IDE** (opcional): IntelliJ IDEA, VS Code, Eclipse

---

## Instalacion y Ejecucion

### 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd Wisp_v0.1
```

### 2. Crear la base de datos

Abrir MySQL y ejecutar:

```sql
CREATE DATABASE wisp;
```

### 3. Configurar credenciales (opcional)

Las credenciales por defecto estan en `src/main/resources/application.properties`:

```properties
server.port=8090
spring.datasource.url=jdbc:mysql://localhost:3306/wisp
spring.datasource.username=root
spring.datasource.password=0202
```

Si tu MySQL usa otras credenciales, modificar los campos `username` y `password`.

### 4. Ejecutar la aplicacion

```bash
# Usando Maven Wrapper (recomendado)
./mvnw spring-boot:run

# En Windows
mvnw.cmd spring-boot:run

# O desde el IDE: ejecutar AppApplication.java
```

Hibernate creara automaticamente las tablas al iniciar (modo `ddl-auto=update`).

### 5. Acceder al sistema

Abrir el navegador en:

```
http://localhost:8090
```

Se redirigira automaticamente a la pagina de login.

### 6. Credenciales de acceso

Los usuarios se crean desde el modulo **Usuarios** dentro del sistema (requiere rol ADMIN). Las contraseñas se almacenan encriptadas con BCrypt.

---

## Estructura del Proyecto

```
app/
├── pom.xml                          # Configuracion Maven
├── mvnw / mvnw.cmd                  # Maven Wrapper
├── src/main/java/com/wisp/app/
│   ├── AppApplication.java          # Punto de entrada
│   ├── SecurityConfig.java          # Configuracion de seguridad
│   ├── controllers/                 # 12 controllers (rutas HTTP)
│   │   ├── AccessDeniedController.java
│   │   ├── AveriaController.java
│   │   ├── AuthController.java
│   │   ├── ClienteController.java
│   │   ├── GlobalControllerAdvice.java
│   │   ├── InstalacionController.java
│   │   ├── PagosController.java
│   │   ├── PlanesController.java
│   │   ├── RedWanController.java
│   │   ├── StockController.java
│   │   ├── UsuarioController.java
│   │   └── ViewController.java
│   ├── dto/                         # 6 DTOs (transferencia de datos)
│   ├── entity/                      # 8 entidades JPA
│   │   ├── Averia.java
│   │   ├── Cliente.java
│   │   ├── Instalacion.java
│   │   ├── Pagos.java
│   │   ├── Planes.java
│   │   ├── RedWan.java
│   │   ├── Stock.java
│   │   └── Usuario.java
│   ├── repository/                  # 8 repositorios JPA
│   ├── security/                    # Servicio de autenticacion
│   │   ├── DatabaseUserDetailsService.java
│   │   └── TestUser.java
│   ├── servicios/                   # 5 interfaces de servicio
│   └── servicios/impl/             # 5 implementaciones
├── src/main/resources/
│   ├── application.properties       # Configuracion de la app
│   ├── static/
│   │   ├── css/styles.css           # Estilos globales (~1500 lineas)
│   │   ├── js/main.js               # JavaScript compartido
│   │   └── img/
│   │       ├── logo.png             # Logo del sistema
│   │       └── favicon.png          # Icono de pestana
│   └── templates/                   # 25+ plantillas Thymeleaf
│       ├── login.html
│       ├── dashboard.html
│       ├── clientes.html / clientes-form.html / clientesInfo.html
│       ├── instalaciones.html / instalaciones-form.html / instalaciones-info.html
│       ├── averias.html / averias-form.html / averias-info.html
│       ├── pagos.html / pagos-form.html / pagos-info.html
│       ├── stock.html / stock-form.html / stock-info.html
│       ├── redes-wan.html / redes-wan-form.html / redes-wan-info.html
│       ├── usuarios.html / usuarios-form.html
│       ├── clientes-pagos.html
│       ├── error/403.html
│       └── fragments/
│           ├── sidebar.html
│           ├── topbar.html
│           ├── favicon.html
│           └── theme.html
```

---

## Arquitectura

### Capas

```
┌─────────────────────────────────────────────┐
│               FRONTEND (Thymeleaf)           │
│  Templates HTML + CSS + JavaScript           │
└─────────────────┬───────────────────────────┘
                  │
┌─────────────────▼───────────────────────────┐
│             CONTROLLERS (12)                 │
│  Manejan rutas HTTP, validan, redirigen      │
├─────────────────────────────────────────────┤
│             SERVICES (5 interfaces + impl)   │
│  Logica de negocio, reglas de estado         │
├─────────────────────────────────────────────┤
│             REPOSITORIES (8)                 │
│  Acceso a datos via Spring Data JPA          │
├─────────────────────────────────────────────┤
│             ENTITIES (8)                     │
│  Mapeo JPA a tablas MySQL                    │
├─────────────────────────────────────────────┤
│             DATABASE (MySQL)                 │
│  Base de datos: wisp                         │
└─────────────────────────────────────────────┘
```

### Relaciones entre Entidades

```
Planes (1) ───────< (N) Cliente
RedWan (1) ───────< (N) Cliente
Cliente (1) ───────< (N) Averia
Cliente (1) ───────< (N) Instalacion
Cliente (1) ───────< (N) Pagos
```

---

## Seguridad

### Roles

| Rol | Permisos |
|-----|----------|
| **ADMIN** | Acceso total: CRUD de todos los modulos, dashboard, gestion de usuarios |
| **Tecnico** | Solo lectura: listar y ver instalaciones y averias |

### Rutas protegidas

| Recurso | ADMIN | Tecnico | Publico |
|---------|-------|---------|---------|
| Dashboard | Si | No | No |
| Clientes | CRUD | No | No |
| Instalaciones | CRUD | Ver/Leer | No |
| Averias | CRUD | Ver/Leer | No |
| Pagos | CRUD | No | No |
| Stock | CRUD | No | No |
| Usuarios | CRUD | No | No |
| Redes WAN | CRUD | No | No |
| Login | - | - | Si |
| Estaticos (CSS/JS/IMG) | - | - | Si |

### Autenticacion

- **Login**: Formulario en `/login` con Spring Security
- **Contrasenas**: Encriptadas con BCrypt
- **Sesion**: Manejada por Spring Security (cookie de sesion)
- **Logout**: `/logout` → redirige a `/login?logout=true`

### Content-Security-Policy

El sistema implementa un header `Content-Security-Policy` via Spring Security para proteccion contra ataques XSS y otras inyecciones de contenido.

| Directiva | Valor | Razon |
|-----------|-------|-------|
| `default-src` | `'self'` | Baseline: solo recursos del mismo origen |
| `script-src` | `'self' 'unsafe-inline'` | `main.js` + scripts inline en templates |
| `style-src` | `'self' 'unsafe-inline' https://fonts.googleapis.com https://cdn.jsdelivr.net data:` | CSS local + inline styles + Google Fonts + Phosphor Icons + SVG data URIs |
| `img-src` | `'self' data:` | Logo, favicon + SVG data URIs en CSS |
| `font-src` | `https://fonts.gstatic.com` | Fuentes web de Google Fonts |
| `connect-src` | `'self'` | Llamadas `fetch()` para modal de detalle |
| `frame-ancestors` | `'none'` | Prevenir framing de la aplicacion |

**Ubicacion:** `SecurityConfig.java` dentro del `SecurityFilterChain`.

---

## Modulos

### 1. Redes WAN

Gestion de redes WAN (Wide Area Network) a las que se conectan los clientes.

**Entidad `RedWan`:** nombre, direccion de red, mascara, gateway, DNS, estado (Activa/Inactiva)

**Funcionalidades:**
- Crear, editar, eliminar redes WAN
- Asignar redes a clientes
- Las IPs de los clientes se generan automaticamente desde la subred

### 2. Clientes

Gestion de clientes del servicio de internet.

**Entidad `Cliente`:** nombres, apellidos, DNI, telefono, direccion, correo, IP, estado, plan, red WAN

**Estados posibles:**
- `Nuevo` → `Pendiente` → `Activo`
- `Activo` → `Suspendido`
- `Suspendido` → `Activo`

**Funcionalidades:**
- Crear, editar clientes
- Suspender clientes (registra fecha de suspension)
- Generacion automatica de IP desde la subred de la red WAN asignada
- Filtrar por estado (Nuevo, Pendiente, Activo, Suspendido)
- Ver historial de pagos de un cliente

### 3. Instalaciones

Gestion de instalaciones realizadas a clientes.

**Entidad `Instalacion`:** tipo, direccion, estado, fechas, cobro (Si/No), monto, metodo de pago, observacion, cliente

**Estados posibles:**
- `Pendiente` → `Atendido`
- `Pendiente` → `Anulado`

**Reglas de negocio:**
- Solo se pueden editar instalaciones en estado `Pendiente`
- Al marcar como `Atendido`, se registra automaticamente la fecha de atencion
- Al atender una instalacion, el cliente se marca como `Activo`

### 4. Averias

Gestion de averias/incidentes reportados por clientes.

**Entidad `Averia`:** codigo, descripcion, direccion, estado, fechas, cliente

**Estados posibles:**
- `Sin atender` → `Atendido`

**Funcionalidades:**
- Crear y editar averias
- Al marcar como `Atendido`, se registra la fecha de atencion
- Cada averia tiene un codigo unico

### 5. Pagos

Gestion de pagos mensuales de los clientes.

**Entidad `Pagos`:** cliente, monto, fechas (emision, vencimiento, pago), mes, anio, estado, metodo de pago, observacion

**Estados posibles:**
- `Pendiente` → `Pagado`
- `Pendiente` → `Vencido`
- `Pagado` → `Anulado`

**Funcionalidades:**
- Generacion automatica de pagos mensuales para todos los clientes activos
- Actualizacion automatica de pagos vencidos (al cargar el dashboard)
- Filtrar por anio y mes

### 6. Stock

Gestion de inventario de equipos.

**Entidad `Stock`:** equipo, marca, modelo, cantidad, estado, ultimo ingreso

### 7. Usuarios

Gestion de usuarios del sistema.

**Entidad `Usuario`:** username, password (BCrypt), role (ADMIN/Tecnico)

**Funcionalidades:**
- Crear, editar, eliminar usuarios
- Asignar roles (ADMIN o Tecnico)
- Las contraseñas se encriptan automaticamente antes de guardar

### 8. Dashboard

Panel de control con metricas generales.

**Estadisticas mostradas:**
- Total de clientes (nuevos, pendientes, suspendidos, activos, deudores)
- Total de averias (atendidas, pendientes)
- Total de pagos (recibidos, pendientes, vencidos) con monto total
- Total de instalaciones (pendientes, atendidas, anuladas)
- Alertas: clientes suspendidos, pagos vencidos, instalaciones pendientes, averias pendientes
- Actividad reciente (ultimas 5 instalaciones, pagos y averias)

---

## Rutas HTTP

### Autenticacion

| Metodo | URL | Descripcion | Acceso |
|--------|-----|-------------|--------|
| GET | `/login` | Pagina de login | Publico |
| POST | `/login` | Procesar login | Publico |
| GET/POST | `/logout` | Cerrar sesion | Autenticado |
| GET | `/acceso-denegado` | Pagina 403 | Publico |

### Clientes

| Metodo | URL | Descripcion | Acceso |
|--------|-----|-------------|--------|
| GET | `/clientes` | Listar clientes | ADMIN |
| GET | `/clientes?estado=Activo` | Filtrar por estado | ADMIN |
| GET | `/clientes/nuevo` | Formulario nuevo | ADMIN |
| POST | `/clientes/guardar` | Guardar cliente | ADMIN |
| GET | `/clientes/editar/{id}` | Formulario editar | ADMIN |
| GET | `/clientes/ver/{id}` | Ver detalle | ADMIN |
| POST | `/clientes/suspender/{id}` | Suspender cliente | ADMIN |
| GET | `/clientes/{id}/pagos` | Historial de pagos | ADMIN |

### Instalaciones

| Metodo | URL | Descripcion | Acceso |
|--------|-----|-------------|--------|
| GET | `/instalaciones` | Listar instalaciones | ADMIN, Tecnico |
| GET | `/instalaciones/nueva` | Formulario nueva | ADMIN |
| POST | `/instalaciones/guardar` | Guardar instalacion | ADMIN |
| GET | `/instalaciones/editar/{id}` | Formulario editar | ADMIN |
| GET | `/instalaciones/ver/{id}` | Ver detalle | ADMIN, Tecnico |

### Averias

| Metodo | URL | Descripcion | Acceso |
|--------|-----|-------------|--------|
| GET | `/averias` | Listar averias | ADMIN, Tecnico |
| GET | `/averias/nueva` | Formulario nueva | ADMIN |
| POST | `/averias/guardar` | Guardar averia | ADMIN |
| GET | `/averias/editar/{id}` | Formulario editar | ADMIN |
| GET | `/averias/ver/{id}` | Ver detalle | ADMIN, Tecnico |

### Pagos

| Metodo | URL | Descripcion | Acceso |
|--------|-----|-------------|--------|
| GET | `/pagos` | Listar pagos | ADMIN |
| GET | `/pagos?anio=2026&mes=7` | Filtrar por periodo | ADMIN |
| GET | `/pagos/generar` | Generar pagos mensuales | ADMIN |
| GET | `/pagos/editar/{id}` | Formulario editar | ADMIN |
| POST | `/pagos/guardar` | Guardar pago | ADMIN |
| GET | `/pagos/ver/{id}` | Ver detalle | ADMIN |

### Stock

| Metodo | URL | Descripcion | Acceso |
|--------|-----|-------------|--------|
| GET | `/stock` | Listar stock | ADMIN |
| GET | `/stock/nuevo` | Formulario nuevo | ADMIN |
| POST | `/stock/guardar` | Guardar item | ADMIN |
| GET | `/stock/editar/{id}` | Formulario editar | ADMIN |
| GET | `/stock/ver/{id}` | Ver detalle | ADMIN |

### Redes WAN

| Metodo | URL | Descripcion | Acceso |
|--------|-----|-------------|--------|
| GET | `/redes-wan` | Listar redes | ADMIN |
| GET | `/redes-wan/nueva` | Formulario nueva | ADMIN |
| POST | `/redes-wan/guardar` | Guardar red | ADMIN |
| GET | `/redes-wan/editar/{id}` | Formulario editar | ADMIN |
| GET | `/redes-wan/ver/{id}` | Ver detalle | ADMIN |
| POST | `/redes-wan/eliminar/{id}` | Eliminar red | ADMIN |

### Usuarios

| Metodo | URL | Descripcion | Acceso |
|--------|-----|-------------|--------|
| GET | `/usuarios` | Listar usuarios | ADMIN |
| GET | `/usuarios/nuevo` | Formulario nuevo | ADMIN |
| POST | `/usuarios/guardar` | Guardar usuario | ADMIN |
| GET | `/usuarios/editar/{id}` | Formulario editar | ADMIN |
| POST | `/usuarios/eliminar/{id}` | Eliminar usuario | ADMIN |

### Dashboard

| Metodo | URL | Descripcion | Acceso |
|--------|-----|-------------|--------|
| GET | `/` | Dashboard (redirect) | ADMIN |
| GET | `/dashboard` | Dashboard | ADMIN |

---

## Frontend

### Tema Dark/Light

El sistema incluye un toggle de tema en la barra superior que permite cambiar entre modo oscuro y claro. La preferencia se guarda en `localStorage` y persiste entre sesiones.

### Componentes CSS

- **Sidebar**: Navegacion lateral con iconos Phosphor, responsive (drawer en movil, colapsado en tablet)
- **Topbar**: Barra superior con menu hamburguesa, toggle de tema e informacion de usuario
- **Cards**: Tarjetas de informacion con avatar, gradiente y secciones agrupadas
- **Tables**: Tablas con busqueda, filtros por estado, y acciones (ver, editar, eliminar)
- **Forms**: Formularios con validacion visual, focus animado, y layout responsive
- **Modals**: Ventanas flotantes para ver detalle de registros sin navegar
- **Toasts**: Notificaciones emergentes con auto-dismiss
- **Login**: Pagina de login con efecto glassmorphism y fondo animado

### Responsive

| Breakpoint | Comportamiento |
|------------|----------------|
| > 1023px | Sidebar completo (240px) |
| 768-1023px | Sidebar colapsado (68px) con tooltips |
| < 768px | Sidebar drawer con overlay |

---

## Desarrollo

### Hot Reload

El proyecto tiene Spring DevTools habilitado. Los cambios en Java se recargan automaticamente. Los cambios en templates HTML y archivos estaticos se refrescan con livereload.

### Deshabilitar cache de Thymeleaf

Para desarrollo, la cache de Thymeleaf esta deshabilitada en `application.properties`:

```properties
spring.thymeleaf.cache=false
```

### Logs de SQL

Las consultas SQL se muestran en la consola formateadas:

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```
