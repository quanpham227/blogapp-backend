package com.pivinadanang.blog.services.client;

import com.pivinadanang.blog.components.ImageSizeConfig;
import com.pivinadanang.blog.components.converters.LocalizationUtils;
import com.pivinadanang.blog.dtos.ClientDTO;
import com.pivinadanang.blog.dtos.CloudinaryDTO;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.exceptions.FileSizeExceededException;
import com.pivinadanang.blog.exceptions.InvalidFileTypeException;
import com.pivinadanang.blog.models.ClientEntity;
import com.pivinadanang.blog.repositories.ClientRepository;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.client.ClientResponse;
import com.pivinadanang.blog.services.cloudinary.ICloudinaryService;
import com.pivinadanang.blog.services.google.GoogleDiveService;
import com.pivinadanang.blog.ultils.FileUtils;
import com.pivinadanang.blog.ultils.HtmlSanitizer;
import com.pivinadanang.blog.ultils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService implements IClientService{
    private final ClientRepository clientRepository;
    private final ICloudinaryService iCloudinaryService;
    private final LocalizationUtils localizationUtils;
    private final ImageSizeConfig imageSizeConfig; // Inject the configuration class


    @Override
    public boolean exitsByName(String name) {
        return clientRepository.existsByName(name);
    }

    @Override
    public ClientEntity findById(long id) throws Exception {
        return clientRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find client with id " + id));
    }

    @Override
    @Transactional
    public ClientResponse createClient(ClientDTO clientDTO) throws IOException {
        String fileId = FileUtils.extractFileIdFromUrl(clientDTO.getLogo());
        String sanitizedDescription = HtmlSanitizer.sanitize(clientDTO.getDescription());
        ClientEntity newClient = ClientEntity.builder()
                .name(clientDTO.getName())
                .logo(clientDTO.getLogo())
                .publicId(fileId)
                .description(sanitizedDescription)
                .build();
        ClientEntity clientEntity = clientRepository.save(newClient);
        return ClientResponse.fromClient(clientEntity);
    }

    @Override
    public List<ClientResponse> getAllClients() {
        return clientRepository.findAll().stream().map(ClientResponse::fromClient).toList();
    }

    @Override
    @Transactional
    public ClientResponse updateClient(long clientId, ClientDTO clientDTO) throws DataNotFoundException, IOException {
        ClientEntity exitsClient = clientRepository.findById(clientId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find client with id " + clientId));

        if (clientDTO.getName() != null || !clientDTO.getName().isEmpty()) {
            if (!exitsClient.getName().equals(clientDTO.getName())) {
                boolean isNameExists = clientRepository.existsByName(clientDTO.getName());
                if (isNameExists) {
                    throw new IllegalArgumentException("Name already exists"); // Ném ngoại lệ nếu tên đã tồn tại
                }
            }
            exitsClient.setName(clientDTO.getName());
        }
        if ( clientDTO.getDescription() != null || !clientDTO.getDescription().isEmpty()){
            String sanitizedDescription = HtmlSanitizer.sanitize(clientDTO.getDescription());
            exitsClient.setDescription(sanitizedDescription);
        }

        if (clientDTO.getLogo() != null || !clientDTO.getLogo().isEmpty()) {
            String fileId = FileUtils.extractFileIdFromUrl(clientDTO.getLogo());
            exitsClient.setLogo(clientDTO.getLogo());
            exitsClient.setPublicId(fileId);
        }

       ClientEntity clientEntity =  clientRepository.save(exitsClient);
       return  ClientResponse.fromClient(clientEntity);
    }

    @Override
    @Transactional
    public void deleteClients(long id) throws Exception {
        ClientEntity exitsClient = clientRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find client with id " + id));
        try {
            clientRepository.delete(exitsClient);
            iCloudinaryService.delete(exitsClient.getPublicId());


        } catch (Exception e) {
            // Ném ngoại lệ cụ thể hơn với thông báo rõ ràng
            throw new RuntimeException("Failed to delete client with id: " + id, e);
        }
    }
}
