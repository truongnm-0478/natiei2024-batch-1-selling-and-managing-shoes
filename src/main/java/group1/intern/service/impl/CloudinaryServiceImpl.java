package group1.intern.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import group1.intern.service.CloudinaryService;
import group1.intern.util.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public Map<String, Object> uploadFile(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String url = (String) uploadResult.get("url");
            String publicId = (String) uploadResult.get("public_id");

            Map<String, Object> result = new HashMap<>();
            result.put("url", url);
            result.put("public_id", publicId);

            return result;
        } catch (Exception e) {
            throw new BadRequestException("Có lỗi xả ra trong quá trình lưu ảnh!");
        }
    }

    @Override
    public Map<String, Object> deleteFileByUrl(String url) {
        try {
            String publicId = extractPublicIdFromUrl(url);
            return deleteFile(publicId);
        } catch (Exception e) {
            throw new BadRequestException("Xoá ảnh không thành công!");
        }
    }

    @Override
    public Map<String, Object> deleteFile(String publicId) {
        try {
            return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new BadRequestException("Xoá ảnh không thành công!");
        }
    }

    private String extractPublicIdFromUrl(String url) {
        String[] parts = url.split("/");
        String publicIdWithFormat = parts[parts.length - 1];
        return publicIdWithFormat.substring(0, publicIdWithFormat.lastIndexOf('.'));
    }
}
