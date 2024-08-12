package com.pivinadanang.blog.services.client;

import com.pivinadanang.blog.dtos.ClientDTO;
import com.pivinadanang.blog.models.ClientEntity;
import com.pivinadanang.blog.responses.ClientResponse;

import java.util.List;

public interface IClientService {
    boolean exitsByName(String name);
    ClientEntity findById(long id) throws Exception;
    ClientResponse createClient(ClientDTO clientDTO) throws Exception;
    List<ClientResponse> getAllClients();
    ClientResponse updateClient(long clientId, ClientDTO clientDTO) throws Exception;
    void deleteClients(long id) throws Exception;
}
