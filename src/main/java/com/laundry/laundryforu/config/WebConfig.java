package com.laundry.laundryforu.config; // Pastikan ini sesuai letak foldernya

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Ini kuncinya: URL /uploads/** dipetakan ke folder fisik di luar src
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}