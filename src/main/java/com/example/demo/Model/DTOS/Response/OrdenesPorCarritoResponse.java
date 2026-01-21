package com.example.demo.Model.DTOS.Response;

import com.example.demo.Model.Enums.BindingTypeEnum;
import com.example.demo.Model.Enums.FileTypeEnum;
import lombok.Data;

import java.util.UUID;

@Data
public class OrdenesPorCarritoResponse {
    private UUID id;
    private String nombreArchivo;
    private FileTypeEnum tipoArchivo;
    private int cantidadPaginas;
    private int cantidadCopias;
    private boolean color;
    private boolean doubleSided;
    private BindingTypeEnum tipoEncuadernado;
    private String comentarios;
}
