package com.example.CCHKT.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "nhansu")
public class NhanSu {

    @Id
    @Column(name = "cccd", length = 20, nullable = false)
    private String cccd;

    @Column(name = "hoten", nullable = false)
    private String hoTen;

    @Column(name = "sdt", unique = true, length = 15, nullable = false)
    private String sdt;

    @Column(name = "taikhoan", unique = true, nullable = false)
    private String taiKhoan;

    @Column(name = "matkhau", nullable = false)
    private String matKhau;

    // Thêm cột role (mặc định hoặc quy ước: admin, nhanvien)
    @Column(name = "role", nullable = false, length = 20)
    private String role; // Ví dụ: "admin", "nhanvien"

    @Column(name = "trang_thai")
    private String trangThai;

    @Column(name = "so_lan_sai")
    private Integer soLanSai = 0;

    @Column(name = "khoa_den")
    private LocalDateTime khoaDen;

    public NhanSu() {}

    public NhanSu(String cccd, String hoTen, String sdt, String taiKhoan, String matKhau, String role) {
        this.cccd = cccd;
        this.hoTen = hoTen;
        this.sdt = sdt;
        this.taiKhoan = taiKhoan;
        this.matKhau = matKhau;
        this.role = role;
    }

    // Getters và Setters
    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getTaiKhoan() { return taiKhoan; }
    public void setTaiKhoan(String taiKhoan) { this.taiKhoan = taiKhoan; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public Integer getSoLanSai() { return soLanSai; }
    public void setSoLanSai(Integer soLanSai) { this.soLanSai = soLanSai; }

    public LocalDateTime getKhoaDen() { return khoaDen; }
    public void setKhoaDen(LocalDateTime khoaDen) { this.khoaDen = khoaDen; }
}
