package com.example.CCHKT.dto.request;

import java.util.List;

public class HoaDonRequest {
    private String sdt;              // SĐT khách hàng (có thể null/"" = khách lẻ)
    private String tenNhanVien;      // Tên thu ngân đang đăng nhập
    private String maVoucher;        // Mã voucher áp dụng (tùy chọn)
    private List<Item> items;

    public static class Item {
        private String maSp;
        private Integer soLuong;

        public String getMaSp() { return maSp; }
        public void setMaSp(String maSp) { this.maSp = maSp; }
        public Integer getSoLuong() { return soLuong; }
        public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
    }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public String getTenNhanVien() { return tenNhanVien; }
    public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien; }
    public String getMaVoucher() { return maVoucher; }
    public void setMaVoucher(String maVoucher) { this.maVoucher = maVoucher; }
    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }
}