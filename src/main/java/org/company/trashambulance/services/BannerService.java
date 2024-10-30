package org.company.trashambulance.services;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class BannerService {
    private final ResourceLoader resourceLoader;

    public BannerService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public File getImage() {
        Resource resource = resourceLoader.getResource("classpath:static/banner.png");
        File tempFile = null;
        try {
            tempFile = File.createTempFile("banner", ".png");
            tempFile.deleteOnExit();

            Path path = Paths.get(resource.getURI());
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                byte[] bytes = Files.readAllBytes(path);
                fos.write(bytes);
            }
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
