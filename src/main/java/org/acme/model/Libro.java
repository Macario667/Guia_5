package org.acme.model;

import com.fasterxml.jackson.annotation.JsonView;
import io.quarkus.resteasy.reactive.jackson.SecureField;

public class Libro {

    @JsonView(Views.Public.class)
    public Long id;

    @JsonView(Views.Public.class)
    public String titulo;

    @JsonView(Views.Public.class)
    public String autor;

    /**
     * Este campo solo se ve con la vista Interna
     */
    @JsonView(Views.Interno.class)
    public String isbn;

    /**
     * Solo usuarios con rol "admin" ven este campo
     */
    @SecureField(rolesAllowed = "admin")
    public Double precioCompra;

    public Libro() {}  /* OBLIGATORIO para deserialización */

    public Libro(Long id, String titulo, String autor, String isbn, Double precioCompra) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.precioCompra = precioCompra;
    }
}