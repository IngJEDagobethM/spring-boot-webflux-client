package me.ingjedagobethm.springboot.webflux.client.app.services;

import lombok.RequiredArgsConstructor;
import me.ingjedagobethm.springboot.webflux.client.app.models.Producto;
import me.ingjedagobethm.springboot.webflux.client.app.repository.ProductoRepository;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ProductoService implements ProductoRepository {

    //private final WebClient client;
    private final WebClient.Builder client;

    @Override
    public Flux<Producto> findAll() {
        return client.build().get()
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToFlux(clientResponse -> clientResponse.bodyToFlux(Producto.class));
    }

    @Override
    public Mono<Producto> findById(String id) {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("id", id);
        return client.build().get().uri("/{id}", requestParams)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Producto.class);
    }

    @Override
    public Mono<Producto> save(Producto producto) {
        return client.build().post()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                // Se puede importar estáticamente el BodyInserters
                .body(BodyInserters.fromValue(producto))
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(Producto.class));
    }

    @Override
    public Mono<Producto> update(Producto producto, String id) {
        return client.build().put()
                .uri("/{id}", Collections.singletonMap("id", id))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                // Se puede importar estáticamente el BodyInserters
                .body(BodyInserters.fromValue(producto))
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(Producto.class));
    }

    @Override
    public Mono<Void> delete(String id) {
        return client.build().delete()
                .uri("/{id}", Collections.singletonMap("id", id))
                //.exchangeToMono(ClientResponse::releaseBody)
                .retrieve()
                .bodyToMono(Void.class)
        ;
    }

    @Override
    public Mono<Producto> upload(FilePart file, String id) {

        MultipartBodyBuilder parts = new MultipartBodyBuilder();
        parts.asyncPart("file", file.content(), DataBuffer.class)
                .headers(h -> {
                    h.setContentDispositionFormData("file", file.filename());
                });

        return client.build().post()
                .uri("/upload/{id}", Collections.singletonMap("id", id))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(parts.build())
                .retrieve()
                .bodyToMono(Producto.class);
    }
}
