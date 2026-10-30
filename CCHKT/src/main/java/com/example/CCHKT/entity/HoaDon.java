package com.example.CCHKT.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hoa_don")
@Getter
@Setter
@NoArgsConstructor
public class HoaDon {

    @Id
    @Column(name = "ma_hd", length = 20, nullable = false)
    private String maHd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sdt", nullable = true)
    private KhachHang khachHang;

    @Column(name = "ngay_dat", nullable = false)
    private LocalDateTime ngayDat = LocalDateTime.now();

    @Column(name = "tong_tien", precision = 18, scale = 2, nullable = false)
    private BigDecimal tongTien = BigDecimal.ZERO;

    @Column(name = "ma_voucher", length = 30)
    private String maVoucher;

    // Quan hệ chỉ đọc để tạo FK hoa_don.ma_voucher -> voucher.ma_voucher
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_voucher", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_hd_voucher"))
    private Voucher voucher;

    // FK hoa_don.cccd_nhan_vien -> nhansu.cccd (nullable)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cccd_nhan_vien", nullable = true, foreignKey = @ForeignKey(name = "FK_hd_nhansu"))
    private NhanSu nhanSu;

    @Column(name = "ten_nhan_vien", length = 100)
    private String tenNhanVien;

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ChiTietHoaDon> chiTietHoaDons = new ArrayList<>();
}