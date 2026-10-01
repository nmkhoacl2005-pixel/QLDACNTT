package com.example.CCHKT.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "phieu_nhap")
@Getter
@Setter
@NoArgsConstructor
public class PhieuNhap {

    @Id
    @Column(name = "ma_pn", length = 20, nullable = false)
    private String maPn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_ncc", nullable = false)
    private NhaCungCap nhaCungCap;

    @Column(name = "ngay_nhap", nullable = false)
    private LocalDateTime ngayNhap = LocalDateTime.now();

    @Column(name = "ten_nhan_vien", length = 100)
    private String tenNhanVien;

    @OneToMany(mappedBy = "phieuNhap", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ChiTietPhieuNhap> chiTietPhieuNhaps = new ArrayList<>();
}
