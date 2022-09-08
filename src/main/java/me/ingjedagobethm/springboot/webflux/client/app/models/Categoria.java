package me.ingjedagobethm.springboot.webflux.client.app.models;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class Categoria {
    private String id;
    private String nombre;
}
