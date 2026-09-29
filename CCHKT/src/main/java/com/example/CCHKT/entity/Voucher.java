package com.example.CCHKT.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "voucher")
@Getter
@Setter
@NoArgsConstructor
public class Voucher {

    @Id
    @Column(name = "ma_voucher", length = 30, nullable = false)
    private String maVoucher;

    // Lưu trực tiếp SĐT (không map quan hệ) để tránh lỗi lazy-loading khi trả JSON
    @Column(name = "sdt", length = 12, nullable = false)
    private String sdt;

    // 10, 20 hoặc 30 (%)
    @Column(name = "phan_tram_giam", nullable = false)
    private Integer phanTramGiam;

    // Số điểm đã trừ để đổi voucher này (5 / 8 / 10)
    @Column(name = "diem_da_doi", nullable = false)
    private Integer diemDaDoi;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(name = "da_su_dung", nullable = false)
    private Boolean daSuDung = false;

    @Column(name = "ngay_su_dung")
    private LocalDateTime ngaySuDung;
}
