package org.acme.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;

import org.jboss.resteasy.reactive.RestStreamElementType;

import io.smallrye.mutiny.Multi;

import java.time.Duration;

@Path("/api/sse")
public class SseResource {

    @Inject
    Sse sse;

    /**
     * GET /api/sse/tiempo
     * @return Cada 2 segundos envía un evento con la hora actual
     */
    @GET
    @Path("/tiempo")
    @RestStreamElementType(MediaType.TEXT_PLAIN)
    public Multi<OutboundSseEvent> tiempo() {
        return Multi.createFrom()
                .ticks().every(Duration.ofSeconds(2))
                .onItem().transform(tick ->
                        sse.newEventBuilder()
                                .name("tick")
                                .data("Tick número: " + tick)
                                .build()
                );
    }

    /**
     * GET /api/sse/libros
     * @return Cada 3 segundos emite un JSON
     */
    @GET
    @Path("/libros")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<String> libros() {
        return Multi.createFrom()
                .ticks().every(Duration.ofSeconds(3))
                .onItem().transform(tick -> "{\"evento\":\"tick\",\"numero\":" + tick + "}");
    }
}