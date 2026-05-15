package com.qzy.springbootlogin.config;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;

@RestController
public class ImageFallbackController {

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Resource resource = new ClassPathResource("static/images/" + filename);
            if (resource.exists()) {
                String contentType = "image/svg+xml";
                if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) contentType = "image/svg+xml";
                else if (filename.endsWith(".png")) contentType = "image/png";
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            }
        } catch (Exception e) {}
        Resource fallback = new ClassPathResource("static/images/default-product.svg");
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("image/svg+xml"))
                .body(fallback);
    }
}
