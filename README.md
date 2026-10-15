# quarkus-rest-demo

Demo de Quarkus REST con CRUD de libros en memoria, búsqueda, transmisión JSON y eventos SSE.

## Ejecutar

Requiere Java 21 o posterior y Maven:

```powershell
mvn quarkus:dev
```

## Endpoints

- `GET /libros`: lista libros en vista resumida.
- `GET /libros/{id}`: obtiene un libro con su detalle.
- `POST /libros`: crea un libro; requiere `titulo`, `autor` y `anioPublicacion`.
- `PUT /libros/{id}`: reemplaza los datos de un libro.
- `DELETE /libros/{id}`: elimina un libro.
- `GET /busqueda/libros?q={texto}`: busca por título o autor.
- `GET /stream/libros`: transmite libros como elementos JSON.
- `GET /eventos`: emite eventos SSE cada cinco segundos.

Los datos se guardan en memoria y se reinician al detener la aplicación.
