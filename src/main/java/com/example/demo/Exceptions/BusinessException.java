package com.example.demo.Exceptions;

import com.example.demo.Model.Enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus status;
    private final String codigo;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.status = errorCode.getStatus();
        this.codigo = errorCode.getCode();
    }

    // Constructor opcional para personalizar el mensaje pero mantener el código
    public BusinessException(ErrorCode errorCode, String customMessage) {
      super(customMessage);
      this.codigo = errorCode.getCode();
      this.status = errorCode.getStatus();
    }
}
