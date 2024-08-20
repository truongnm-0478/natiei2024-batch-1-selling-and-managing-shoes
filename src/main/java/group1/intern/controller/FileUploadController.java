package group1.intern.controller;

import group1.intern.service.CloudinaryService;
import group1.intern.service.CloudinaryService;
import group1.intern.util.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Controller
public class FileUploadController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("/upload")
    public String showUploadForm() {
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {
        try {
            Map uploadResult = cloudinaryService.uploadFile(file);
            model.addAttribute("uploadResult", uploadResult);
            return "upload";
        } catch (BadRequestException e) {
            model.addAttribute("error", "File upload failed: " + e.getMessage());
            return "upload";
        }
    }

    @PostMapping("/delete")
    public String deleteFile(@RequestParam("publicId") String publicId, Model model) {
        try {
            Map deleteResult = cloudinaryService.deleteFile(publicId);
            model.addAttribute("deleteResult", deleteResult);
            return "deleteResult";
        } catch (BadRequestException e) {
            model.addAttribute("error", "File deletion failed: " + e.getMessage());
            return "deleteResult";
        }
    }

    @PostMapping("/deleteByUrl")
    public String deleteFileByUrl(@RequestParam("url") String url, Model model) {
        try {
            Map deleteResult = cloudinaryService.deleteFileByUrl(url);
            model.addAttribute("deleteResult", deleteResult);
            return "deleteResult"; // Tên của tệp HTML mà bạn đang sử dụng
        } catch (BadRequestException e) {
            model.addAttribute("error", "File deletion failed: " + e.getMessage());
            return "deleteResult";
        }
    }
}