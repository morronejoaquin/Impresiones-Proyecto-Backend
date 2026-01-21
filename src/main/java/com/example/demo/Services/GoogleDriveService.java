package com.example.demo.Services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class GoogleDriveService {
    private static final String APPLICATION_NAME = "Impresiones Backend";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = List.of(DriveScopes.DRIVE_FILE);

    private Drive drive;

    @Value("${google.drive.folder-id}")
    private String folderId;

    @PostConstruct
    public void init() {
        try {
            InputStream in = getClass().getResourceAsStream("/credentials.json");

            if (in == null) {
                System.out.println("⚠️ Advertencia: credentials.json no encontrado. GoogleDriveService deshabilitado.");
                return;
            }

            GoogleClientSecrets clientSecrets =
                    GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            GoogleAuthorizationCodeFlow flow =
                    new GoogleAuthorizationCodeFlow.Builder(
                            GoogleNetHttpTransport.newTrustedTransport(),
                            JSON_FACTORY,
                            clientSecrets,
                            SCOPES
                    )
                            .setDataStoreFactory(new FileDataStoreFactory(
                                    new java.io.File("tokens")))
                            .setAccessType("offline")
                            .build();

            LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                    .setPort(8888)
                    .build();

            Credential credential =
                    new AuthorizationCodeInstalledApp(flow, receiver)
                            .authorize("user");

            drive = new Drive.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    credential
            )
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (Exception e) {
            System.out.println("⚠️ Error inicializando GoogleDriveService: " + e.getMessage());
            System.out.println("   La funcionalidad de Google Drive estará deshabilitada.");
        }
    }

    public String uploadFile(String fileName, InputStream fileStream, String contentType) throws Exception {

        if (folderId == null || folderId.isBlank()) {
            throw new IllegalStateException("google.drive.folder-id no está configurado");
        }

        if (fileName == null || contentType == null) {
            throw new IllegalArgumentException("Archivo inválido");
        }

        File fileMetadata = new File();
        fileMetadata.setName(fileName);

        fileMetadata.setParents(List.of(folderId));

        InputStreamContent mediaContent =
                new InputStreamContent(contentType, fileStream);

        File file = drive.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();

        return file.getId(); // ⬅️ ESTE ES EL driveFileId
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
}
