package com.example.CCHKT.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chi_tiet_phieu_nhap")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietPhieuNhap {

    @EmbeddedId
    private ChiTietPhieuNhapId id = new ChiTietPhieuNhapId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maPn")
    @JoinColumn(name = "ma_pn")
    private PhieuNhap phieuNhap;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maSp")
    @JoinColumn(name = "ma_sp")
    private SanPham sanPham;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;
}
