(function () {
    'use strict';

    var reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    var scrollBehavior = reduceMotion ? 'auto' : 'smooth';

    /* ------------------------------------------------------------------
       Menú móvil: botón hamburguesa, aria-expanded y cierre con Escape
       ------------------------------------------------------------------ */
    var toggle = document.getElementById('navToggle');
    var nav = document.getElementById('primaryNav');

    function setMenu(open) {
        nav.classList.toggle('is-open', open);
        toggle.setAttribute('aria-expanded', open ? 'true' : 'false');
        toggle.setAttribute('aria-label', open ? 'Cerrar el menú de navegación' : 'Abrir el menú de navegación');
    }

    if (toggle && nav) {
        toggle.addEventListener('click', function () {
            setMenu(toggle.getAttribute('aria-expanded') !== 'true');
        });

        nav.addEventListener('click', function (ev) {
            if (ev.target.closest('a')) { setMenu(false); }
        });

        document.addEventListener('keydown', function (ev) {
            if (ev.key === 'Escape' && toggle.getAttribute('aria-expanded') === 'true') {
                setMenu(false);
                toggle.focus();
            }
        });

        // El breakpoint del panel hamburguesa es 1024px (ver static/CSS/landing.css).
        var wide = window.matchMedia('(min-width: 1024px)');
        function closeOnWide() {
            if (wide.matches) { setMenu(false); }
        }
        closeOnWide();
        if (wide.addEventListener) { wide.addEventListener('change', closeOnWide); }
        else if (wide.addListener) { wide.addListener(closeOnWide); }
    }

    /* ------------------------------------------------------------------
       Rutas de sección + scroll suave + sección activa en el menú

       Cada sección tiene su propia ruta ("/servicios", "/contacto", ...) y el
       id del <section> es ese mismo nombre, así que la sección se resuelve a
       partir del href: "/servicios" -> id "servicios".
       ------------------------------------------------------------------ */
    function sectionId(href) {
        if (!href) { return null; }
        var path = href.split('?')[0].split('#')[0];
        if (path === '/' || path === '') { return 'inicio'; }
        var id = path.replace(/^\/+/, '');
        return id || null;
    }

    var navLinks = Array.prototype.slice.call(document.querySelectorAll('[data-nav]'));
    var sections = navLinks
        .map(function (link) { return document.getElementById(sectionId(link.getAttribute('href'))); })
        .filter(Boolean);

    function goTo(target) {
        if (!target) { return; }
        target.scrollIntoView({ behavior: scrollBehavior, block: 'start' });
        // El foco acompaña al scroll para que el teclado no quede atrás.
        target.setAttribute('tabindex', '-1');
        target.focus({ preventScroll: true });
    }

    document.addEventListener('click', function (ev) {
        var link = ev.target.closest('a[href]');
        if (!link) { return; }
        var href = link.getAttribute('href');
        if (!href || href === '#') { return; }

        // Ancla dentro de la misma página (por ejemplo el enlace de salto
        // al contenido principal).
        if (href.charAt(0) === '#') {
            var anchor = document.querySelector(href);
            if (!anchor) { return; }
            ev.preventDefault();
            goTo(anchor);
            history.replaceState(null, '', href);
            return;
        }

        // Ruta de sección: se hace scroll sin recargar y la URL queda
        // actualizada. Si no hay ninguna sección con ese id (por ejemplo
        // un enlace externo o /login), el navegador navega con normalidad.
        var target = document.getElementById(sectionId(href));
        if (!target) { return; }
        ev.preventDefault();
        goTo(target);
        history.pushState(null, '', href);
    });

    // Los CTA "Pedir cita" llevan a la sección de contacto y enfocan el formulario.
    document.querySelectorAll('[data-cta="cita"]').forEach(function (cta) {
        cta.addEventListener('click', function () {
            window.setTimeout(function () {
                var nombre = document.getElementById('nombre');
                if (nombre) { nombre.focus({ preventScroll: true }); }
            }, reduceMotion ? 0 : 450);
        });
    });

    function setActive(id) {
        navLinks.forEach(function (link) {
            var active = sectionId(link.getAttribute('href')) === id;
            link.classList.toggle('is-active', active);
            if (active) { link.setAttribute('aria-current', 'true'); }
            else { link.removeAttribute('aria-current'); }
        });
    }

    if ('IntersectionObserver' in window && sections.length) {
        var observer = new IntersectionObserver(function (entries) {
            entries.forEach(function (entry) {
                if (entry.isIntersecting) { setActive(entry.target.id); }
            });
        }, { rootMargin: '-45% 0px -50% 0px', threshold: 0 });

        sections.forEach(function (section) { observer.observe(section); });
    }

    /* Si se entra por una ruta de sección ("/servicios"), se posiciona en esa
       sección. Se repite al cargar las imágenes, salvo que el visitante ya se
       haya movido (rueda, toque o teclado). */
    var userMoved = false;
    ['wheel', 'touchstart', 'keydown'].forEach(function (evt) {
        window.addEventListener(evt, function () { userMoved = true; }, { passive: true, capture: true });
    });

    function alignWithUrl() {
        if (userMoved) { return; }
        var target = document.getElementById(sectionId(window.location.pathname));
        if (target) { target.scrollIntoView({ behavior: 'auto', block: 'start' }); }
    }
    alignWithUrl();
    window.addEventListener('load', alignWithUrl);

    // Botón "atrás" del navegador: vuelve a la sección de la URL anterior.
    window.addEventListener('popstate', function () {
        var target = document.getElementById(sectionId(window.location.pathname));
        if (target) { goTo(target); }
    });

    /* ------------------------------------------------------------------
       Carrusel de pacientes: botones, teclado y scroll-snap
       ------------------------------------------------------------------ */
    var track = document.getElementById('pacientesTrack');
    var prevBtn = document.getElementById('prevPaciente');
    var nextBtn = document.getElementById('nextPaciente');
    var status = document.getElementById('pacientesStatus');

    if (track && prevBtn && nextBtn) {
        var cards = Array.prototype.slice.call(track.querySelectorAll('.paciente'));

        function visibleCount() {
            if (!cards.length) { return 1; }
            var trackRect = track.getBoundingClientRect();
            var visible = cards.filter(function (card) {
                var box = card.getBoundingClientRect();
                return box.left < trackRect.right - 1 && box.right > trackRect.left + 1;
            }).length;
            return Math.max(1, visible);
        }

        function stepSize() {
            if (!cards.length) { return track.clientWidth; }
            var first = cards[0].getBoundingClientRect();
            var second = cards[1] ? cards[1].getBoundingClientRect() : null;
            var gap = second ? (second.left - first.left - first.width) : 0;
            var unit = first.width + gap;
            return unit * visibleCount();
        }

        // Índice de la primera tarjeta que queda pegada al borde izquierdo.
        function currentIndex() {
            var trackRect = track.getBoundingClientRect();
            var index = 0;
            cards.forEach(function (card, i) {
                var box = card.getBoundingClientRect();
                if (box.left <= trackRect.left + 4 && box.right > trackRect.left + 4) { index = i; }
            });
            return index;
        }

        function updateStatus() {
            var max = track.scrollWidth - track.clientWidth;
            if (status) {
                var text = 'Paciente ' + (currentIndex() + 1) + ' de ' + cards.length;
                // Solo se anuncia cuando cambia, para no saturar al lector de pantalla.
                if (status.textContent !== text) { status.textContent = text; }
            }
            prevBtn.disabled = track.scrollLeft <= 4;
            nextBtn.disabled = track.scrollLeft >= max - 4;
        }

        function move(direction) {
            track.scrollBy({ left: direction * stepSize(), behavior: scrollBehavior });
        }

        prevBtn.addEventListener('click', function () { move(-1); });
        nextBtn.addEventListener('click', function () { move(1); });

        track.addEventListener('keydown', function (ev) {
            if (ev.key === 'ArrowRight') { ev.preventDefault(); move(1); }
            if (ev.key === 'ArrowLeft') { ev.preventDefault(); move(-1); }
        });

        var ticking = false;
        track.addEventListener('scroll', function () {
            if (ticking) { return; }
            ticking = true;
            window.requestAnimationFrame(function () {
                updateStatus();
                ticking = false;
            });
        });

        window.addEventListener('resize', updateStatus);
        updateStatus();
    }

    /* ------------------------------------------------------------------
       Modal de opinión del paciente: al pulsar una tarjeta se abre con la
       opinión del dueño y quién la escribió. Los datos se leen de la tarjeta
       pulsada, que los guarda ocultos en .paciente__detalle.
       ------------------------------------------------------------------ */
    var modal = document.getElementById('pacienteModal');

    if (modal) {
        var fotoModal = document.getElementById('pacienteModalFoto');
        var tagModal = document.getElementById('pacienteModalTag');
        var nombreModal = document.getElementById('pacienteModalNombre');
        var razaModal = document.getElementById('pacienteModalRaza');
        var citaModal = document.getElementById('pacienteModalCita');
        var autorModal = document.getElementById('pacienteModalAutor');
        var ultimaTarjeta = null;
        var sinDialogo = typeof modal.showModal !== 'function';

        // Texto de un elemento dentro de la tarjeta pulsada (no de la primera)
        function deTarjeta(card, selector) {
            var el = card.querySelector(selector);
            return el ? el.textContent.trim() : '';
        }

        // Con el modal abierto el fondo de la pagina no debe desplazarse.
        // Se fija el body y se guarda la posicion para restaurarla al cerrar.
        var scrollY = 0;
        function bloquearScroll() {
            scrollY = window.scrollY || window.pageYOffset || 0;
            document.body.style.position = 'fixed';
            document.body.style.top = -scrollY + 'px';
            document.body.style.left = '0';
            document.body.style.right = '0';
            document.body.style.width = '100%';
        }
        function desbloquearScroll() {
            document.body.style.position = '';
            document.body.style.top = '';
            document.body.style.left = '';
            document.body.style.right = '';
            document.body.style.width = '';
            window.scrollTo(0, scrollY);
        }

        function alCerrar() {
            desbloquearScroll();
            // El foco vuelve a la tarjeta desde la que se abrió el modal.
            if (ultimaTarjeta) {
                ultimaTarjeta.focus();
                ultimaTarjeta = null;
            }
        }

        function abrir() {
            bloquearScroll();
            if (sinDialogo) {
                modal.setAttribute('open', '');
                modal.classList.add('is-open');
            } else {
                modal.showModal();
            }
        }

        function cerrar() {
            if (sinDialogo) {
                modal.removeAttribute('open');
                modal.classList.remove('is-open');
                alCerrar();
            } else {
                modal.close();   // dispara el evento "close" -> alCerrar()
            }
        }

        document.querySelectorAll('[data-paciente]').forEach(function (boton) {
            boton.addEventListener('click', function () {
                var card = boton.closest('.paciente');
                if (!card) { return; }

                var foto = card.querySelector('.paciente__foto img');
                if (fotoModal && foto) {
                    fotoModal.src = foto.currentSrc || foto.src;
                    fotoModal.alt = foto.alt || '';
                }
                if (tagModal) { tagModal.textContent = deTarjeta(card, '.paciente__tag'); }
                if (nombreModal) { nombreModal.textContent = deTarjeta(card, '.paciente__name'); }
                if (razaModal) { razaModal.textContent = deTarjeta(card, '.paciente__breed'); }
                if (citaModal) { citaModal.textContent = deTarjeta(card, '.paciente__quote'); }
                if (autorModal) { autorModal.textContent = deTarjeta(card, '.paciente__owner'); }

                ultimaTarjeta = boton;
                abrir();
            });
        });

        // Cerrar con la X
        var cerrarBtn = modal.querySelector('[data-cerrar-paciente]');
        if (cerrarBtn) { cerrarBtn.addEventListener('click', cerrar); }

        // Cerrar pulsando el fondo oscurecido (fuera del recuadro)
        modal.addEventListener('click', function (ev) {
            if (ev.target !== modal) { return; }
            var caja = modal.getBoundingClientRect();
            var dentro = ev.clientX >= caja.left && ev.clientX <= caja.right
                      && ev.clientY >= caja.top && ev.clientY <= caja.bottom;
            if (!dentro) { cerrar(); }
        });

        // Escape: el <dialog> nativo ya lo cierra; en el respaldo hay que hacerlo a mano
        if (sinDialogo) {
            document.addEventListener('keydown', function (ev) {
                if (ev.key === 'Escape' && modal.hasAttribute('open')) { cerrar(); }
            });
        }

        // Al cerrarse (por Escape, por la X o por el fondo) se restaura el foco
        modal.addEventListener('close', alCerrar);
    }

    /* ------------------------------------------------------------------
       Formulario de contacto: validación en el cliente y estado de éxito
       ------------------------------------------------------------------ */
    var form = document.getElementById('contactForm');
    var success = document.getElementById('formSuccess');

    var rules = {
        nombre: function (value) {
            if (!value.trim()) { return 'Escribe tu nombre.'; }
            if (value.trim().length < 2) { return 'Mínimo 2 caracteres.'; }
            return '';
        },
        telefono: function (value) {
            var digits = value.replace(/\D/g, '');
            if (!digits) { return 'Escribe tu teléfono.'; }
            if (digits.length < 7) { return 'Mínimo 7 dígitos.'; }
            return '';
        },
        email: function (value) {
            var correo = value.trim();
            if (!correo) { return 'Escribe tu correo.'; }
            if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(correo)) { return 'Correo no válido.'; }
            return '';
        },
        mascota: function (value) {
            if (!value.trim()) { return 'Escribe su nombre.'; }
            if (value.trim().length < 2) { return 'Mínimo 2 caracteres.'; }
            return '';
        },
        especie: function (value) {
            if (!value) { return 'Elige una especie.'; }
            return '';
        },
        mensaje: function (value) {
            if (!value.trim()) { return 'Cuéntanos tu consulta.'; }
            if (value.trim().length < 10) { return 'Mínimo 10 caracteres.'; }
            return '';
        }
    };

    function showError(id, message) {
        var input = document.getElementById(id);
        var box = document.querySelector('[data-error-for="' + id + '"]');
        if (!input || !box) { return; }
        if (message) {
            input.setAttribute('aria-invalid', 'true');
            input.setAttribute('aria-describedby', box.id);
            box.textContent = message;
            box.classList.add('is-visible');
        } else {
            input.removeAttribute('aria-invalid');
            input.removeAttribute('aria-describedby');
            box.textContent = '';
            box.classList.remove('is-visible');
        }
    }

    if (form && success) {
        /* Campos con los que el usuario ya interactuó de verdad.
           Un campo que solo recibió un clic NO se valida al salir: el error
           saltaría aunque no se haya escrito nada y descolocaría el formulario. */
        var tocados = {};

        Object.keys(rules).forEach(function (id) {
            var input = document.getElementById(id);
            if (!input) { return; }

            input.addEventListener('input', function () {
                tocados[id] = true;
                // Si el campo ya está marcado, el error se va corrigiendo al escribir.
                if (input.getAttribute('aria-invalid') === 'true') {
                    showError(id, rules[id](input.value));
                }
            });

            input.addEventListener('change', function () {
                tocados[id] = true;
                showError(id, rules[id](input.value));
            });

            input.addEventListener('blur', function () {
                if (!tocados[id]) { return; }
                showError(id, rules[id](input.value));
            });
        });

        var formError = document.getElementById('formError');
        var submitBtn = form.querySelector('button[type="submit"]');

        function mostrarErrorFormulario(message) {
            if (!formError) { return; }
            formError.textContent = message;
            formError.classList.add('is-visible');
        }

        function limpiarErrorFormulario() {
            if (!formError) { return; }
            formError.textContent = '';
            formError.classList.remove('is-visible');
        }

        form.addEventListener('submit', async function (ev) {
            ev.preventDefault();
            var firstInvalid = null;

            limpiarErrorFormulario();

            Object.keys(rules).forEach(function (id) {
                var input = document.getElementById(id);
                if (!input) { return; }
                tocados[id] = true;   // al intentar enviar, sí se muestran todos
                var message = rules[id](input.value);
                showError(id, message);
                if (message && !firstInvalid) { firstInvalid = input; }
            });

            if (firstInvalid) {
                firstInvalid.focus();
                return;
            }

            var accessKey = form.querySelector('[name="access_key"]');
            if (!accessKey || !accessKey.value.trim()) {
                mostrarErrorFormulario('Falta configurar WEB3FORMS_ACCESS_KEY en el archivo .env.');
                return;
            }

            var htmlOriginal = submitBtn ? submitBtn.innerHTML : '';

            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.setAttribute('aria-busy', 'true');
                submitBtn.textContent = 'Enviando…';
            }

            try {
                // Web3Forms recomienda enviar los datos como JSON.
                var datos = Object.fromEntries(new FormData(form));

                var respuesta = await fetch(form.action, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    },
                    body: JSON.stringify(datos)
                });

                var resultado = await respuesta.json().catch(function () { return {}; });

                if (!respuesta.ok || resultado.success !== true) {
                    throw new Error(resultado.message || 'No se pudo enviar el mensaje.');
                }

                form.style.display = 'none';
                success.classList.add('is-visible');
                success.scrollIntoView({ behavior: scrollBehavior, block: 'center' });

            } catch (error) {
                mostrarErrorFormulario('No pudimos enviar tu mensaje. Inténtalo de nuevo o escríbenos por WhatsApp.');

                if (submitBtn) {
                    submitBtn.disabled = false;
                    submitBtn.removeAttribute('aria-busy');
                    submitBtn.innerHTML = htmlOriginal;
                }
            }
        });
    }

    /* ------------------------------------------------------------------
       Hero: único efecto de carga (la foto aparece cuando termina de cargar)
       ------------------------------------------------------------------ */
    var heroPhoto = document.getElementById('heroPhoto');
    if (heroPhoto) {
        var heroImg = heroPhoto.querySelector('img');
        if (heroImg) {
            var markLoaded = function () { heroPhoto.classList.add('is-loaded'); };
            if (heroImg.complete && heroImg.naturalWidth > 0) { markLoaded(); }
            else {
                heroImg.addEventListener('load', markLoaded, { once: true });
                heroImg.addEventListener('error', markLoaded, { once: true });
                // Red de seguridad: si el evento nunca llega, la foto se muestra igual.
                window.setTimeout(markLoaded, 4000);
            }
        }
    }
})();