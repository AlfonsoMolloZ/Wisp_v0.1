/* ============================================================
   Wisp — main.js
   Funcionalidades compartidas: sidebar, modales, toasts, búsqueda
   ============================================================ */

document.addEventListener('DOMContentLoaded', function () {

  // =====================================================
  // THEME TOGGLE - Dark / Light (init handled by head script)
  // =====================================================
  var themeToggle = document.getElementById('themeToggle');
  if (themeToggle) {
    themeToggle.checked = document.documentElement.getAttribute('data-theme') === 'light';
    themeToggle.addEventListener('change', function () {
      var newTheme = themeToggle.checked ? 'light' : 'dark';
      document.documentElement.setAttribute('data-theme', newTheme);
      localStorage.setItem('wisp-theme', newTheme);
    });
  }

  // =====================================================
  // NAV - Marcar item activo según URL
  // =====================================================
  var path = window.location.pathname;
  document.querySelectorAll('.nav-item').forEach(function (link) {
    var href = link.getAttribute('href') || '';
    if (href && path.includes(href)) {
      link.classList.add('active');
    }
  });

  // =====================================================
  // SIDEBAR RESPONSIVE
  // =====================================================
  var sidebar = document.querySelector('.sidebar');
  var overlay = document.querySelector('.sidebar-overlay');
  var menuBtn = document.querySelector('.topbar-menu');

  if (menuBtn) {
    menuBtn.addEventListener('click', toggleSidebar);
  }
  if (overlay) {
    overlay.addEventListener('click', closeSidebar);
  }

  function toggleSidebar() {
    if (isMobile()) {
      if (sidebar.classList.contains('open')) {
        closeSidebar();
      } else {
        openSidebar();
      }
    } else {
      sidebar.classList.toggle('collapsed');
      applySidebarCollapse();
    }
  }

  function openSidebar() {
    sidebar.classList.add('open');
    overlay.classList.add('show');
    document.body.style.overflow = 'hidden';
  }

  function closeSidebar() {
    sidebar.classList.remove('open');
    overlay.classList.remove('show');
    document.body.style.overflow = '';
  }

  function isMobile() {
    return window.innerWidth < 768;
  }

  window.addEventListener('resize', function () {
    if (!isMobile()) {
      closeSidebar();
    }
  });

  function applySidebarCollapse() {
    var collapsed = sidebar.classList.contains('collapsed');
    sidebar.style.width = collapsed ? 'var(--sidebar-collapsed)' : '';
    sidebar.querySelectorAll('.nav-label, .sidebar-logo span:not(.logo-icon), .logout-label').forEach(function (el) {
      el.style.display = collapsed ? 'none' : '';
    });
    sidebar.querySelectorAll('.nav-item').forEach(function (el) {
      el.style.justifyContent = collapsed ? 'center' : '';
      el.style.padding = collapsed ? '12px 0' : '';
    });
    document.querySelector('.main').style.marginLeft = collapsed ? 'var(--sidebar-collapsed)' : '';
  }

  // =====================================================
  // MODALES
  // =====================================================
  document.querySelectorAll('.modal-overlay').forEach(function (m) {
    m.addEventListener('click', function (e) {
      if (e.target === m) m.classList.remove('open');
    });
  });

  document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape') {
      document.querySelectorAll('.modal-overlay.open').forEach(function (m) {
        m.classList.remove('open');
      });
    }
  });
});

// Funciones globales de modal
function abrirModal(id) {
  var modal = document.getElementById(id);
  if (modal) {
    modal.classList.add('open');
    modal.style.display = 'flex';
  }
}

function cerrarModal(id) {
  var modal = document.getElementById(id);
  if (modal) {
    modal.classList.remove('open');
    modal.style.display = '';
  }
}

function verDetalle(url) {
  var existing = document.getElementById('infoModal');
  if (existing) existing.remove();

  var overlay = document.createElement('div');
  overlay.className = 'modal-overlay';
  overlay.id = 'infoModal';
  overlay.innerHTML =
    '<div class="modal-box info-modal" style="position:relative;">' +
      '<button class="modal-close-btn" onclick="cerrarModal(\'infoModal\')">' +
        '<span class="ph ph-x"></span>' +
      '</button>' +
      '<div id="infoModalBody" style="padding-top:8px;"></div>' +
    '</div>';
  document.body.appendChild(overlay);

  overlay.addEventListener('click', function (e) {
    if (e.target === overlay) cerrarModal('infoModal');
  });

  fetch(url)
    .then(function (r) { return r.text(); })
    .then(function (html) {
      var doc = new DOMParser().parseFromString(html, 'text/html');
      var card = doc.querySelector('.info-card') || doc.querySelector('.cliente-card');
      if (card) {
        document.getElementById('infoModalBody').innerHTML = card.outerHTML;
        abrirModal('infoModal');
      }
    })
    .catch(function () {
      showToast('Error al cargar los datos', 'error');
    });
}

// =====================================================
// TOAST NOTIFICATIONS
// =====================================================
function showToast(message, type) {
  type = type || 'info';
  var container = document.getElementById('toastContainer');
  if (!container) {
    container = document.createElement('div');
    container.className = 'toast-container';
    container.id = 'toastContainer';
    document.body.appendChild(container);
  }

  var icons = {
    success: 'ph-check-circle',
    error: 'ph-x-circle',
    warning: 'ph-warning',
    info: 'ph-info'
  };

  var toast = document.createElement('div');
  toast.className = 'toast toast-' + type;
  toast.innerHTML =
    '<span class="toast-icon ph ' + (icons[type] || 'ph-info') + '"></span>' +
    '<span class="toast-message">' + message + '</span>' +
    '<span class="toast-close ph ph-x" onclick="removeToast(this.parentElement)"></span>';

  container.appendChild(toast);

  // Auto-remove después de 4 segundos
  setTimeout(function () {
    removeToast(toast);
  }, 4000);
}

function removeToast(toast) {
  if (!toast || !toast.parentElement) return;
  toast.classList.add('removing');
  setTimeout(function () {
    if (toast.parentElement) {
      toast.parentElement.removeChild(toast);
    }
  }, 250);
}

// =====================================================
// BÚSQUEDA EN TABLA
// =====================================================
function initTableSearch(inputId, tableBodyId) {
  var input = document.getElementById(inputId);
  if (!input) return;
  input.addEventListener('input', function () {
    var q = input.value.toLowerCase();
    document.querySelectorAll('#' + tableBodyId + ' tr').forEach(function (row) {
      row.style.display = row.textContent.toLowerCase().includes(q) ? '' : 'none';
    });
  });
}

// =====================================================
// FILTRO DE TABLA (compatibilidad)
// =====================================================
function filtrarTabla(tablaId, valor) {
  var filas = document.querySelectorAll('#' + tablaId + ' tbody tr');
  valor = valor.toLowerCase();
  filas.forEach(function (fila) {
    fila.style.display = fila.textContent.toLowerCase().includes(valor) ? '' : 'none';
  });
}

// =====================================================
// CONFIRMACIÓN
// =====================================================
function confirmDelete(msg) {
  msg = msg || '¿Eliminar este registro?';
  return window.confirm(msg);
}

// =====================================================
// AYUDA CONTEXTUAL — iconos "?" y nubes de explicación
// =====================================================
(function () {
  var WISP_HELP = {
    // --- Login ---
    'login.usuario':  { t: 'Usuario',              d: 'Tu nombre de usuario del sistema.' },
    'login.password': { t: 'Contraseña',           d: 'Tu clave personal. Usa "Mostrar contraseña" si dudas.' },
    'login.mostrar':  { t: 'Mostrar contraseña',   d: 'Alterna entre ocultar y ver lo que escribes.' },
    'login.recordar': { t: 'Recuérdame',           d: 'Mantiene tu sesión iniciada en este equipo.' },
    'login.olvido':   { t: '¿Olvidó su contraseña?', d: 'Pide al administrador que restablezca tu acceso.' },
    'login.entrar':   { t: 'Iniciar sesión',       d: 'Accede al sistema con tu usuario y contraseña.' },

    // --- Dashboard ---
    'dashboard.mes':            { t: 'Mes',            d: 'Cambia el mes para ver los totales de ese periodo.' },
    'dashboard.clientes':       { t: 'Clientes',       d: 'Resumen del mes: activos, nuevos, pendientes y suspendidos.' },
    'dashboard.pagos':          { t: 'Pagos',          d: 'Total cobrado y pagos pendientes o vencidos del mes.' },
    'dashboard.instalaciones':  { t: 'Instalaciones',  d: 'Trabajos pendientes, atendidos y anulados del mes.' },
    'dashboard.averias':        { t: 'Averías',        d: 'Averías pendientes y atendidas del mes.' },

    // --- Clientes ---
    'clientes.titulo':    { t: 'Lista de clientes', d: 'Todos los clientes registrados. Filtra o busca para encontrarlos rápido.' },
    'clientes.filtro':    { t: 'Filtros',           d: 'Muestra solo clientes activos, suspendidos o nuevos.' },
    'clientes.buscar':    { t: 'Buscar',            d: 'Filtra por nombre o IP mientras escribes.' },
    'clientes.nuevo':     { t: 'Nuevo cliente',     d: 'Registra un cliente y asígnale Red WAN y Plan.' },
    'clientes.acciones':  { t: 'Acciones',          d: 'Ver: ficha completa · Editar: modificar datos · Suspender: cortar el servicio.' },
    'cliente.identidad':  { t: 'Datos del cliente', d: 'Nombres, apellidos y DNI para identificarlo.' },
    'cliente.redWan':     { t: 'Red WAN',           d: 'Red a la que se conecta el cliente.' },
    'cliente.plan':       { t: 'Plan',              d: 'Velocidad o paquete contratado.' },
    'cliente.estado':     { t: 'Estado',            d: 'Activo o Suspendido. Cambia aquí para reactivar.' },
    'cliente.guardar':    { t: 'Guardar',           d: 'Registra o actualiza los datos del cliente.' },

    // --- Instalaciones ---
    'instalaciones.titulo':   { t: 'Lista de instalaciones', d: 'Todas las instalaciones. Filtra por estado o busca.' },
    'instalaciones.filtro':   { t: 'Filtros',                d: 'Pendientes, atendidos o anulados.' },
    'instalaciones.buscar':   { t: 'Buscar',                 d: 'Filtra por pre-cliente o dirección.' },
    'instalaciones.nueva':    { t: 'Nueva instalación',      d: 'Agenda un trabajo de instalación para un cliente.' },
    'instalaciones.acciones': { t: 'Acciones',               d: 'Ver: ficha completa · Editar: solo mientras está Pendiente.' },
    'instalacion.cliente':    { t: 'Cliente',                d: 'Al elegirlo se carga su dirección automáticamente.' },
    'instalacion.tipo':       { t: 'Tipo',                   d: 'Estándar o Premium según el plan.' },
    'instalacion.estado':     { t: 'Estado',                 d: 'Pendiente, atendido o anulado.' },
    'instalacion.cobrar':     { t: '¿Cobrar instalación?',   d: 'Si cobras, indica el monto y el método de pago.' },
    'instalacion.guardar':    { t: 'Guardar',                d: 'Registra o actualiza la instalación.' },

    // --- Pagos ---
    'pagos.titulo':    { t: 'Lista de pagos',           d: 'Pagos del periodo. Cambia año y mes o busca un cliente.' },
    'pagos.mes':       { t: 'Mes',                      d: 'Filtra los pagos del mes elegido.' },
    'pagos.buscar':    { t: 'Buscar',                   d: 'Filtra por cliente o estado.' },
    'pagos.generar':   { t: 'Generar pagos del mes',    d: 'Crea los pagos pendientes de todos los clientes activos.' },
    'pagos.acciones':  { t: 'Acciones',                 d: 'Ver: detalle del pago · Editar: registrar el pago o cambiar su estado.' },
    'pago.monto':      { t: 'Monto',                    d: 'Importe del pago en soles.' },
    'pago.estado':     { t: 'Estado',                   d: 'Marca "Pagado" al recibir el dinero.' },
    'pago.fechaPago':  { t: 'Fecha de pago',            d: 'Día en que se recibió el pago.' },
    'pago.metodo':     { t: 'Método de pago',           d: 'Efectivo o Yape.' },
    'pago.guardar':    { t: 'Guardar',                  d: 'Confirma el registro del pago.' }
  };

  var pop = null;
  var current = null;
  var hideTimer = null;
  var canHover = window.matchMedia && window.matchMedia('(hover: hover)').matches;

  function ensure() {
    if (pop) return pop;
    pop = document.createElement('div');
    pop.id = 'helpPopover';
    pop.className = 'help-popover';
    pop.setAttribute('role', 'tooltip');
    pop.innerHTML = '<span class="help-pop-title"></span><span class="help-pop-text"></span>';
    document.body.appendChild(pop);
    return pop;
  }

  function contentFor(icon) {
    var key = icon.getAttribute('data-help');
    var entry = key && WISP_HELP[key];
    return {
      title: icon.getAttribute('data-help-title') || (entry ? entry.t : ''),
      text: icon.getAttribute('data-help-text') || (entry ? entry.d : '')
    };
  }

  function place(icon) {
    if (!pop) return;
    var r = icon.getBoundingClientRect();
    var pw = pop.offsetWidth;
    var ph = pop.offsetHeight;
    var gap = 10;
    var vw = window.innerWidth;
    var vh = window.innerHeight;
    var above = (r.bottom + gap + ph > vh) && (r.top - gap - ph > 0);
    var top = above ? (r.top - gap - ph) : (r.bottom + gap);
    var left = r.left + r.width / 2 - pw / 2;
    left = Math.max(8, Math.min(left, vw - pw - 8));
    pop.style.top = top + 'px';
    pop.style.left = left + 'px';
    pop.setAttribute('data-pos', above ? 'top' : 'bottom');
    var ax = r.left + r.width / 2 - left;
    ax = Math.max(14, Math.min(ax, pw - 14));
    pop.style.setProperty('--arrow-x', ax + 'px');
  }

  function show(icon) {
    var c = contentFor(icon);
    if (!c.text) return;
    clearTimeout(hideTimer);
    ensure();
    pop.querySelector('.help-pop-title').textContent = c.title;
    pop.querySelector('.help-pop-title').style.display = c.title ? 'block' : 'none';
    pop.querySelector('.help-pop-text').textContent = c.text;
    if (current && current !== icon) current.classList.remove('active');
    current = icon;
    icon.classList.add('active');
    icon.setAttribute('aria-describedby', 'helpPopover');
    pop.classList.add('show');
    place(icon);
  }

  function hide() {
    clearTimeout(hideTimer);
    if (pop) pop.classList.remove('show');
    if (current) {
      current.classList.remove('active');
      current.removeAttribute('aria-describedby');
    }
    current = null;
  }

  function scheduleHide() {
    clearTimeout(hideTimer);
    hideTimer = setTimeout(hide, 140);
  }

  function isIcon(t) {
    return t && t.closest ? t.closest('.help-icon') : null;
  }
  function inPopover(t) {
    return t && t.closest ? t.closest('.help-popover') : null;
  }

  document.addEventListener('click', function (e) {
    var icon = isIcon(e.target);
    if (icon) {
      e.preventDefault();
      if (current === icon) hide(); else show(icon);
      return;
    }
    if (current && !inPopover(e.target)) hide();
  });

  document.addEventListener('mouseover', function (e) {
    if (!canHover) return;
    var icon = isIcon(e.target);
    if (icon) { clearTimeout(hideTimer); show(icon); }
    else if (inPopover(e.target)) clearTimeout(hideTimer);
  });

  document.addEventListener('mouseout', function (e) {
    if (!canHover) return;
    if (isIcon(e.target) || inPopover(e.target)) scheduleHide();
  });

  document.addEventListener('focusin', function (e) {
    var icon = isIcon(e.target);
    if (icon) show(icon);
  });

  document.addEventListener('focusout', function (e) {
    if (isIcon(e.target)) scheduleHide();
  });

  document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape') hide();
  });

  window.addEventListener('resize', function () { if (current) place(current); });
  window.addEventListener('scroll', function () { if (current) place(current); }, true);
})();
