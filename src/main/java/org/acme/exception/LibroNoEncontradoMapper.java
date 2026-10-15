package org.acme.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class LibroNoEncontradoMapper implements ExceptionMapper<LibroNoEncontradoException> {

    @Override
    public Response toResponse(LibroNoEncontradoException e) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(e.getMessage()))
                .build();
    }

    public record ErrorResponse(String mensaje) {}
}