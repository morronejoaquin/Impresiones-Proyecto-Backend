package com.example.demo.Services;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserCredentials;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Collections;

@Service
public class GoogleDriveService {
    private static final String APPLICATION_NAME = "Impresiones Backend";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private Drive drive;

    @Value("${google.drive.folder-id}")
    private String folderId;

    // Nuevos valores para OAuth2 Personal
    @Value("${google.drive.client-id}")
    private String clientId;

    @Value("${google.drive.client-secret}")
    private String clientSecret;

    @Value("${google.drive.refresh-token}")
    private String refreshToken;

    @PostConstruct
    public void init() {
        try {
            // Configuramos las credenciales usando el Refresh Token de tu cuenta @gmail.com
            GoogleCredentials credentials = UserCredentials.newBuilder()
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setRefreshToken(refreshToken)
                    .build();

            drive = new Drive.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    new HttpCredentialsAdapter(credentials)
            )
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            
            System.out.println("✅ GoogleDriveService conectado exitosamente a cuenta personal.");

        } catch (Exception e) {
            System.err.println("⚠️ Error inicializando GoogleDriveService con OAuth2: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String uploadFile(String fileName, InputStream fileStream, String contentType) throws Exception {
        if (drive == null) {
            System.out.println("⚠️ GoogleDrive deshabilitado.");
            return "mock-drive-id-" + System.currentTimeMillis();
        }

        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        fileMetadata.setParents(Collections.singletonList(folderId));

        InputStreamContent mediaContent = new InputStreamContent(contentType, fileStream);

        // Al subir como 'tú', el archivo usará tus 15GB automáticamente
        File file = drive.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();

        return file.getId();
    }

    public InputStream descargarArchivo(String driveFileId) throws Exception {
        if (drive == null) {
            throw new IllegalStateException("GoogleDriveService no está inicializado");
        }

        if (driveFileId == null || driveFileId.isBlank()) {
            throw new IllegalArgumentException("ID del archivo inválido");
        }

        return drive.files().get(driveFileId)
                .executeMediaAsInputStream();
    }

    public void deleteFile(String driveFileId) {
        if (drive == null) {
            System.out.println("⚠️ GoogleDrive deshabilitado. Ignorando eliminación de: " + driveFileId);
            return;
        }
        try {
            if (driveFileId != null && !driveFileId.isEmpty()) {
                drive.files().delete(driveFileId).execute();
                System.out.println("Archivo eliminado de Drive: " + driveFileId);
            }
        } catch (Exception e) {
            // Logueamos el error pero no frenamos el proceso
            System.err.println("Error al eliminar archivo " + driveFileId + ": " + e.getMessage());
        }
    }
}
