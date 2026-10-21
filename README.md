# quarkus-rest-demo

API de ejemplo para practicar recursos REST, JSON, operaciones CRUD, vistas de serialización, búsqueda y respuestas reactivas. Los libros se almacenan en memoria: al reiniciar la aplicación se pierden.

## Guías adaptadas

- [Writing REST Services with RESTEasy Reactive](https://quarkus.io/guides/rest): los recursos implementan rutas HTTP con Quarkus REST (antes llamado RESTEasy Reactive).
- [Writing JSON REST Services](https://quarkus.io/guides/rest-json): `quarkus-rest-jackson` y las anotaciones JSON permiten recibir y producir representaciones JSON.
- [Conocimientos JSON](https://www.nylas.com/blog/the-complete-guide-to-working-with-json/): apoyo conceptual para comprender los cuerpos JSON que se intercambian con la API; no es una extensión instalada en el proyecto.
- [Validation with Hibernate Validator](https://quarkus.io/guides/validation): el proyecto incluye `quarkus-hibernate-validator` y `LibroInput` declara `@NotBlank`.

La guía de [RESTEasy Classic](https://es.quarkus.io/guides/resteasy) describe otra implementación. No es la usada aquí: las dependencias son `quarkus-rest-jackson` y `quarkus-rest`.

## Cómo se aplica

- `LibroResource` ofrece endpoints bajo `/api/libros` para listar, buscar por id, crear, actualizar y eliminar.
- `LibroInput` representa los datos recibidos en JSON; `Libro` es el modelo de respuesta.
- `Views.Public` y `Views.Interno` junto con `@JsonView` muestran cómo variar los campos serializados.
- `BusquedaResource` devuelve `Uni<Libro>` para una respuesta reactiva y muestra el uso de `@Blocking`.
- `StreamResource` y `SseResource` contienen ejemplos de transmisión JSON y SSE.
- `LibroNoEncontradoMapper` transforma la excepción de libro inexistente en una respuesta HTTP.

## Ejecutar

Requiere Java 21 según `maven.compiler.release` en `pom.xml`. Este proyecto no incluye Maven Wrapper:

```powershell
mvn quarkus:dev
```

## Ejemplos HTTP

Listar libros:

```powershell
curl.exe http://localhost:8080/api/libros
```

Crear un libro:

```powershell
curl.exe -i -X POST http://localhost:8080/api/libros `
  -H "Content-Type: application/json" `
  -d '{"titulo":"Quarkus práctico","autor":"Ana Pérez","isbn":"9780000000001","precioCompra":24.5}'
```

Obtener un libro y sus detalles:

```powershell
curl.exe http://localhost:8080/api/libros/1
curl.exe http://localhost:8080/api/libros/1/detalle
```

El detalle puede incluir campos distintos a la vista pública. Prueba también `PUT` y `DELETE` en `/api/libros/{id}`.

## Ejercicio de validación

`LibroInput` tiene restricciones `@NotBlank`, pero el parámetro del método `crear` actualmente no lleva `@Valid`; por ello, la validación automática de ese cuerpo no se activa en este endpoint. Para practicar la guía:

1. Añade `@Valid` al parámetro `LibroInput` del método `crear` en `LibroResource`.
2. Envía el JSON válido anterior y confirma que sigue creándose el libro.
3. Envía un cuerpo con `titulo` vacío; comprueba la respuesta de validación.
4. Añade una prueba RestAssured para los dos casos.

## Pruebas

```powershell
mvn test
```
