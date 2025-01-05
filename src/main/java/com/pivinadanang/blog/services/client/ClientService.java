package com.pivinadanang.blog.services.client;

import com.pivinadanang.blog.dtos.ClientDTO;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.ClientEntity;
import com.pivinadanang.blog.models.ImageEntity;
import com.pivinadanang.blog.repositories.ClientRepository;
import com.pivinadanang.blog.repositories.ImageRepository;
import com.pivinadanang.blog.responses.client.ClientResponse;
import com.pivinadanang.blog.services.cloudinary.ICloudinaryService;

import com.pivinadanang.blog.ultils.HtmlSanitizer;
import com.pivinadanang.blog.ultils.HtmlSanitizerWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService implements IClientService{
    private final ClientRepository clientRepository;
    private final ImageRepository imageRepository;
    private final HtmlSanitizerWrapper htmlSanitizerWrapper;


    @Override
    public boolean exitsByName(String name) {
        return clientRepository.existsByName(name);
    }

    @Override
    public ClientEntity findById(long id) throws Exception {
        return clientRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find client with id " + id));
    }

    public ClientResponse createClient(ClientDTO clientDTO) throws IOException {
        String sanitizedDescription = htmlSanitizerWrapper.sanitize(clientDTO.getDescription());

        // Tìm hình ảnh tương ứng dựa trên publicId
        ImageEntity imageEntity = imageRepository.findByPublicId(clientDTO.getPublicId())
                .orElseThrow(() -> new RuntimeException("Image not found"));

        // Cập nhật thuộc tính isUsed và usageCount
        imageEntity.setIsUsed(true);
        imageEntity.setUsageCount(imageEntity.getUsageCount() + 1);

        // Lưu thay đổi vào cơ sở dữ liệu
        imageRepository.save(imageEntity);

        // Tạo client mới
        ClientEntity newClient = ClientEntity.builder()
                .name(clientDTO.getName())
                .logo(clientDTO.getLogo())
                .publicId(clientDTO.getPublicId())
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
        ClientEntity existingClient = clientRepository.findById(clientId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find client with id " + clientId));

        if (clientDTO.getName() != null && !clientDTO.getName().isEmpty()) {
            if (!existingClient.getName().equals(clientDTO.getName())) {
                boolean isNameExists = clientRepository.existsByName(clientDTO.getName());
                if (isNameExists) {
                    throw new IllegalArgumentException("Name already exists");
                }
            }
            existingClient.setName(clientDTO.getName());
        }

        if (clientDTO.getDescription() != null && !clientDTO.getDescription().isEmpty()) {
            String sanitizedDescription = htmlSanitizerWrapper.sanitize(clientDTO.getDescription());
            existingClient.setDescription(sanitizedDescription);
        }

        if (clientDTO.getLogo() != null && !clientDTO.getLogo().isEmpty() &&
                clientDTO.getPublicId() != null && !clientDTO.getPublicId().isEmpty()) {

            String newFileId = clientDTO.getPublicId();
            // Tìm hình ảnh hiện tại dựa trên publicId
            ImageEntity currentImage = imageRepository.findByPublicId(existingClient.getPublicId())
                    .orElse(null);

            if (currentImage != null) {
                // Giảm usageCount và cập nhật isUsed của hình ảnh hiện tại nếu cần
                currentImage.setUsageCount(currentImage.getUsageCount() - 1);
                if (currentImage.getUsageCount() <= 0) {
                    currentImage.setIsUsed(false);
                }
                imageRepository.save(currentImage);
            } else {
                // Log a warning if the current image is not found
                System.out.println("Warning: Current image not found, proceeding with new image.");
            }

            // Tìm hình ảnh mới dựa trên publicId
            ImageEntity newImage = imageRepository.findByPublicId(newFileId)
                    .orElseThrow(() -> new RuntimeException("New image not found"));

            // Cập nhật thuộc tính isUsed và usageCount của hình ảnh mới
            newImage.setIsUsed(true);
            newImage.setUsageCount(newImage.getUsageCount() + 1);

            imageRepository.save(newImage);

            existingClient.setLogo(clientDTO.getLogo());
            existingClient.setPublicId(newFileId);
        }

        ClientEntity updatedClient = clientRepository.save(existingClient);
        return ClientResponse.fromClient(updatedClient);
    }

    @Override
    @Transactional
    public void deleteClients(long id) throws Exception {
        ClientEntity existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find client with id " + id));
        try {
            // Tìm hình ảnh hiện tại dựa trên publicId
            ImageEntity currentImage = imageRepository.findByPublicId(existingClient.getPublicId())
                    .orElse(null);

            if (currentImage != null) {
                // Giảm usageCount và cập nhật isUsed của hình ảnh hiện tại nếu cần
                currentImage.setUsageCount(currentImage.getUsageCount() - 1);
                if (currentImage.getUsageCount() <= 0) {
                    currentImage.setIsUsed(false);
                }
                imageRepository.save(currentImage);
            }


            // Xóa client khỏi cơ sở dữ liệu
            clientRepository.delete(existingClient);


        } catch (Exception e) {
            // Ném ngoại lệ cụ thể hơn với thông báo rõ ràng
            throw new RuntimeException("Failed to delete client with id: " + id, e);
        }
    }
}
