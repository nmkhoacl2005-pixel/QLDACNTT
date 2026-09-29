package com.example.CCHKT.util;

import java.util.List;

/**
 * Quy tắc hạng thành viên & đổi voucher theo điểm tích lũy:
 * - Chi 50.000đ  = 1 điểm
 * - Điểm > 20   => hạng Đồng   (đổi được voucher 10%)
 * - Điểm > 50   => hạng Bạc    (đổi được voucher 10%, 20%)
 * - Điểm > 100  => hạng Vàng   (đổi được voucher 10%, 20%, 30%)
 * - Đổi voucher 10% tốn 5 điểm, 20% tốn 8 điểm, 30% tốn 10 điểm
 */
public final class HangThanhVien {

    public static final String CHUA_XEP_HANG = "Chưa xếp hạng";
    public static final String DONG = "Đồng";
    public static final String BAC = "Bạc";
    public static final String VANG = "Vàng";

    private HangThanhVien() {}

    public static String tinhHang(int diem) {
        if (diem > 100) return VANG;
        if (diem > 50) return BAC;
        if (diem > 20) return DONG;
        return CHUA_XEP_HANG;
    }

    public static List<Integer> voucherKhaDung(String hang) {
        return switch (hang) {
            case VANG -> List.of(10, 20, 30);
            case BAC -> List.of(10, 20);
            case DONG -> List.of(10);
            default -> List.of();
        };
    }

    /** Số điểm cần để đổi voucher theo phần trăm giảm; trả -1 nếu phần trăm không hợp lệ. */
    public static int diemCanDoi(int phanTramGiam) {
        return switch (phanTramGiam) {
            case 10 -> 5;
            case 20 -> 8;
            case 30 -> 10;
            default -> -1;
        };
    }
}
