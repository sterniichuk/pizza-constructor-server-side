package com.pizzacalculator.pizzacalculatorserverside.service;

import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;


@Service
@AllArgsConstructor
public class ToppingsService {
    private final FileServiceImpl fileService;

    public Resource downloadPhotoByToppingName(String category, String topping) {
        String path = "food" + $ + category + $ + topping + ".jpeg";
        Resource read;
        try {
            read = fileService.read(path);
        } catch (Exception e) {
            read = fileService.read("not-found.webp");
            e.printStackTrace();
        }
        return read;
    }

    private final String $ = File.separator;

}
