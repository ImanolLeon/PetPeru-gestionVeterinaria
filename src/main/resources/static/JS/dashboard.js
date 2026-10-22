/* =========================================================================
   COLITA FELIZ · JavaScript del panel
   1) Sidebar colapsable en móvil.
   2) Búsqueda en vivo sobre la tabla de clientes.
   3) Rellenar el modal de "Editar cliente" con los datos de la fila.
   4) Rellenar el modal de "Datos de la mascota" al pulsar una card.
   5) Modal de confirmación reutilizable para salidas y borrados.
   6) Aviso en vivo si el DNI escrito ya es de otro cliente.
   Se ejecuta en todas las páginas, así que cada bloque comprueba que sus
   elementos existan antes de usarlos.
   ========================================================================= */
(function () {
  'use strict';

  /* -------------------- 1. Sidebar colapsable (móvil) -------------------- */
  var sidebar   = document.getElementById('sidebar');
  var overlay   = document.getElementById('overlay');
  var toggleBtn = document.getElementById('menuToggle');

  function openSidebar() {
    if (!sidebar || !overlay || !toggleBtn) return;
    sidebar.classList.add('is-open');
    overlay.hidden = false;
    // Reflow para que la transición de opacidad se aplique
    requestAnimationFrame(function () { overlay.classList.add('is-visible'); });
    toggleBtn.setAttribute('aria-expanded', 'true');
  }

  function closeSidebar() {
    if (!sidebar || !overlay || !toggleBtn) return;
    sidebar.classList.remove('is-open');
    overlay.classList.remove('is-visible');
    overlay.hidden = true;
    toggleBtn.setAttribute('aria-expanded', 'false');
  }

  if (toggleBtn) {
    toggleBtn.addEventListener('click', function () {
      sidebar.classList.contains('is-open') ? closeSidebar() : openSidebar();
    });
  }
  if (overlay) overlay.addEventListener('click', closeSidebar);
  document.addEventListener('keydown', function (e) { if (e.key === 'Escape') closeSidebar(); });

  /* -------------------- 2. Búsqueda de clientes -------------------- */
  var input   = document.getElementById('clientSearch');
  var table   = document.getElementById('clientsTable');
  var summary = document.getElementById('tableSummary');

  if (input && table && summary) {
    var rows = Array.prototype.slice.call(table.querySelectorAll('tbody tr[data-row]'));

    input.addEventListener('input', function () {
      var term = input.value.trim().toLowerCase();
      var visible = 0;

      rows.forEach(function (row) {
        var match = row.textContent.toLowerCase().indexOf(term) !== -1;
        row.style.display = match ? '' : 'none';
        if (match) visible++;
      });

      summary.textContent = term
        ? visible + (visible === 1 ? ' coincidencia' : ' coincidencias') + ' de ' + rows.length + ' clientes'
        : 'Mostrando 1 a ' + rows.length + ' de ' + rows.length + ' clientes';
    });
  }

  /* -------------------- 3. Modal editar cliente -------------------- */
  // Al abrir el modal, copiamos los data-* del botón (lápiz) a los campos del formulario.
  var editModal = document.getElementById('editClienteModal');

  if (editModal) {
    editModal.addEventListener('show.bs.modal', function (event) {
      var btn = event.relatedTarget; // botón que disparó el modal
      if (!btn) return;

      var map = {
        'edit-id': 'data-id',
        'edit-dni': 'data-dni',
        'edit-nombre': 'data-nombre',
        'edit-apellido': 'data-apellido',
        'edit-email': 'data-email',
        'edit-telefono': 'data-telefono',
        'edit-direccion': 'data-direccion'
      };

      Object.keys(map).forEach(function (fieldId) {
        var field = document.getElementById(fieldId);
        if (!field) return;
        var value = btn.getAttribute(map[fieldId]);
        field.value = value != null ? value : '';
      });
    });
  }

  /* -------------------- 4. Modal "Datos de la mascota" -------------------- */
  // Al pulsar una card de mascota, copiamos sus data-* al modal de detalle.
  var petModal = document.getElementById('petDetailModal');

  if (petModal) {
    petModal.addEventListener('show.bs.modal', function (event) {
      var card = event.relatedTarget; // card (botón) que abrió el modal
      if (!card) return;

      function set(id, value) {
        var el = document.getElementById(id);
        if (el) el.textContent = (value != null && value !== '') ? value : '—';
      }

      var nombre   = card.getAttribute('data-nombre');
      var especie  = card.getAttribute('data-especie');
      var raza     = card.getAttribute('data-raza');
      var sexo     = card.getAttribute('data-sexo');
      var edad     = card.getAttribute('data-edad');
      var peso     = card.getAttribute('data-peso');
      var cliente  = card.getAttribute('data-cliente');
      var imagen   = card.getAttribute('data-imagen');

      set('pd-nombre', nombre);
      set('pd-sub', [especie, (raza || 'Sin raza especificada')].join(' · '));
      set('pd-especie', especie);
      set('pd-raza', raza);
      set('pd-sexo', sexo);
      set('pd-edad', (edad != null && edad !== '') ? edad + ' años' : null);
      set('pd-peso', (peso != null && peso !== '') ? peso + ' kg' : null);
      set('pd-cliente', cliente);

      // Imagen: si la mascota tiene, se muestra; si no, queda el icono.
      var img         = document.getElementById('pd-imagen');
      var placeholder = document.getElementById('pd-placeholder');

      if (img && placeholder) {
        if (imagen) {
          img.src = imagen;
          img.alt = nombre || 'Mascota';
          img.style.display = 'block';
          placeholder.style.display = 'none';
        } else {
          img.removeAttribute('src');
          img.style.display = 'none';
          placeholder.style.display = '';
        }
      }
    });
  }

  /* -------------------- 5. Modal de confirmación (salir / eliminar) ---- */
  // Cualquier elemento con [data-confirm] abre el modal de confirmación en
  // vez de actuar directamente. Atributos opcionales:
  //   data-confirm-title  → encabezado del modal
  //   data-confirm-text   → mensaje
  //   data-confirm-label  → texto del botón de confirmar
  //   data-confirm-action → URL a la que hacer POST (logout). Si no existe:
  //                          · si el elemento está dentro de un <form>, se envía
  //                          · si es un <a>, se navega a su href
  var confirmModal = document.getElementById('confirmModal');
  var confirmOk    = document.getElementById('confirmModalOk');

  function setText(id, value) {
    var el = document.getElementById(id);
    if (el) el.textContent = value;
  }

  if (confirmModal && confirmOk && typeof bootstrap !== 'undefined') {
    var bsConfirm = bootstrap.Modal.getOrCreateInstance(confirmModal);
    var pendiente = null;

    document.addEventListener('click', function (e) {
      var trigger = (e.target && e.target.closest) ? e.target.closest('[data-confirm]') : null;
      if (!trigger) return;

      e.preventDefault(); // nada de navegar ni enviar todavía
      pendiente = trigger;

      setText('confirmModalTitle', trigger.getAttribute('data-confirm-title') || '¿Estás seguro?');
      setText('confirmModalText', trigger.getAttribute('data-confirm-text') || '¿Seguro que quieres continuar?');
      confirmOk.textContent = trigger.getAttribute('data-confirm-label') || 'Sí, continuar';

      bsConfirm.show();
    });

    confirmOk.addEventListener('click', function () {
      var el = pendiente;
      pendiente = null;
      if (!el) return;

      bsConfirm.hide();

      var action = el.getAttribute('data-confirm-action');
      if (action) {                       // 1) POST a la URL indicada
        var form = document.createElement('form');
        form.method = 'POST';
        form.action = action;
        document.body.appendChild(form);
        form.submit();
        return;
      }
      if (el.form) { el.form.submit(); return; }   // 2) botón dentro de un <form>
      if (el.href) { window.location.href = el.href; } // 3) enlace normal
    });

    confirmModal.addEventListener('hidden.bs.modal', function () { pendiente = null; });
  }

  /* -------------------- 6. DNI único (aviso en vivo) -------------------- */
  // Mientras se escribe el DNI se pregunta al servidor si ya es de otro
  // cliente. Si lo es, se avisa bajo el campo y no se deja enviar el
  // formulario. El servidor lo vuelve a comprobar: esto es solo un aviso.
  var camposDni = [
    { input: 'dni',      aviso: 'dniError',     oculto: null,      form: null },
    { input: 'edit-dni', aviso: 'editDniError', oculto: 'edit-id', form: 'editClienteForm' }
  ];

  camposDni.forEach(function (cfg) {
    var input = document.getElementById(cfg.input);
    if (!input) return;

    var form   = cfg.form ? document.getElementById(cfg.form) : input.closest('form');
    var oculto = cfg.oculto ? document.getElementById(cfg.oculto) : null;
    var boton  = form ? form.querySelector('button[type="submit"]') : null;

    // El mensaje lo dibuja el servidor cuando rechaza el DNI; si no existe, lo creamos
    var aviso = document.getElementById(cfg.aviso);
    if (!aviso) {
      aviso = document.createElement('small');
      aviso.id = cfg.aviso;
      aviso.className = 'field-error';
      aviso.hidden = true;
      input.insertAdjacentElement('afterend', aviso);
    }

    var duplicado = false;
    var timer = null;
    var consultado = null;

    function limpiar() {
      duplicado = false;
      consultado = null;
      input.classList.remove('is-duplicated');
      aviso.hidden = true;
      aviso.textContent = '';
      if (boton) boton.disabled = false;
    }

    function marcarDuplicado() {
      duplicado = true;
      input.classList.add('is-duplicated');
      aviso.textContent = 'El DNI ya está registrado.';
      aviso.hidden = false;
      if (boton) boton.disabled = true;
    }

    function comprobar() {
      var valor = input.value.trim();
      if (!valor) { limpiar(); return; }
      if (valor === consultado) return;
      consultado = valor;

      var id  = (oculto && oculto.value) ? oculto.value : '';
      var url = '/clientes/disponible?dni=' + encodeURIComponent(valor)
              + '&id=' + encodeURIComponent(id);

      fetch(url, { headers: { 'Accept': 'text/plain' } })
        .then(function (r) { return r.ok ? r.text() : 'false'; })
        .then(function (texto) {
          if (input.value.trim() !== valor) return;   // el usuario siguió escribiendo
          if (texto.trim() === 'true') { marcarDuplicado(); } else { limpiar(); }
        })
        .catch(function () { /* si la comprobación falla, no bloqueamos nada */ });
    }

    input.addEventListener('input', function () {
      clearTimeout(timer);
      limpiar();
      timer = setTimeout(comprobar, 400);
    });

    input.addEventListener('blur', function () {
      clearTimeout(timer);
      comprobar();
    });

    if (form) {
      form.addEventListener('submit', function (e) {
        if (!duplicado) return;
        e.preventDefault();
        input.focus();
      });
    }
  });
})();
