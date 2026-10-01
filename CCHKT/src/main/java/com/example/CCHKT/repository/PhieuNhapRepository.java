package com.example.CCHKT.repository;

import com.example.CCHKT.entity.PhieuNhap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhieuNhapRepository extends JpaRepository<PhieuNhap, String> {
    List<PhieuNhap> findAllByOrderByNgayNhapDesc();
    long countByNhaCungCap_MaNcc(String maNcc);
}
