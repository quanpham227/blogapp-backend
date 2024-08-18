package com.pivinadanang.blog.services.client;

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
import com.pivinadanang.blog.ultils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ClientService implements IClientService{
    private final ClientRepository clientRepository;
    private final ICloudinaryService iCloudinaryService;
    private final LocalizationUtils localizationUtils;

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
        // Kiểm tra kích thước file và định dạng
        if (clientDTO.getFile().getSize() > 5 * 1024 * 1024) { // Kích thước > 5MB
         throw new FileSizeExceededException(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_LARGE));
        }
        String contentType = clientDTO.getFile().getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidFileTypeException(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE));
        }
        File file = FileUtils.handleFile(clientDTO.getFile());
        CloudinaryDTO cloudinaryDTO = iCloudinaryService.upload(clientDTO.getFile(), "clients");

        ClientEntity newClient = ClientEntity.builder()
                .name(clientDTO.getName())
                .logo(cloudinaryDTO.getUrl())
                .publicId(cloudinaryDTO.getPublicId())
                .description(clientDTO.getDescription())
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
        if((clientDTO.getFile() != null) && (!clientDTO.getFile().isEmpty())){
            // Kiểm tra kích thước file và định dạng
            if (clientDTO.getFile().getSize() > 5 * 1024 * 1024) { // Kích thước > 5MB
                throw new FileSizeExceededException(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_LARGE));
            }
            String contentType = clientDTO.getFile().getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new InvalidFileTypeException(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE));
            }
            FileUtils.handleFile(clientDTO.getFile());

            CloudinaryDTO cloudinaryDTO = iCloudinaryService.update(exitsClient.getPublicId(), clientDTO.getFile());
            exitsClient.setLogo(cloudinaryDTO.getUrl());
            exitsClient.setPublicId(cloudinaryDTO.getPublicId());
        }
        exitsClient.setName(clientDTO.getName());
        exitsClient.setDescription(clientDTO.getDescription());
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
