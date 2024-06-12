package com.example.upload.controller;

import com.example.upload.model.FileModel;
import com.example.upload.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.nio.file.Path;

@RestController
@RequestMapping("/files")
@CrossOrigin
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public FileModel uploadFile(@RequestParam("file") MultipartFile file) {
        return fileService.store(file);
    }

    @GetMapping("/download/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Path file = fileService.load(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentDispositionFormData("attachment", resource.getFilename());
                // Set the correct Content-Type for JPEG images
                headers.setContentType(MediaType.IMAGE_JPEG);
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
