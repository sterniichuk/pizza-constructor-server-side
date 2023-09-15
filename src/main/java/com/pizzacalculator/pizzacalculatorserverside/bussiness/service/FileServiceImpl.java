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
    private final ResourceLoader resourceLoader;

    public Resource read(String path) {
        return loadAsResource(path);
    }

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