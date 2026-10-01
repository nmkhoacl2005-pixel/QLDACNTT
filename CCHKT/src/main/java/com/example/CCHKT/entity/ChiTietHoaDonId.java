package com.example.CCHKT.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietHoaDonId implements Serializable {

    @Column(name = "ma_hd", length = 20)
    private String maHd;

    @Column(name = "ma_sp", length = 20)
    private String maSp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietHoaDonId that = (ChiTietHoaDonId) o;
        return Objects.equals(maHd, that.maHd) && Objects.equals(maSp, that.maSp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHd, maSp);
    }
}