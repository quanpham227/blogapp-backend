package com.pivinadanang.blog.controller;

import com.pivinadanang.blog.components.LocalizationUtils;
import com.pivinadanang.blog.dtos.ClientDTO;
import com.pivinadanang.blog.models.ClientEntity;
import com.pivinadanang.blog.responses.client.ClientResponse;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.services.client.IClientService;
import com.pivinadanang.blog.ultils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/clients")
@Validated
@RequiredArgsConstructor
public class ClientController {
    private final IClientService clientService;
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
        if (client == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .message("Client not found")
                            .status(HttpStatus.NOT_FOUND)
                            .build());
        }
        return ResponseEntity.ok(ResponseObject.builder()
                .data(client)
                .message("Get client information successfully")
                .status(HttpStatus.OK)
                .build());
    }
    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> insertClient(@Valid @RequestBody ClientDTO clientDTO) throws Exception {
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
                .status(HttpStatus.CREATED)
                .data(client)
                .build());
    }
    @PutMapping(value = "{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> updateClient(@Valid @RequestBody ClientDTO clientDTO,
                                                       @PathVariable  Long id) throws Exception {

        ClientResponse client = clientService.updateClient(id, clientDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_CLIENT_SUCCESSFULLY, id))
                .status(HttpStatus.OK)
                .data(client)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> deleteClient(@PathVariable Long id) throws Exception {
        clientService.deleteClients(id);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_CLIENT_SUCCESSFULLY, id))
                .status(HttpStatus.OK)
                .data(null)
                .build());
    }
}
