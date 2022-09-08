package me.ingjedagobethm.springboot.webflux.client.app.handler;

import lombok.RequiredArgsConstructor;
import me.ingjedagobethm.springboot.webflux.client.app.models.Producto;
import me.ingjedagobethm.springboot.webflux.client.app.services.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class ProductoHandler {

    private final ProductoService productoService;

    public Mono<ServerResponse> execFindAll(ServerRequest request){
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productoService.findAll(), Producto.class);
    }

    public Mono<ServerResponse> execFindById(ServerRequest request){
        return manejoDeErrores(
                productoService.findById(request.pathVariable("id"))
                .flatMap(p -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(p))
                .switchIfEmpty(ServerResponse.notFound().build())
        );
    }

    public Mono<ServerResponse> execSave(ServerRequest request){
        Mono<Producto> producto = request.bodyToMono(Producto.class);
        return producto
                .flatMap(p -> {
                    if (p.getCreateAt() == null){
                        p.setCreateAt(new Date());
                    }
                    return productoService.save(p);
                })
                .flatMap(p -> ServerResponse
                        .created(URI.create("/api/client/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(p)
                        .onErrorResume(error -> {
                            WebClientResponseException errorResponse = (WebClientResponseException) error;
                            if (errorResponse.getStatusCode() == HttpStatus.BAD_REQUEST){
                                return ServerResponse
                                        .badRequest()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(errorResponse.getResponseBodyAsString());
                            }
                            return Mono.error(errorResponse);
                        }));
    }

    public Mono<ServerResponse> execUpdate(ServerRequest request){
        return request.bodyToMono(Producto.class)
                .flatMap(p -> productoService.update(p, request.pathVariable("id")))
                .flatMap(p -> ServerResponse
                        .created(URI.create("/api/client/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(p)))
                .onErrorResume(error -> {
                    WebClientResponseException errorResponse = (WebClientResponseException) error;
                    if (errorResponse.getStatusCode() == HttpStatus.NOT_FOUND){
                        return ServerResponse.notFound().build();
                    }
                    return Mono.error(errorResponse);
                })
                ;
    }

    public Mono<ServerResponse> execDelete(ServerRequest request){
        String id = request.pathVariable("id");
        return manejoDeErrores(
                productoService.delete(id)
                        .then(ServerResponse.noContent().build())
        );
    }

    public Mono<ServerResponse> execUpload(ServerRequest request){
        String id = request.pathVariable("id");
        return manejoDeErrores(
                request.multipartData()
                        .map(multipart -> multipart.toSingleValueMap().get("file"))
                        .cast(FilePart.class)
                        .flatMap(filePart -> productoService.upload(filePart, id))
                        .flatMap(producto -> ServerResponse
                                .created(URI.create("/api/client/".concat(producto.getId())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(producto))
        );
    }

    private Mono<ServerResponse> manejoDeErrores(Mono<ServerResponse> response){
        return response
                .onErrorResume(error -> {
                    WebClientResponseException errorResponse = (WebClientResponseException) error;
                    if (errorResponse.getStatusCode() == HttpStatus.NOT_FOUND){
                        // Para anexar un cuerpo (JSON) a la respuesta
                        Map<String, Object> body = new HashMap<>();
                        body.put("error", "No existe el producto: ".concat(Objects.requireNonNull(errorResponse.getMessage())));
                        body.put("timestamp", new Date());
                        body.put("status", errorResponse.getStatusCode().value());
                        // return ServerResponse.notFound().build(); // Respuesta sin cuerpo
                        return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(body);
            }
            return Mono.error(errorResponse);
        });
    }

}
