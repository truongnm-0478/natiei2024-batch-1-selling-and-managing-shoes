package group1.intern.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CloudinaryService {
    Map<String, Object> uploadFile(MultipartFile file);
    Map<String, Object> deleteFileByUrl(String url);
    Map<String, Object> deleteFile(String publicId);
}
