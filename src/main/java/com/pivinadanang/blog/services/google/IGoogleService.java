package com.pivinadanang.blog.services.google;

import com.pivinadanang.blog.dtos.GoogleDriveDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

public interface IGoogleService {
    GoogleDriveDTO uploadImageToDrive(File file) throws GeneralSecurityException, IOException;
    void deleteFileFromDrive(String fileId) throws IOException, GeneralSecurityException;
}
