package com.example.upload.repository;

import com.example.upload.model.FileModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileModel, Long> {
}
