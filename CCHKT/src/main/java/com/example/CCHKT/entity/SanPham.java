
package com.example.CCHKT.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "san_pham")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SanPham {

    @Id
    @Column(name = "ma_sp", length = 20, nullable = false)
    private String maSp;

    @Column(name = "ten_sp", length = 200, nullable = false)
    private String tenSp;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "don_gia", precision = 18, scale = 2, nullable = false)
    private BigDecimal donGia;

    @JsonIgnore
    @OneToMany(
        mappedBy = "sanPham",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY
    )
    private List<ChiTietHoaDon> chiTietHoaDons = new ArrayList<>();
}