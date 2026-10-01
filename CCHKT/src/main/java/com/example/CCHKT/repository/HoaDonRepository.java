package com.example.CCHKT.repository;

import com.example.CCHKT.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, String> {
    List<HoaDon> findAllByOrderByNgayDatDesc();
    List<HoaDon> findByKhachHang_SdtOrderByNgayDatDesc(String sdt);
}