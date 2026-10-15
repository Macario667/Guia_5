package org.acme.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;

@QuarkusTest
class LibroResourceTest {

    @Test
    void creaBuscaActualizaYEliminaLibros() {
        int id = given()
                .contentType("application/json")
                .body("""
                        {
                          "titulo": "Cien años de soledad",
                          "autor": "Gabriel García Márquez",
                          "anioPublicacion": 1967
                        }
                        """)
                .when()
                .post("/libros")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .get("/libros/{id}", id)
                .then()
                .statusCode(200)
                .body("titulo", equalTo("Cien años de soledad"))
                .body("anioPublicacion", equalTo(1967));

        given()
                .queryParam("q", "gabriel")
                .when()
                .get("/busqueda/libros")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].titulo", equalTo("Cien años de soledad"))
                .body("[0]", not(hasKey("anioPublicacion")));

        given()
                .contentType("application/json")
                .body("""
                        {
                          "titulo": "El amor en los tiempos del cólera",
                          "autor": "Gabriel García Márquez",
                          "anioPublicacion": 1985
                        }
                        """)
                .when()
                .put("/libros/{id}", id)
                .then()
                .statusCode(200)
                .body("titulo", equalTo("El amor en los tiempos del cólera"));

        given()
                .when()
                .delete("/libros/{id}", id)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/libros/{id}", id)
                .then()
                .statusCode(404)
                .body("error", containsString(String.valueOf(id)));
    }

    @Test
    void validaDatosAlCrearLibros() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "titulo": " ",
                          "autor": "Autor",
                          "anioPublicacion": 2020
                        }
                        """)
                .when()
                .post("/libros")
                .then()
                .statusCode(400);
    }

    @Test
    void transmiteLibrosComoJson() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "titulo": "Pedro Páramo",
                          "autor": "Juan Rulfo",
                          "anioPublicacion": 1955
                        }
                        """)
                .when()
                .post("/libros")
                .then()
                .statusCode(201);

        given()
                .accept("application/json")
                .when()
                .get("/stream/libros")
                .then()
                .statusCode(200)
                .body(containsString("Pedro Páramo"));
    }
}
