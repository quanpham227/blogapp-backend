package com.pivinadanang.blog.ultils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    public static File handleFile(MultipartFile file) throws IOException {

        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new IOException("Filename is invalid");
        }

        File tempFile = new File(System.getProperty("java.io.tmpdir"), fileName);
        try (InputStream inputStream = file.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            if (!isImageFile(tempFile)) {
                throw new IOException("File is not a valid image");
            }
        } finally {
            // Xóa tệp tin tạm sau khi xử lý
            if (tempFile.exists() && !tempFile.delete()) {
                System.err.println("Failed to delete temporary file: " + tempFile.getAbsolutePath());
            }
        }
        return tempFile;
    }

    private static boolean isImageFile(File file) throws IOException {
        String mimeType = java.nio.file.Files.probeContentType(file.toPath());
        return mimeType != null && mimeType.startsWith("image");
    }

}
