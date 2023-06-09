package com.pizzacalculator.pizzacalculatorserverside.bussiness.service;


import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
//import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class FileServiceImpl {
//    private final Logger logger = Logger.getLogger(String.valueOf(this.getClass()));
    private final ResourceLoader resourceLoader;

//    public void save(String path, MultipartFile file) {
//        try {
//            file.transferTo(Path.of(path));
//        } catch (IOException e) {
//            String message = "Failed to transfer multipart file to filesystem";
//            throw new IllegalArgumentException(message, e);
//        }
//    }


    public Resource read(String path) {
        return loadAsResource(path);
    }


//    public void delete(String path) {
//        Path pathObj = Paths.get(path);
//        try {
//            boolean exists = Files.deleteIfExists(pathObj);
//            if (!exists) {
//                logger.info("file: " + pathObj.getFileName() + " doesn't exist");
//            }
//        } catch (IOException e) {
//            throw new IllegalArgumentException("Failed to delete file " + pathObj.getFileName(), e);
//        }
//    }

    private Resource loadAsResource(String path) {
        Resource resource = resourceLoader.getResource("classpath:static" + File.separator + path);
        if (resource.exists() || resource.isReadable()) {
            return resource;
        }
        String message = "file: " + path
                + " doesn't exist or file is not readable";
        throw new IllegalArgumentException(message);
    }
}