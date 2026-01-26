package com.example.demo.Security.Model.Enums;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum Rol {
    cliente("cliente", "Usuario con acceso limitado"),
    administrador("administrador", "Usuario con acceso total");

    private final String nombre;
    private final String descripcion;

    Rol(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public boolean esAdministrador(){
        return this == administrador;
    }
}
