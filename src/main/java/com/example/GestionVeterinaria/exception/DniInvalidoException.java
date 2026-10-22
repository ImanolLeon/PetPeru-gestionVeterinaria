package com.example.GestionVeterinaria.exception;

/**
 * Se lanza cuando el DNI de un cliente no sirve:
 * viene vacío o ya está registrado en otro cliente.
 * El controlador la captura para avisar en el formulario en lugar de
 * devolver un error 500.
 */
public class DniInvalidoException extends RuntimeException {

    public DniInvalidoException(String mensaje) {
        super(mensaje);
    }
}
