package com.example.CCHKT.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "chi_tiet_hoa_don")
@Getter
@Setter
@NoArgsConstructor
public class ChiTietHoaDon {

    @EmbeddedId
    private ChiTietHoaDonId id = new ChiTietHoaDonId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maHd")
    @JoinColumn(name = "ma_hd", foreignKey = @ForeignKey(name = "FK_cthd_hd"))
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maSp")
    @JoinColumn(name = "ma_sp", foreignKey = @ForeignKey(name = "FK_cthd_sp"))
    private SanPham sanPham;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "don_gia_ban", precision = 18, scale = 2, nullable = false)
    private BigDecimal donGiaBan;
}