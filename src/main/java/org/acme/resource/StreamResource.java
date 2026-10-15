package org.acme.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import org.jboss.resteasy.reactive.RestMulti;

import java.time.Duration;
import java.util.stream.IntStream;

@Path("/api/stream")
public class StreamResource {

    /**
     * GET /api/stream/numeros
     * @return un JSON array (por defecto)
     */
    @GET
    @Path("/numeros")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Integer> numeros() {
        return Multi.createFrom().items(IntStream.rangeClosed(1, 5).boxed())
        .onItem().call(item ->
                Uni.createFrom().voidItem()
                        .onItem().delayIt().by(Duration.ofMillis(200)));
    }

    /**
     * GET /api/stream/numeros-v2
     * @return Personaliza status y headers
     */
    @GET
    @Path("/numeros-v2")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Integer> numerosV2() {
        Multi<Integer> source = Multi.createFrom().items(1, 2, 3, 4, 5);
        return RestMulti.fromMultiData(source)
                .status(222)  // status custom
                .header("X-Stream", "true")
                .build();
    }

    /**
     * GET /api/stream/numeros-json-v2
     * @return Sin envolver en array JSON (objetos uno a uno)
     */
    @GET
    @Path("/numeros-json-v2")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Integer> numerosJsonV2() {
        Multi<Integer> source = Multi.createFrom().items(1, 2, 3, 4, 5);
        return RestMulti.fromMultiData(source)
                .encodeAsJsonArray(false)  // sin array
                .build();
    }

    /**
     * GET /api/stream/concurrente
     * @return 5 elementos concurrentemente (sin orden)
     */
    @GET
    @Path("/concurrente")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<String> concurrente() {
        Multi<String> source = Multi.createBy().merging().streams(
                Multi.createFrom().items("msg-1", "msg-2", "msg-3", "msg-4", "msg-5")
        );

        return RestMulti.fromMultiData(source)
                .withDemand(5)  // concurrencia de 5
                .build();
    }
}