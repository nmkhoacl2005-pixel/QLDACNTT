package com.example.CCHKT.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Toàn bộ giao diện (login.html, các trang trong HTML/admin/...) nằm ở thư mục
 * "HTML/" tại gốc dự án, bên ngoài src/main/resources/static, nên Spring Boot
 * KHÔNG tự phục vụ các file này theo mặc định. Cấu hình dưới đây ánh xạ:
 *   http://localhost:8080/HTML/**  ->  thư mục ./HTML (chạy ứng dụng tại thư mục gốc dự án)
 * và điều hướng trang chủ "/" sang thẳng trang đăng nhập.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/HTML/**")
                .addResourceLocations("file:HTML/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/HTML/login.html");
    }
}