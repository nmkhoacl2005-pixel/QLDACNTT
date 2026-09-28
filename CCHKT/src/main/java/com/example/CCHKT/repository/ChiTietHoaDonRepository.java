package com.example.CCHKT.repository;

import com.example.CCHKT.entity.ChiTietHoaDon;
import com.example.CCHKT.entity.ChiTietHoaDonId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChiTietHoaDonRepository extends JpaRepository<ChiTietHoaDon, ChiTietHoaDonId> {
}