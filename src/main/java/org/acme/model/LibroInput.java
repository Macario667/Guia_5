package org.acme.model;

import jakarta.validation.constraints.NotBlank;

public class LibroInput {
    @NotBlank
    public String titulo;
    @NotBlank
    public String autor;
    public String isbn;
    public Double precioCompra;

    public LibroInput() {}

    public LibroInput(String titulo, String autor, String isbn, Double precioCompra) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.precioCompra = precioCompra;
    }
}