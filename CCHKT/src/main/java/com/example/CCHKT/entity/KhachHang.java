package com.example.CCHKT.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "khach_hang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhachHang {

    @Id
    @Column(name = "sdt", length = 12, nullable = false)
    private String sdt;

    @Column(name = "ten", length = 100, nullable = false)
    private String ten;

    @Column(name = "diem", nullable = false)
    private Integer diem = 0;

    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HoaDon> hoaDons = new ArrayList<>();
}
