package com.example.demo.Model.Enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Errores de Carrito
    CART_NOT_FOUND("CART_001", "El carrito no existe", HttpStatus.NOT_FOUND),
    CART_IS_CLOSED("CART_002", "El carrito ya no permite modificaciones", HttpStatus.BAD_REQUEST),
    CART_IS_EMPTY("CART_003", "El carrito esta vacio", HttpStatus.BAD_REQUEST),
    ORDER_ALREADY_PROCESS("CART_004", "No se puede cancelar un pedido ya en proceso", HttpStatus.BAD_REQUEST),
    ORDER_ALREADY_DELIVERED("CART_005", "No se puede modificar un carrito entregado o cancelado", HttpStatus.BAD_REQUEST),
    CART_LIMIT_REACHED("CART_006", "Has alcanzado el límite máximo de items", HttpStatus.BAD_REQUEST),
    USER_HAS_OPEN_CART("CART_007", "El usuario ya tiene un carrito activo", HttpStatus.CONFLICT),

    // Errores de Item
    ITEM_NOT_FOUND("ITEM_001", "El item no existe o no pertenece al carrito", HttpStatus.NOT_FOUND),

    // Errores de Archivo
    FILE_NOT_ALLOWED("FILE_001", "Formato de archivo no permitido", HttpStatus.BAD_REQUEST),
    CORRUPTED_FILE("FILE_002", "El archivo esta corrupto", HttpStatus.BAD_REQUEST),
    CLOUD_UPLOAD_ERROR("FILE_003", "Ha ocurrido un error al intentar subir el archivo a la nube", HttpStatus.BAD_REQUEST),

    // Errores de pagos
    PAYMENT_NOT_FOUND("PAYMENT_001", "El pago no existe", HttpStatus.NOT_FOUND),
    PAYMENT_NOT_ALLOWED("PAYMENT_002", "Metodo de pago no soportado", HttpStatus.BAD_REQUEST),

    // Errores de precios
    PRICES_NOT_FOUND("PRICES_001", "Precios no encontrados", HttpStatus.NOT_FOUND),
    PRICES_NOT_CONFIGURED("PRICES_002", "No hay precios vigentes configurados", HttpStatus.NOT_FOUND),

    // Errores de ubicacion
    LOCATION_NOT_CONFIGURED("LOCATION_001", "No hay una ubicación configurada", HttpStatus.NOT_FOUND),

    // Errores de Usuario / Seguridad
    USER_NOT_FOUND("USER_001", "Usuario no encontrado", HttpStatus.NOT_FOUND),
    ACCESS_DENIED("AUTH_001", "No tienes permisos para esta acción", HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS("AUTH_002", "Email o contraseña incorrectos", HttpStatus.UNAUTHORIZED),
    CREDENTIALS_NOT_FOUND("AUTH_003", "Credenciales del usuaio no encontradas", HttpStatus.NOT_FOUND);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus status;

    ErrorCode(String code, String defaultMessage, HttpStatus status) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }
}
