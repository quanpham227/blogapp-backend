package com.pivinadanang.blog.controller;

import com.pivinadanang.blog.components.LocalizationUtils;
import com.pivinadanang.blog.dtos.ClientDTO;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.ClientEntity;
import com.pivinadanang.blog.responses.client.ClientResponse;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.services.client.ClientService;
import com.pivinadanang.blog.services.cloudinary.ICloudinaryService;
import com.pivinadanang.blog.ultils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/clients")
@Validated
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;
    private final LocalizationUtils localizationUtils;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllClients() {
        List<ClientResponse> clients = clientService.getAllClients();
        return ResponseEntity.ok(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_CLIENT_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(clients)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getClientById(@PathVariable("id") Long clienttId) throws Exception {
        ClientEntity client = clientService.findById(clienttId);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(client)
                .message("Get client information successfully")
                .status(HttpStatus.OK)
                .build());
    }
    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> insertClient(@Valid @RequestBody ClientDTO clientDTO, BindingResult result) throws Exception {
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
        ClientResponse client = clientService.createClient(clientDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_CLIENT_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(client)
                .build());
    }
    @PutMapping(value = "{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> updateClient(@Valid @RequestBody ClientDTO clientDTO,
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
        ClientResponse client = clientService.updateClient(id, clientDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_CLIENT_SUCCESSFULLY, id))
                .status(HttpStatus.OK)
                .data(client)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> deleteClient(@PathVariable Long id) {
        try {
             clientService.deleteClients(id);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_CLIENT_SUCCESSFULLY, id))
                    .status(HttpStatus.OK)
                    .data(null)
                    .build());
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_CLIENT_FAILED, id))
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build());
        }
    }
}
