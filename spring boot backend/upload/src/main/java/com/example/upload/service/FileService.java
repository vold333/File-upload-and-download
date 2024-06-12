package com.example.upload.service;

import com.example.upload.model.FileModel;
import com.example.upload.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {

    private final Path rootLocation;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    public FileService(@Value("${file.storage.location}") String location) {
        this.rootLocation = Paths.get(location);
    }

    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    public FileModel store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file");
            }
            String fileName = file.getOriginalFilename();
            Path destinationFile = rootLocation.resolve(Paths.get(fileName))
                    .normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(rootLocation.toAbsolutePath())) {
                throw new RuntimeException("Cannot store file outside current directory");
            }

            Files.copy(file.getInputStream(), destinationFile);

            FileModel fileModel = new FileModel();
            fileModel.setFileName(fileName);
            fileModel.setFilePath(destinationFile.toString());
            return fileRepository.save(fileModel);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public Path load(String fileName) {
        return rootLocation.resolve(fileName);
    }
}
