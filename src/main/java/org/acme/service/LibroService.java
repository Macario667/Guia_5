package org.acme.service;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.model.Libro;
import org.acme.model.LibroInput;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class LibroService {

    private final Map<Long, Libro> libros = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong(0);

    public LibroService() {
        // Datos iniciales
        crear(new LibroInput("Cien años de soledad", "García Márquez", "978-0307474728", 25.50));
        crear(new LibroInput("1984", "George Orwell", "978-0451524935", 15.00));
        crear(new LibroInput("El Quijote", "Cervantes", "978-8437604572", 30.00));
    }

    public List<Libro> listar() {
        return List.copyOf(libros.values());
    }

    public Optional<Libro> buscarPorId(Long id) {
        return Optional.ofNullable(libros.get(id));
    }

    public List<Libro> buscarPorAutor(String autor) {
        return libros.values().stream()
                .filter(l -> l.autor.toLowerCase().contains(autor.toLowerCase()))
                .toList();
    }

    public Libro crear(LibroInput input) {
        Long id = ids.incrementAndGet();
        Libro libro = new Libro(id, input.titulo, input.autor, input.isbn, input.precioCompra);
        libros.put(id, libro);
        return libro;
    }

    public Optional<Libro> actualizar(Long id, LibroInput input) {
        return buscarPorId(id).map(existente -> {
            existente.titulo = input.titulo;
            existente.autor = input.autor;
            existente.isbn = input.isbn;
            existente.precioCompra = input.precioCompra;
            return existente;
        });
    }

    public boolean eliminar(Long id) {
        return libros.remove(id) != null;
    }

    /**
     * Método reactivo (simula una llamada lenta a DB)
     * @param id
     * @return
     */
    public Uni<Libro> buscarAsync(Long id) {
        return Uni.createFrom().item(() -> buscarPorId(id).orElseThrow())
                .onItem().delayIt().by(java.time.Duration.ofMillis(500));
    }
}