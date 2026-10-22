document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('contactForm');
    const exito = document.getElementById('formSuccess');
    const errorEnvio = document.getElementById('formError');
    if (!form) return;
    const boton = form.querySelector('button[type="submit"]');

    const mensajes = {
        nombre: 'Escribe tu nombre.',
        telefono: 'Escribe un teléfono de contacto válido.',
        mascota: 'Escribe el nombre de tu mascota.',
        especie: 'Elige una especie.',
        mensaje: 'Cuéntanos en qué podemos ayudarte.'
    };

    const campos = Object.keys(mensajes).map(n => form.elements[n]);

    function validar(campo) {
        const aviso = form.querySelector(`[data-error-for="${campo.name}"]`);
        let valido = campo.value.trim() !== '';
        if (valido && campo.name === 'telefono') {
            valido = /^[+\d\s()-]{7,20}$/.test(campo.value.trim());
        }
        campo.setAttribute('aria-invalid', String(!valido));
        if (aviso) aviso.textContent = valido ? '' : mensajes[campo.name];
        return valido;
    }

    campos.forEach(c => c.addEventListener('input', () => {
        if (c.getAttribute('aria-invalid') === 'true') validar(c);
    }));

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        errorEnvio.textContent = '';

        const resultados = campos.map(validar);
        if (resultados.includes(false)) {
            campos[resultados.indexOf(false)].focus();   // lleva al primer error
            return;
        }

        boton.disabled = true;
        const textoOriginal = boton.textContent;
        boton.textContent = 'Enviando…';

        try {
            const res = await fetch('https://api.web3forms.com/submit', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
                body: JSON.stringify(Object.fromEntries(new FormData(form)))
            });
            const data = await res.json();
            if (!data.success) throw new Error(data.message);

            form.reset();
            form.hidden = true;
            exito.hidden = false;
            exito.classList.add('is-visible');       // ajusta al nombre de clase que use tu CSS
            exito.setAttribute('tabindex', '-1');
            exito.focus();
        } catch (err) {
            errorEnvio.textContent =
                'No pudimos enviar el mensaje. Escríbenos por WhatsApp al 936 468 431.';
        } finally {
            boton.disabled = false;
            boton.textContent = textoOriginal;
        }
    });
});