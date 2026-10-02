package com.example.CCHKT.dto.request;

import java.util.List;

public class PhieuNhapRequest {
    private String maNcc;            // Mã nhà cung cấp
    private String tenNhanVien;      // Người lập phiếu (tài khoản đang đăng nhập)
    private List<Item> items;

    public static class Item {
        private String maSp;
        private Integer soLuong;

        public String getMaSp() { return maSp; }
        public void setMaSp(String maSp) { this.maSp = maSp; }
        public Integer getSoLuong() { return soLuong; }
        public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
    }

    public String getMaNcc() { return maNcc; }
    public void setMaNcc(String maNcc) { this.maNcc = maNcc; }
    public String getTenNhanVien() { return tenNhanVien; }
    public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien; }
    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }
}
