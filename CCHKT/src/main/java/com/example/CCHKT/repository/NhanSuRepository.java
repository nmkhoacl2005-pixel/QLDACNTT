package com.example.CCHKT.repository;

import com.example.CCHKT.entity.NhanSu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NhanSuRepository extends JpaRepository<NhanSu, String> {
    // Tìm nhân sự theo tài khoản và mật khẩu chưa mã hoá
    Optional<NhanSu> findByTaiKhoanAndMatKhau(String taiKhoan, String matKhau);
    Optional<NhanSu> findByCccd(String cccd);
    Optional<NhanSu> findByTrangThai(String trangThai);
}