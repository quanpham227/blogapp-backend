package com.pivinadanang.blog.controller;

import com.pivinadanang.blog.components.converters.LocalizationUtils;
import com.pivinadanang.blog.dtos.ClientDTO;
import com.pivinadanang.blog.dtos.CloudinaryDTO;
import com.pivinadanang.blog.dtos.GoogleDriveDTO;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.ClientEntity;
import com.pivinadanang.blog.responses.client.ClientResponse;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.services.client.ClientService;
import com.pivinadanang.blog.services.cloudinary.ICloudinaryService;
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
    private final ICloudinaryService cloudinaryService;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllClients() {
        List<ClientResponse> clients = clientService.getAllClients();
        return ResponseEntity.ok(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_CLIENT_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(clients)
                .build());
    }
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> insertClient(@Valid @ModelAttribute ClientDTO clientDTO, BindingResult result) {
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
        try {
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
    @PatchMapping(value = "{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE,
                        MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                        MediaType.MULTIPART_FORM_DATA_VALUE},
            produces =  MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> updateClient(@Valid @ModelAttribute ClientDTO clientDTO,
                                                       @PathVariable  Long id,
                                                       BindingResult result) throws DataNotFoundException, IOException {
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
        ClientResponse client = clientService.updateClient(id, clientDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_CLIENT_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(client)
                .build());
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
            exception.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_CLIENT_FAILED))
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build());
        }
    }
}
