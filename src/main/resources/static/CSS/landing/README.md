# CSS de la landing · Pet'Salud

La hoja de estilos de la landing pública (`/`) está dividida en **un archivo por
fragmento HTML**, igual que los templates de `templates/landing/`. Cada archivo
contiene los estilos de su sección **y sus propias reglas responsive**, de modo
que cada bloque se puede leer y modificar de forma aislada.

## Archivos

| Archivo | Fragmento HTML | Contenido |
|---|---|---|
| `base.css` | — (global) | Tokens (`:root`), reset, tipografía, foco visible, utilidades (`.container`, `.u-visually-hidden`, `.skip-link`), botones (`.btn*`), variantes de sección (`.section*`) y la marca compartida (`.brand*`) |
| `header.css` | `landing/header.html` | Barra fija (`.site-header*`), navegación (`.site-nav*`) y botón hamburguesa (`.nav-toggle`) |
| `hero.css` | `landing/hero.html` | Portada (`.hero*`) |
| `servicios.css` | `landing/servicios.html` | Tarjetas de servicio (`.servicio*`), mosaico (`.mosaic*`), foto que se revela al hacer hover (`.servicio__foto`) y el fondo fotográfico de la sección (`.section--foto`) |
| `pacientes.css` | `landing/pacientes.html` | Carrusel (`.pacientes__*`, `.paciente*`) y modal de opinión (`.paciente-modal*`) |
| `contacto.css` | `landing/contacto.html` | Canales (`.canal*`), formulario (`.form-card*`, `.field*`) y mensaje de éxito (`.form-success*`) |
| `ubicacion.css` | `landing/ubicacion.html` | Datos (`.datos`, `.dato__*`), CTA (`.ubicacion__cta`) y mapa (`.mapa`) |
| `footer.css` | `landing/footer.html` | Pie de página (`.site-footer*`) y redes sociales (`.social-btn`) |

## Cómo se cargan

Todos se enlazan en `templates/landing/head.html`, en este orden:

```html
<link rel="stylesheet" th:href="@{/css/landing/base.css}">
<link rel="stylesheet" th:href="@{/css/landing/header.css}">
<link rel="stylesheet" th:href="@{/css/landing/hero.css}">
<link rel="stylesheet" th:href="@{/css/landing/servicios.css}">
<link rel="stylesheet" th:href="@{/css/landing/pacientes.css}">
<link rel="stylesheet" th:href="@{/css/landing/contacto.css}">
<link rel="stylesheet" th:href="@{/css/landing/ubicacion.css}">
<link rel="stylesheet" th:href="@{/css/landing/footer.css}">
```

**El orden importa** (la cascada de CSS no depende del archivo, sino del orden
en que se enlazan):

1. `base.css` va **primero** porque define los tokens (`--teal-900`, `--radius`,
   `--huellas`, …) y los componentes compartidos.
2. Los demás siguen el **orden de las secciones en la página**, de modo que un
   archivo pueda pisar a otro si hiciera falta (por ejemplo, `footer.css`
   redefine `.brand` para el fondo teal).

## Fotos de la sección de servicios

Cada tarjeta de servicio trae su propia foto en `static/img/servicios/`:

| Servicio | Archivo |
|---|---|
| Consulta general | `consulta.jpg` |
| Tratamientos | `tratamientos.webp` |
| Vacunas | `cavunas.webp` ⚠️ (el nombre del archivo tiene el typo "cavunas") |
| Exámenes de laboratorio | `laboratorio.webp` |
| Profilaxis | `profilaxis.webp` |
| Cirugías | `cirugias.webp` |
| Baños y/o cortes | `banos.webp` |

- En la tarjeta, la foto es un `<img class="servicio__foto">` **decorativo**
  (`alt=""` + `aria-hidden`): está oculto en reposo y se revela al pasar el
  cursor (`opacity` + `scale`), con un velo oscuro encima para que el título
  y la descripción se lean en blanco.
- En pantallas táctiles (sin hover) la foto se queda como textura sutil
  (`@media (hover: none)`).
- El fondo de la sección (`.section--foto`) usa `tratamientos.webp` — es la
  única foto con resolución suficiente (2560×1707) para cubrir la sección
  entera — con un lavado verde claro encima para no perder legibilidad.
- En CSS las imágenes se referencian **relativas a la hoja**
  (`../../img/servicios/...`): así funcionan también con un context path.

## Reglas para tocar el CSS

- Cada selector vive en el archivo de su fragmento. Si un estilo se usa en dos
  secciones, va en `base.css`.
- Los `@media` de cada bloque están **al final de su propio archivo**, después
  de las reglas base de ese bloque.
- Al agregar un componente nuevo: primero se lo ubica en el fragmento que lo
  usa; si lo usan varios, va a `base.css`.
- Las URLs siguen la convención del proyecto (`/css/...` en minúsculas para la
  carpeta `static/CSS/`). Ojo: esa diferencia de mayúsculas funciona en
  Windows/macOS, pero en un servidor Linux la carpeta `CSS` tendría que
  renombrarse a `css` (o las URLs escribirse con mayúscula) para no dar 404.

