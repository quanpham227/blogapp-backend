package com.pivinadanang.blog.services.client;

import com.pivinadanang.blog.dtos.ClientDTO;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.ClientEntity;
import com.pivinadanang.blog.repositories.ClientRepository;
import com.pivinadanang.blog.responses.ClientResponse;
import com.pivinadanang.blog.responses.category.CategoryResponse;
import com.pivinadanang.blog.services.google.GoogleDiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ClientService implements IClientService{
    private final ClientRepository clientRepository;
    private final GoogleDiveService googleDiveService;

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
    public ClientResponse createClient(ClientDTO clientDTO) {
        ClientEntity newClient = ClientEntity.builder()
                .name(clientDTO.getName())
                .logo(clientDTO.getLogo())
                .fileId(clientDTO.getFileId())
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
    public ClientResponse updateClient(long clientId, ClientDTO clientDTO) throws DataNotFoundException{
        ClientEntity exitsClient = clientRepository.findById(clientId)
        .orElseThrow(() -> new DataNotFoundException("Cannot find client with id " + clientId));
        exitsClient.setName(clientDTO.getName());
        exitsClient.setLogo(clientDTO.getLogo());
        exitsClient.setFileId(clientDTO.getFileId());
        exitsClient.setDescription(clientDTO.getDescription());
       ClientEntity clientEntity =  clientRepository.save(exitsClient);
       return  ClientResponse.fromClient(clientEntity);
    }

    @Override
    @Transactional
    public void deleteClients(long id) throws Exception {
        ClientEntity exitsClient = clientRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find client with id " + id));
        clientRepository.delete(exitsClient);
        try {
            googleDiveService.deleteFileFromDrive(exitsClient.getFileId());
        } catch (Exception e) {
            // Ghi nhận lỗi xóa file hoặc thực hiện hành động bù trừ nếu cần
            // Ví dụ: gửi thông báo qua email để xử lý sau
            throw new Exception("Failed to delete file from Google Drive", e);
        }
    }
}
