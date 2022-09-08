package me.ingjedagobethm.springboot.webflux.client.app.models;

import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class Producto {
    private String id;
    private String nombre;
    private Double precio;
    private Date createAt;
    private String foto;
    private Categoria categoria;
}
