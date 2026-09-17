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
