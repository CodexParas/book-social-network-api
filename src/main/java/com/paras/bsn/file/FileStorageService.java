package com.paras.bsn.file;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.io.File.separator;
import static java.lang.System.currentTimeMillis;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${fileUploadPath}")
    private String fileUploadPath;

    public String saveFile(
            @NotNull MultipartFile file,
            @NotNull Integer userId) {
        final String fileUploadSubPath = "users" + separator + userId;
        return upload(file, fileUploadSubPath);
    }

    private String upload(@NotNull MultipartFile file, String fileUploadSubPath) {
        final String finalUploadPath = fileUploadPath + separator + fileUploadSubPath;
        File targetFolder = new File(finalUploadPath);
        if (!targetFolder.exists()) {
            boolean folderCreated = targetFolder.mkdirs();
            if (!folderCreated) {
                log.warn("Failed to create folder: {}", finalUploadPath);
                return null;
            }
        }
        final String fileExtension = getFileExtension(file.getOriginalFilename());
        String targetFilePath = finalUploadPath + separator + currentTimeMillis() + "." + fileExtension;
        Path targetPath = Paths.get(targetFilePath);
        try {
            Files.write(targetPath, file.getBytes());
            log.info("File saved successfully: {}", targetFilePath);
            return targetFilePath;
        } catch (IOException e) {
            log.error("Failed to save file: {}", targetFilePath);
            return null;
        }
    }

    private String getFileExtension(String originalFilename) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            return "";
        }
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return originalFilename.substring(lastDotIndex + 1).toLowerCase();
    }
}
