package com.example.demo.Security.Model.Enums;

public enum Permits {

    // Permisos de usuario cliente y administrador
    VER_CUENTA,
    CREAR_CUENTA,
    MODIFICAR_CUENTA,
    ELIMINAR_CUENTA,

    // Permisos de usuario cliente
    CREAR_CARRITO,
    CARGAR_PEDIDO,
    MODIFICAR_PEDIDO,
    ELIMINAR_PEDIDO,
    VER_MI_CARRITO,
    PAGAR_CARRITO,
    VER_MIS_CARRITOS,
    CANCELAR_PEDIDO,

    // Permisos de Administrador
    VER_TODOS_CARRITOS,
    VER_CARRITO_CLIENTE,
    MODIFICAR_ESTADO_PEDIDO,
    VER_TODOS_USUARIOS,
    VER_USUARIO_CLIENTE,
    MODIFICAR_USUARIO_CLIENTE,
    ELIMINAR_USUARIO_CLIENTE,
    MODIFICAR_PRECIOS,
    VER_ESTADISTICAS,
    VER_PAGOS,
    ACTUALIZAR_PAGO,

}
