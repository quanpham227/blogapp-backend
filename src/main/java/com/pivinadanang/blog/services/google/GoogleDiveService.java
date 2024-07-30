package com.pivinadanang.blog.services.google;

import com.pivinadanang.blog.dtos.GoogleDriveDTO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;


import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Collections;
@Service
public class GoogleDiveService implements IGoogleService{
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${google.drive.credentials}")
    private Resource credentialsResource;
    private static String getPathToGoodleCredentials() {
        String currentDirectory = System.getProperty("user.dir");
        Path filePath = Paths.get(currentDirectory, "google_credentials.json");
        return filePath.toString();
    }
    @Override
    public GoogleDriveDTO uploadImageToDrive(File file) throws GeneralSecurityException, IOException {
        GoogleDriveDTO googleDriveDTO = new GoogleDriveDTO();

        try {
            String folderId = "1QoqlIPH2VJzazmVl0A0f5Y8FXXtn8j_7";
            Drive drive = createDriveService();
            com.google.api.services.drive.model.File fileMetaData = new com.google.api.services.drive.model.File();
            fileMetaData.setName(file.getName());
            fileMetaData.setParents(Collections.singletonList(folderId));
            FileContent mediaContent = new FileContent("image/jpeg", file);
            com.google.api.services.drive.model.File uploadedFile = drive.files().create(fileMetaData, mediaContent)
                    .setFields("id").execute();
            String fileId = uploadedFile.getId();
            String imageUrl = "https://drive.google.com/uc?export=view&id=" + uploadedFile.getId();
            System.out.println("IMAGE URL: " + imageUrl);
            file.delete();

            return googleDriveDTO.builder()
                    .url(imageUrl)
                    .fileId(fileId)
                    .build();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to upload image to Google Drive", exception);
        }
    }

    @Override
    public void deleteFileFromDrive(String fileId) throws IOException, GeneralSecurityException {
        Drive drive = createDriveService();
        drive.files().delete(fileId).execute();
    }
    private Drive createDriveService() throws GeneralSecurityException, IOException {
        InputStream inputStream = credentialsResource.getInputStream();
        GoogleCredential credential = GoogleCredential.fromStream(inputStream)
                .createScoped(Collections.singleton(DriveScopes.DRIVE));

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential)
                .build();
    }
}