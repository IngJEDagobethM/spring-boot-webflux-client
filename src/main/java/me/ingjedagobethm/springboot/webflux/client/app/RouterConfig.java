package me.ingjedagobethm.springboot.webflux.client.app;

import lombok.RequiredArgsConstructor;
import me.ingjedagobethm.springboot.webflux.client.app.handler.ProductoHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@RequiredArgsConstructor
@Configuration
public class RouterConfig {

    private final ProductoHandler productoHandler;

    @Bean
    public RouterFunction<ServerResponse> controladorUrl(){
        return RouterFunctions
                .route(RequestPredicates.GET("/api/client"), productoHandler::execFindAll)
                .andRoute(RequestPredicates.GET("/api/client/{id}"), productoHandler::execFindById)
                .andRoute(RequestPredicates.POST("/api/client"), productoHandler::execSave)
                .andRoute(RequestPredicates.PUT("/api/client/{id}"), productoHandler::execUpdate)
                .andRoute(RequestPredicates.DELETE("/api/client/{id}"), productoHandler::execDelete)
                .andRoute(RequestPredicates.POST("/api/client/upload/{id}"), productoHandler::execUpload)
                ;
    }
}
