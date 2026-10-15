package org.acme.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import io.smallrye.mutiny.Uni;
import io.smallrye.common.annotation.Blocking;

import org.acme.model.Libro;
import org.acme.model.Views;
import org.acme.service.LibroService;

import com.fasterxml.jackson.annotation.JsonView;

import org.jboss.resteasy.reactive.RestPath;

@Path("/api/reactivo/libros")
@Produces(MediaType.APPLICATION_JSON)
public class BusquedaResource {

    @Inject
    LibroService service;

    /**
     * GET /api/reactivo/libros/{id}
     * @param id
     * @return libro (vista pública)
     */
    @GET
    @Path("/{id}")
    @JsonView(Views.Public.class)
    public Uni<Libro> buscarAsync(@RestPath Long id) {
        return service.buscarAsync(id);
    }

    /**
     * GET /api/reactivo/libros/{id}/bloqueante
     * @param id
     * @return libro (vista pública)
     */
    @GET
    @Path("/{id}/bloqueante")
    @Blocking
    public Uni<Libro> buscarBloqueante(@RestPath Long id) {
        // Operación bloqueante simulada
        return Uni.createFrom().item(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            return service.buscarPorId(id).orElseThrow();
        });
    }
}