package org.company.trashambulance.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public boolean removeFileByPath(String filePath) {
        File fileToRemove = new File(filePath);
        if (fileToRemove.exists()) {
            if (fileToRemove.delete()) {
                logger.info("File {} deleted successfully", filePath);
                return true;
            } else {
                logger.error("Failed to delete file {}", filePath);
            }
        } else {
            logger.warn("File {} does not exist", filePath);
        }
        return false;
    }
}
