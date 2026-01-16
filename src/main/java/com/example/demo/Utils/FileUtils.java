package com.example.demo.Utils;

import org.apache.pdfbox.pdmodel.PDDocument;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class FileUtils {

    public static FileMetaData obtenerMetadata(InputStream inputStream, String contentType) {
        FileMetaData metadata = new FileMetaData();

        try {
            if (contentType.equals("application/pdf")) {
                try (PDDocument document = PDDocument.load(inputStream)) {
                    metadata.setPages(document.getNumberOfPages());
                }
            }
            else if (contentType.equals("image/jpeg") || contentType.equals("image/png")) {
                BufferedImage image = ImageIO.read(inputStream);
                metadata.setImageWidth(image.getWidth());
                metadata.setImageHeight(image.getHeight());
            }
            else {
                throw new IllegalArgumentException("Formato no soportado");
            }
        } catch (Exception e) {
            throw new RuntimeException("No se pudo procesar el archivo", e);
        }

        return metadata;
    }
}
