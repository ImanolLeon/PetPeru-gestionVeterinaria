package com.example.GestionVeterinaria.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Galería de imágenes del panel.
 * Lee lo que haya en static/img y lo devuelve como rutas para el navegador,
 * así se pueden añadir o quitar fotos sin tocar código: basta con copiar el
 * archivo a src/main/resources/static/img.
 */
@Service
public class GaleriaService {

    // Carpeta de imágenes (funciona en desarrollo y dentro del .jar)
    private static final String CARPETA = "classpath*:static/img/*";

    // Extensiones que se muestran en la galería
    private static final List<String> EXTENSIONES =
            List.of(".png", ".jpg", ".jpeg", ".gif", ".webp");

    // Devuelve rutas como "/img/logo.png", ordenadas alfabéticamente
    public List<String> listar() {

        ResourcePatternResolver resolvedor = new PathMatchingResourcePatternResolver();

        try {
            // LinkedHashSet: si la carpeta aparece dos veces en el classpath, no se repite
            Set<String> rutas = new LinkedHashSet<>();

            for (Resource recurso : resolvedor.getResources(CARPETA)) {
                String nombre = recurso.getFilename();
                if (nombre == null) {
                    continue;
                }
                String enMinusculas = nombre.toLowerCase();
                boolean esImagen = EXTENSIONES.stream().anyMatch(enMinusculas::endsWith);
                if (esImagen) {
                    rutas.add("/img/" + nombre);
                }
            }

            List<String> lista = new ArrayList<>(rutas);
            Collections.sort(lista);
            return lista;

        } catch (IOException e) {
            // Sin imágenes la galería simplemente no se muestra
            return List.of();
        }
    }
}
