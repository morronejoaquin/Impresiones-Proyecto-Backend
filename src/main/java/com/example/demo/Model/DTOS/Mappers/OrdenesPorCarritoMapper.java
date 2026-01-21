package com.example.demo.Model.DTOS.Mappers;

import com.example.demo.Model.DTOS.Response.OrdenesPorCarritoResponse;
import com.example.demo.Model.Entities.OrderItemEntity;
import org.springframework.stereotype.Component;

@Component
public class OrdenesPorCarritoMapper {

    public OrdenesPorCarritoResponse toResponse(OrderItemEntity entity) {
        if (entity == null) {
            return null;
        }

        OrdenesPorCarritoResponse response = new OrdenesPorCarritoResponse();
        response.setId(entity.getId());
        response.setNombreArchivo(entity.getFileName());
        response.setTipoArchivo(entity.getFileType());
        response.setCantidadPaginas(entity.getPages());
        response.setCantidadCopias(entity.getCopies());
        response.setColor(entity.isColor());
        response.setDoubleSided(entity.isDoubleSided());
        response.setTipoEncuadernado(entity.getBinding());
        response.setComentarios(entity.getComments());

        return response;
    }
}
