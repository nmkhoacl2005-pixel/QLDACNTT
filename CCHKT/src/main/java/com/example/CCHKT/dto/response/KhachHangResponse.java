package com.example.CCHKT.dto.response;

import java.util.List;

public class KhachHangResponse {
    private String sdt;
    private String ten;
    private Integer diem;
    private String hang;              // Chưa xếp hạng / Đồng / Bạc / Vàng
    private List<Integer> voucherKhaDung; // Ví dụ: [10, 20]

    public KhachHangResponse() {}

    public KhachHangResponse(String sdt, String ten, Integer diem, String hang, List<Integer> voucherKhaDung) {
        this.sdt = sdt;
        this.ten = ten;
        this.diem = diem;
        this.hang = hang;
        this.voucherKhaDung = voucherKhaDung;
    }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    public Integer getDiem() { return diem; }
    public void setDiem(Integer diem) { this.diem = diem; }

    public String getHang() { return hang; }
    public void setHang(String hang) { this.hang = hang; }

    public List<Integer> getVoucherKhaDung() { return voucherKhaDung; }
    public void setVoucherKhaDung(List<Integer> voucherKhaDung) { this.voucherKhaDung = voucherKhaDung; }
}
