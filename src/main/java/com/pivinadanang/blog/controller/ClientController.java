package com.pivinadanang.blog.controller;

import com.pivinadanang.blog.components.converters.LocalizationUtils;
import com.pivinadanang.blog.dtos.ClientDTO;
import com.pivinadanang.blog.dtos.GoogleDriveDTO;
import com.pivinadanang.blog.models.ClientEntity;
import com.pivinadanang.blog.responses.ClientResponse;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.services.client.ClientService;
import com.pivinadanang.blog.services.google.IGoogleService;
import com.pivinadanang.blog.ultils.FileUtils;
import com.pivinadanang.blog.ultils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping("api/v1/clients")
@Validated
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;
    private final LocalizationUtils localizationUtils;
    private final IGoogleService googleService;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllClients() {
        List<ClientResponse> clients = clientService.getAllClients();
        return ResponseEntity.ok(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_CLIENT_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(clients)
                .build());
    }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> insertClient(@Valid @ModelAttribute ClientDTO clientDTO,
                                                       @RequestParam (value = "logoFile", required = false) MultipartFile file,
                                                         BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(errorMessages.toString())
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());

        }
        if (clientService.exitsByName(clientDTO.getName())) {
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.CLIENT_ALREADY_EXISTS))
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }
        // Kiểm tra kích thước file và định dạng
        if (file.getSize() > 5 * 1024 * 1024) { // Kích thước > 5MB
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(ResponseObject.builder()
                            .message(localizationUtils
                                    .getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_LARGE))
                            .status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .build());
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(ResponseObject.builder()
                            .message(localizationUtils
                                    .getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE))
                            .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .build());
        }
        try {
            File tempFile = FileUtils.handleFile(file);
            GoogleDriveDTO googleDriveDTO = googleService.uploadImageToDrive(tempFile);
            clientDTO.setLogo(googleDriveDTO.getUrl());
            clientDTO.setFileId(googleDriveDTO.getFileId());
            ClientResponse client = clientService.createClient(clientDTO);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_CLIENT_SUCCESSFULLY))
                    .status(HttpStatus.OK)
                    .data(client)
                    .build());
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_CLIENT_FAILED))
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build());
        }
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> updateClient(@Valid @ModelAttribute ClientDTO clientDTO,
                                                       @PathVariable  Long id,
                                                       @RequestParam (value = "logoFile", required = false) MultipartFile file,
                                                       BindingResult result) throws GeneralSecurityException, IOException {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(errorMessages.toString())
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());

        }
        if (clientService.exitsByName(clientDTO.getName())) {
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.CLIENT_ALREADY_EXISTS))
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }
        // Kiểm tra kích thước file và định dạng
        String oldFileId = null;
        String newFileId = null;
        try {
            if(file != null && !file.isEmpty()){
                ClientEntity existingClient = clientService.findById(id);
                oldFileId = existingClient.getFileId(); // Lưu lại fileId cũ để xóa sau
                if (file.getSize() > 5 * 1024 * 1024) { // Kích thước > 5MB
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body(ResponseObject.builder()
                                    .message(localizationUtils
                                            .getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_LARGE))
                                    .status(HttpStatus.PAYLOAD_TOO_LARGE)
                                    .build());
                }
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body(ResponseObject.builder()
                                    .message(localizationUtils
                                            .getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE))
                                    .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                                    .build());
                }

                File tempFile = FileUtils.handleFile(file);
                GoogleDriveDTO googleDriveDTO = googleService.uploadImageToDrive(tempFile);
                clientDTO.setLogo(googleDriveDTO.getUrl());
                clientDTO.setFileId(googleDriveDTO.getFileId());
                newFileId = googleDriveDTO.getFileId(); // Lưu lại fileId mới để xóa trong trường hợp lỗi


            }
            ClientResponse client = clientService.updateClient(id,clientDTO);
            // Xóa logo cũ khỏi Google Drive nếu update thành công và có logo cũ
            if (oldFileId != null) {
                googleService.deleteFileFromDrive(oldFileId);
            }
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_CLIENT_SUCCESSFULLY))
                    .status(HttpStatus.OK)
                    .data(client)
                    .build());

        } catch (Exception exception) {
            // Xóa logo mới khỏi Google Drive nếu có lỗi xảy ra sau khi upload file
            if (newFileId != null) {
                googleService.deleteFileFromDrive(newFileId);
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_CLIENT_FAILED))
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteClient(@PathVariable Long id) {
        try {
             clientService.deleteClients(id);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_CLIENT_SUCCESSFULLY))
                    .status(HttpStatus.OK)
                    .data(null)
                    .build());
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_CLIENT_FAILED))
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build());
        }
    }
}
