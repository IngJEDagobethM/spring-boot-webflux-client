package me.ingjedagobethm.springboot.webflux.client.app.repository;

import me.ingjedagobethm.springboot.webflux.client.app.models.Producto;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoRepository {
    Flux<Producto> findAll();
    Mono<Producto> findById(String id);
    Mono<Producto> save(Producto producto);
    Mono<Producto> update(Producto producto, String id);
    Mono<Void> delete(String id);
    Mono<Producto> upload(FilePart file, String id);
}
