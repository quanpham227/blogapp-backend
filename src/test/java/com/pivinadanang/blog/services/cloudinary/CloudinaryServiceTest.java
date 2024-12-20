package com.pivinadanang.blog.services.cloudinary;

import com.cloudinary.Api;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.Uploader;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import com.pivinadanang.blog.dtos.CloudinaryDTO;
import com.pivinadanang.blog.exceptions.CloudinaryException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CloudinaryServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private Api api;

    @InjectMocks
    private CloudinaryService cloudinaryService;
    @Mock
    private Uploader uploader;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Chỉ cần gọi một lần
        when(cloudinary.uploader()).thenReturn(uploader);
        when(cloudinary.api()).thenReturn(api);
    }

    @Test
    public void testUpload_Success() throws Exception {
        // Mock upload result
        Map<String, Object> uploadResult = Map.of(
                "public_id", "sample_public_id",
                "secure_url", "http://example.com/sample.jpg",
                "bytes", 12345L
        );

        when(multipartFile.getBytes()).thenReturn(new byte[0]);
        when(multipartFile.getOriginalFilename()).thenReturn("sample.jpg");
        when(uploader.upload(any(byte[].class), any(Map.class))).thenReturn(uploadResult);

        // Call the service method
        CloudinaryDTO cloudinaryDTO = cloudinaryService.upload(multipartFile, "test_folder", 100, 100);

        // Assertions
        assertEquals("sample_public_id", cloudinaryDTO.getPublicId());
        assertEquals("http://example.com/sample.jpg", cloudinaryDTO.getUrl());
        assertEquals("sample.jpg", cloudinaryDTO.getFileName());
        assertEquals(12345L, cloudinaryDTO.getFileSize());
    }
    @Test
    public void testUpload_Failure() throws Exception {
        when(multipartFile.getBytes()).thenThrow(new RuntimeException("Upload failed"));

        // Call the service method and expect an exception
        Exception exception = assertThrows(CloudinaryException.class, () -> {
            cloudinaryService.upload(multipartFile, "test_folder", 100, 100);
        });

        // Assertions
        assertEquals("failed to load to Cloudinary the image file: ", exception.getMessage());
    }

    @Test
    public void testUpdate_Success() throws Exception {
        // Mock upload result
        Map<String, Object> uploadResult = Map.of(
                "public_id", "sample_public_id",
                "secure_url", "http://example.com/sample.jpg",
                "bytes", 12345L
        );

        when(multipartFile.getBytes()).thenReturn(new byte[0]);
        when(multipartFile.getOriginalFilename()).thenReturn("sample.jpg");
        when(cloudinary.uploader().upload(any(byte[].class), any(Map.class))).thenReturn(uploadResult);

        // Call the service method
        CloudinaryDTO cloudinaryDTO = cloudinaryService.update("sample_public_id", multipartFile, 100, 100);

        // Assertions
        assertEquals("sample_public_id", cloudinaryDTO.getPublicId());
        assertEquals("http://example.com/sample.jpg", cloudinaryDTO.getUrl());
        assertEquals("sample.jpg", cloudinaryDTO.getFileName());
        assertEquals(12345L, cloudinaryDTO.getFileSize());
    }

    @Test
    public void testUpdate_Failure() throws Exception {
        when(multipartFile.getBytes()).thenThrow(new RuntimeException("Update failed"));

        // Call the service method and expect an exception
        Exception exception = assertThrows(CloudinaryException.class, () -> {
            cloudinaryService.update("sample_public_id", multipartFile, 100, 100);
        });

        // Assertions
        assertEquals("Failed to update image on Cloudinary with public ID sample_public_id", exception.getMessage());
    }

    @Test
    public void testDelete_Success() throws Exception {
        // Mock delete result
        Map<String, Object> deleteResult = Map.of("result", "ok");

        when(cloudinary.uploader().destroy(anyString(), any(Map.class))).thenReturn(deleteResult);

        // Call the service method
        cloudinaryService.delete("sample_public_id");

        // Verify the delete method was called
        verify(cloudinary.uploader(), times(1)).destroy("sample_public_id", ObjectUtils.emptyMap());
    }

    @Test
    public void testDelete_Failure() throws Exception {
        when(cloudinary.uploader().destroy(anyString(), any(Map.class))).thenThrow(new RuntimeException("Delete failed"));

        // Call the service method and expect an exception
        Exception exception = assertThrows(CloudinaryException.class, () -> {
            cloudinaryService.delete("sample_public_id");
        });

        // Assertions
        assertEquals("failed to delete  Cloudinary the image file: ", exception.getMessage());
    }

//    @Test
//    public void testDownload_Success() throws Exception {
//        // Mock Cloudinary API
//        Cloudinary cloudinary = mock(Cloudinary.class);
//        com.cloudinary.Url cloudinaryUrl = mock(com.cloudinary.Url.class);
//        when(cloudinary.url()).thenReturn(cloudinaryUrl);
//
//        // Mock URL and InputStream
//        URL url = mock(URL.class);
//        InputStream inputStream = mock(InputStream.class);
//        byte[] byteArray = new byte[0];
//
//        when(url.openStream()).thenReturn(inputStream);
//        when(IOUtils.toByteArray(inputStream)).thenReturn(byteArray);
//        when(cloudinaryUrl.secure(true)).thenReturn(cloudinaryUrl);
//        when(cloudinaryUrl.format(anyString())).thenReturn(cloudinaryUrl);
//        when(cloudinaryUrl.transformation(any(Transformation.class))).thenReturn(cloudinaryUrl);
//        when(cloudinaryUrl.publicId(anyString())).thenReturn(cloudinaryUrl);
//        when(cloudinaryUrl.generate()).thenReturn("http://example.com/sample.jpg");
//
//        // Inject the mocked Cloudinary into the service
//        CloudinaryService cloudinaryService = new CloudinaryService(cloudinary);
//
//        // Call the service method
//        ByteArrayResource resource = cloudinaryService.download("sample_public_id", 100, 100, false);
//
//        // Assertions
//        assertNotNull(resource);
//    }
//    @Test
    public void testDownload_Failure() throws Exception {
        // Mock Cloudinary API
        Cloudinary cloudinary = mock(Cloudinary.class);
        com.cloudinary.Url cloudinaryUrl = mock(com.cloudinary.Url.class);
        when(cloudinary.url()).thenReturn(cloudinaryUrl);

        // Mock URL and InputStream
        URL url = mock(URL.class);
        InputStream inputStream = mock(InputStream.class);

        when(cloudinaryUrl.secure(true)).thenReturn(cloudinaryUrl);
        when(cloudinaryUrl.format(anyString())).thenReturn(cloudinaryUrl);
        when(cloudinaryUrl.transformation(any(Transformation.class))).thenReturn(cloudinaryUrl);
        when(cloudinaryUrl.publicId(anyString())).thenReturn(cloudinaryUrl);
        when(cloudinaryUrl.generate()).thenReturn("http://example.com/sample.jpg");

        // Mock URL to throw exception
        URL generatedUrl = new URL("http://example.com/sample.jpg");
        URL urlSpy = spy(generatedUrl);
        doThrow(new RuntimeException("Download failed")).when(urlSpy).openStream();

        // Inject the mocked Cloudinary into the service
        CloudinaryService cloudinaryService = new CloudinaryService(cloudinary);

        // Call the service method and expect an exception
        Exception exception = assertThrows(CloudinaryException.class, () -> {
            cloudinaryService.download("sample_public_id", 100, 100, false);
        });

        // Assertions
        assertEquals("failed to download image from Cloudinary with public ID sample_public_id", exception.getMessage());
    }
    @Test
    public void testCheckImageExistsOnCloudinary_Success() throws Exception {
        // Mock Cloudinary API
        Cloudinary cloudinary = mock(Cloudinary.class);
        Api api = mock(Api.class);
        when(cloudinary.api()).thenReturn(api);

        // Mock resource result
        ApiResponse resourceResult = mock(ApiResponse.class);
        when(resourceResult.get("public_id")).thenReturn("sample_public_id");
        when(api.resource(anyString(), any(Map.class))).thenReturn(resourceResult);

        // Inject the mocked Cloudinary into the service
        CloudinaryService cloudinaryService = new CloudinaryService(cloudinary);

        // Call the service method
        boolean exists = cloudinaryService.checkImageExistsOnCloudinary("sample_public_id");

        // Assertions
        assertTrue(exists);
    }
    @Test
    public void testCheckImageExistsOnCloudinary_Failure() throws Exception {
        when(api.resource(anyString(), any(Map.class))).thenThrow(new RuntimeException("Resource not found"));

        // Call the service method
        boolean exists = cloudinaryService.checkImageExistsOnCloudinary("sample_public_id");

        // Assertions
        assertFalse(exists);
    }
    @Test
    public void testListResources_Success() throws Exception {
        // Mock Cloudinary API
        Cloudinary cloudinary = mock(Cloudinary.class);
        Api api = mock(Api.class);
        when(cloudinary.api()).thenReturn(api);

        // Mock ApiResponse
        ApiResponse resourcesResult = mock(ApiResponse.class);
        when(resourcesResult.get("resources")).thenReturn("sample_resources");
        when(api.resources(any(Map.class))).thenReturn(resourcesResult);

        // Inject the mocked Cloudinary into the service
        CloudinaryService cloudinaryService = new CloudinaryService(cloudinary);

        // Call the service method
        Map<?, ?> resources = cloudinaryService.listResources("test_folder");

        // Assertions
        assertEquals("sample_resources", resources.get("resources"));
    }
    @Test
    public void testListResources_Failure() throws Exception {
        when(api.resources(any(Map.class))).thenThrow(new RuntimeException("List resources failed"));

        // Call the service method and expect an exception
        Exception exception = assertThrows(CloudinaryException.class, () -> {
            cloudinaryService.listResources("test_folder");
        });

        // Assertions
        assertEquals("Failed to list resources in folder: test_folder", exception.getMessage());
    }
    @Test
    public void testRenameResource_Success() throws Exception {
        // Mock Cloudinary API
        Cloudinary cloudinary = mock(Cloudinary.class);
        Uploader uploader = mock(Uploader.class);
        when(cloudinary.uploader()).thenReturn(uploader);

        // Mock rename result
        ApiResponse renameResult = mock(ApiResponse.class);
        when(renameResult.get("result")).thenReturn("ok");
        when(uploader.rename(anyString(), anyString(), any(Map.class))).thenReturn(renameResult);

        // Inject the mocked Cloudinary into the service
        CloudinaryService cloudinaryService = new CloudinaryService(cloudinary);

        // Call the service method
        cloudinaryService.renameResource("old_public_id", "new_public_id");

        // Verify the rename method was called
        verify(uploader, times(1)).rename("old_public_id", "new_public_id", ObjectUtils.emptyMap());
    }

    @Test
    public void testRenameResource_Failure() throws Exception {
        // Mock Cloudinary API
        Cloudinary cloudinary = mock(Cloudinary.class);
        Uploader uploader = mock(Uploader.class);
        when(cloudinary.uploader()).thenReturn(uploader);

        // Mock rename failure
        when(uploader.rename(anyString(), anyString(), any(Map.class))).thenThrow(new RuntimeException("Rename failed"));

        // Inject the mocked Cloudinary into the service
        CloudinaryService cloudinaryService = new CloudinaryService(cloudinary);

        // Call the service method and expect an exception
        Exception exception = assertThrows(CloudinaryException.class, () -> {
            cloudinaryService.renameResource("old_public_id", "new_public_id");
        });

        // Assertions
        assertEquals("Failed to rename resource from old_public_id to new_public_id", exception.getMessage());
    }

    @Test
    public void testCreateFolder_Success() throws Exception {
        // Mock Cloudinary API
        Cloudinary cloudinary = mock(Cloudinary.class);
        Api api = mock(Api.class);
        when(cloudinary.api()).thenReturn(api);

        // Mock create folder result
        ApiResponse createFolderResult = mock(ApiResponse.class);
        when(createFolderResult.get("result")).thenReturn("ok");
        when(api.createFolder(anyString(), any(Map.class))).thenReturn(createFolderResult);

        // Inject the mocked Cloudinary into the service
        CloudinaryService cloudinaryService = new CloudinaryService(cloudinary);

        // Call the service method
        cloudinaryService.createFolder("test_folder");

        // Verify the create folder method was called
        verify(api, times(1)).createFolder("test_folder", ObjectUtils.emptyMap());
    }

    @Test
    public void testCreateFolder_Failure() throws Exception {
        when(cloudinary.api().createFolder(anyString(), any(Map.class))).thenThrow(new RuntimeException("Create folder failed"));

        // Call the service method and expect an exception
        Exception exception = assertThrows(CloudinaryException.class, () -> {
            cloudinaryService.createFolder("test_folder");
        });

        // Assertions
        assertEquals("Failed to create folder: test_folder", exception.getMessage());
    }

    @Test
    public void testDeleteFolder_Success() throws Exception {
        // Mock Cloudinary API
        Cloudinary cloudinary = mock(Cloudinary.class);
        Api api = mock(Api.class);
        when(cloudinary.api()).thenReturn(api);

        // Mock delete folder result
        ApiResponse deleteFolderResult = mock(ApiResponse.class);
        when(deleteFolderResult.get("result")).thenReturn("ok");
        when(api.deleteFolder(anyString(), any(Map.class))).thenReturn(deleteFolderResult);

        // Inject the mocked Cloudinary into the service
        CloudinaryService cloudinaryService = new CloudinaryService(cloudinary);

        // Call the service method
        cloudinaryService.deleteFolder("test_folder");

        // Verify the delete folder method was called
        verify(api, times(1)).deleteFolder("test_folder", ObjectUtils.emptyMap());
    }

    @Test
    public void testDeleteFolder_Failure() throws Exception {
        when(cloudinary.api().deleteFolder(anyString(), any(Map.class))).thenThrow(new RuntimeException("Delete folder failed"));

        // Call the service method and expect an exception
        Exception exception = assertThrows(CloudinaryException.class, () -> {
            cloudinaryService.deleteFolder("test_folder");
        });

        // Assertions
        assertEquals("Failed to delete folder: test_folder", exception.getMessage());
    }
}