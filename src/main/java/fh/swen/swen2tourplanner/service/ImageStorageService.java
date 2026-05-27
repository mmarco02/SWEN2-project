package fh.swen.swen2tourplanner.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class ImageStorageService {
    /*
    https://www.geeksforgeeks.org/java/spring-boot-file-handling/
    https://medium.com/@dulanjayasandaruwan1998/uploading-images-in-a-spring-boot-project-a-step-by-step-guide-8a55248ea520
     */

    @Value("${app.upload.dir:uploads/images}")
    private String uploadDir;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        rootLocation = Paths.get(uploadDir).toAbsolutePath();
        try {
            Files.createDirectories(rootLocation);
            log.info("Image storage directory: {}", rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public String store(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID() + extension;
        Files.copy(file.getInputStream(), rootLocation.resolve(filename));
        log.info("Stored image: {}", filename);
        return filename;
    }

    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    public void delete(String filename) throws IOException {
        if (filename != null) {
            Files.deleteIfExists(rootLocation.resolve(filename));
            log.info("Deleted image: {}", filename);
        }
    }
}
