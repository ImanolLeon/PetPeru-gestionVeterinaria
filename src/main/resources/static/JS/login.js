/* ============================================================================
   Pantalla de login · mostrar / ocultar contraseña
   ============================================================================ */
(function () {
    'use strict';

    var toggle = document.getElementById('togglePassword');
    var input = document.getElementById('password');

    if (!toggle || !input) return;

    toggle.addEventListener('click', function () {
        var mostrar = input.type === 'password';

        input.type = mostrar ? 'text' : 'password';
        toggle.classList.toggle('is-visible', mostrar);
        toggle.setAttribute('aria-pressed', String(mostrar));
        toggle.setAttribute('aria-label', mostrar ? 'Ocultar contraseña' : 'Mostrar contraseña');

        input.focus();
    });
})();
