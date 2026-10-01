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
public class ChiTietPhieuNhapId implements Serializable {

    @Column(name = "ma_pn", length = 20)
    private String maPn;

    @Column(name = "ma_sp", length = 20)
    private String maSp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietPhieuNhapId that = (ChiTietPhieuNhapId) o;
        return Objects.equals(maPn, that.maPn) && Objects.equals(maSp, that.maSp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPn, maSp);
    }
}
