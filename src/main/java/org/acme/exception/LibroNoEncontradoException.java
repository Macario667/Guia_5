package org.acme.exception;

public class LibroNoEncontradoException extends RuntimeException {

    public LibroNoEncontradoException(Long id) {
        super("Libro con id " + id + " no encontrado");
    }
}
