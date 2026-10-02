package com.example.CCHKT.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nha_cung_cap")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhaCungCap {

    // Lưu mã không dấu (ASCII) để không bị hỏng khi cột SQL Server là varchar
    public static final String DANG_GIAO_DICH = "HOAT_DONG";
    public static final String NGUNG_GIAO_DICH = "NGUNG";

    @Id
    @Column(name = "ma_ncc", length = 20, nullable = false)
    private String maNcc;

    @Column(name = "ten_ncc", length = 200, nullable = false)
    private String tenNcc;

    @Column(name = "nguoi_lien_he", length = 100)
    private String nguoiLienHe;

    @Column(name = "sdt", length = 12)
    private String sdt;

    @Column(name = "trang_thai", length = 30, nullable = false)
    private String trangThai = DANG_GIAO_DICH;

    // Chỉ "NGUNG" mới là ngừng giao dịch; mọi giá trị khác (kể cả dữ liệu cũ) coi là đang giao dịch
    @JsonIgnore
    public boolean isHoatDong() {
        return !NGUNG_GIAO_DICH.equals(trangThai);
    }
}
