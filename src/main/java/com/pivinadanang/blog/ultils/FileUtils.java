package com.pivinadanang.blog.ultils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class FileUtils {

    public static File handleFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new IOException("Filename is invalid");
        }

        File tempFile = new File(System.getProperty("java.io.tmpdir"), fileName);
        file.transferTo(tempFile);

        if (!isImageFile(tempFile)) {
            tempFile.delete(); // Delete the file if it's not an image
            throw new IOException("File is not a valid image");
        }
        return tempFile;
    }
    private static boolean isImageFile(File file) throws IOException {
        String mimeType = java.nio.file.Files.probeContentType(file.toPath());
        return mimeType != null && mimeType.startsWith("image");
    }

}
