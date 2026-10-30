package com.example.CCHKT.config;

import com.example.CCHKT.entity.NhanSu;
import com.example.CCHKT.repository.NhanSuRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initAdmin(NhanSuRepository nhanSuRepository) {
        return args -> {
            if (nhanSuRepository.findByTaiKhoan("admin").isEmpty()) {
                NhanSu admin = new NhanSu("000000000001", "Administrator", "0000000001", "admin", "admin123", "admin");
                admin.setTrangThai("active");
                nhanSuRepository.save(admin);
            }
        };
    }
}