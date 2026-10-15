package org.acme.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.acme.exception.LibroNoEncontradoException;
import org.acme.model.Libro;
import org.acme.model.LibroInput;
import org.acme.model.Views;
import org.acme.service.LibroService;
import org.jboss.resteasy.reactive.*;

import com.fasterxml.jackson.annotation.JsonView;
import io.quarkus.resteasy.reactive.jackson.EnableSecureSerialization;

import java.net.URI;
import java.util.List;

@Path("/api/libros")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@EnableSecureSerialization  // activa @SecureField
public class LibroResource {

    @Inject
    LibroService service;

    /**
     * GET /api/libros
     * @param offset
     * @param limit
     * @return lista de libros (vista pública)
     */
    @GET
    @JsonView(Views.Public.class)
    @Cache(maxAge = 60)  // Cache-Control: max-age=60
    public RestResponse<List<Libro>> listar(
            @RestQuery @DefaultValue("0") int offset,
            @RestQuery @DefaultValue("20") int limit) {

        List<Libro> todos = service.listar();
        List<Libro> pagina = todos.stream()
                .skip(offset)
                .limit(limit)
                .toList();

        return RestResponse.ok(pagina);
    }

    /**
     * GET /api/libros/{id}
     * @param id
     * @return libro (vista pública)
     */
    @GET
    @Path("/{id}")
    @JsonView(Views.Public.class)
    public RestResponse<Libro> buscarPorId(@RestPath Long id) {
        return service.buscarPorId(id)
                .map(RestResponse::ok)
                .orElseGet(() -> RestResponse.notFound());  // ← 404 directo
    }

    /**
     * GET /api/libros/{id}/detalle
     * @param id
     * @return libro con vista INTERNO (incluye ISBN, precio)
     */
    @GET
    @Path("/{id}/detalle")
    @JsonView(Views.Interno.class)
    @NoCache  // Cache-Control: no-cache
    public Libro detalle(@RestPath Long id) {
        return service.buscarPorId(id)
                .orElseThrow(() -> new LibroNoEncontradoException(id));
    }

    /**
     * GET /api/libros/buscar;formato={formato}?autor=...
     * @param autor
     * @param formato
     * @param requestId
     * @param prefs
     * @return lista de libros (vista pública)
     */
    @GET
    @Path("/buscar;formato={formato}")
    public List<Libro> buscar(
            @RestQuery String autor,
            @RestMatrix String formato,
            @RestHeader("X-Request-ID") String requestId,
            @RestCookie("preferencias") String prefs) {

        System.out.println("RequestID: " + requestId + " | Prefs: " + prefs + " | Formato: " + formato);
        return service.buscarPorAutor(autor);
    }

    /**
     * POST /api/libros
     * @param input
     * @return libro creado (201) + Location + header custom
     */
    @POST
    @ResponseStatus(201)
    @ResponseHeader(name = "X-Created-By", value = "LibroResource")
    public RestResponse<Libro> crear(LibroInput input) {
        Libro nuevo = service.crear(input);
        URI location = URI.create("/api/libros/" + nuevo.id);
        return RestResponse.ResponseBuilder.create(Response.Status.CREATED, nuevo)
        .location(location)
        .build();
    }

    /**
     * PUT /api/libros/{id}
     * @param id
     * @param input
     * @return libro actualizado (200)
     */
    @PUT
    @Path("/{id}")
    @JsonView(Views.Interno.class)
    public RestResponse<Libro> actualizar(@RestPath Long id, LibroInput input) {
        return service.actualizar(id, input)
                .map(RestResponse::ok)
                .orElseThrow(() -> new LibroNoEncontradoException(id));
    }

    /**
     * DELETE /api/libros/{id}
     * @param id
     * @return 204 si se borró, 404 si no existía
     */
    @DELETE
    @Path("/{id}")
    public RestResponse<Void> eliminar(@RestPath Long id) {
        if (service.eliminar(id)) {
            return RestResponse.noContent();  // 204
        }
        return RestResponse.notFound();  // 404
    }

    /**
     * POST /api/libros/{id}/acciones/crear
     * @param id
     * @param input
     * @param uriInfo
     * @return 303 redireccionando a la nueva URL
     */
    @POST
    @Path("/{id}/acciones/crear")
    public RestResponse<Void> crearConRedirect(@RestPath Long id, LibroInput input, @Context UriInfo uriInfo) {
        Libro nuevo = service.crear(input);
        return RestResponse.seeOther(
                uriInfo.getAbsolutePathBuilder().path(Long.toString(nuevo.id)).build()
        );
    }

    /**
     * Parámetros agrupados con record (Java 17+)
     * FiltroParams
     * @param autor
     * @param editorial
     * @param idioma
     */
    public record FiltroParams(
            @RestQuery String autor,
            @RestQuery String editorial,
            @RestHeader("X-Idioma") @DefaultValue("es") String idioma
    ) {}

    @GET
    @Path("/filtrar")
    public List<Libro> filtrar(@BeanParam FiltroParams filtro) {
        return service.buscarPorAutor(filtro.autor());
    }
}