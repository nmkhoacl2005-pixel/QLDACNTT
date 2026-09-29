package com.example.CCHKT.controller;

import com.example.CCHKT.dto.response.KhachHangResponse;
import com.example.CCHKT.entity.KhachHang;
import com.example.CCHKT.entity.Voucher;
import com.example.CCHKT.repository.KhachHangRepository;
import com.example.CCHKT.repository.VoucherRepository;
import com.example.CCHKT.util.HangThanhVien;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/khachhang")
@CrossOrigin(origins = "*")
public class KhachHangController {

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    // Lấy toàn bộ danh sách khách hàng (kèm hạng, voucher khả dụng)
    @GetMapping
    public ResponseEntity<List<KhachHangResponse>> getAll() {
        List<KhachHangResponse> result = khachHangRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // Lấy chi tiết 1 khách hàng theo SĐT
    @GetMapping("/{sdt}")
    public ResponseEntity<?> getBySdt(@PathVariable String sdt) {
        return khachHangRepository.findById(sdt)
                .map(kh -> ResponseEntity.ok(toResponse(kh)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Thêm khách hàng mới
    @PostMapping
    public ResponseEntity<?> create(@RequestBody KhachHang khachHang) {
        if (khachHang.getSdt() == null || khachHang.getSdt().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Thiếu số điện thoại"));
        }
        if (khachHangRepository.existsById(khachHang.getSdt())) {
            return ResponseEntity.status(409).body(Map.of("message", "Số điện thoại đã tồn tại trong hệ thống"));
        }
        if (khachHang.getDiem() == null) khachHang.setDiem(0);
        KhachHang saved = khachHangRepository.save(khachHang);
        return ResponseEntity.status(201).body(toResponse(saved));
    }

    // Cộng điểm khi thanh toán hóa đơn: cứ 50.000đ = 1 điểm
    // body: { "tongTien": 640000 }
    @PostMapping("/{sdt}/tich-diem")
    public ResponseEntity<?> tichDiem(@PathVariable String sdt, @RequestBody Map<String, Object> body) {
        Optional<KhachHang> opt = khachHangRepository.findById(sdt);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        KhachHang kh = opt.get();

        double tongTien;
        try {
            tongTien = Double.parseDouble(String.valueOf(body.get("tongTien")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "tongTien không hợp lệ"));
        }

        int diemCong = (int) (tongTien / 50000);
        int diemHienTai = kh.getDiem() == null ? 0 : kh.getDiem();
        kh.setDiem(diemHienTai + diemCong);
        khachHangRepository.save(kh);

        return ResponseEntity.ok(Map.of(
                "message", "Đã cộng " + diemCong + " điểm",
                "diemCong", diemCong,
                "khachHang", toResponse(kh)
        ));
    }

    // Đổi điểm lấy voucher giảm giá
    // body: { "phanTramGiam": 10 }  (10 | 20 | 30)
    @PostMapping("/{sdt}/doi-voucher")
    public ResponseEntity<?> doiVoucher(@PathVariable String sdt, @RequestBody Map<String, Object> body) {
        Optional<KhachHang> opt = khachHangRepository.findById(sdt);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        KhachHang kh = opt.get();

        int phanTram;
        try {
            phanTram = Integer.parseInt(String.valueOf(body.get("phanTramGiam")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "phanTramGiam không hợp lệ"));
        }

        int diemHienTai = kh.getDiem() == null ? 0 : kh.getDiem();
        String hang = HangThanhVien.tinhHang(diemHienTai);
        List<Integer> khaDung = HangThanhVien.voucherKhaDung(hang);

        if (!khaDung.contains(phanTram)) {
            return ResponseEntity.status(400).body(Map.of(
                    "message", "Hạng " + hang + " không được đổi voucher giảm " + phanTram + "%"));
        }

        int diemCan = HangThanhVien.diemCanDoi(phanTram);
        if (diemHienTai < diemCan) {
            return ResponseEntity.status(400).body(Map.of(
                    "message", "Không đủ điểm để đổi. Cần " + diemCan + " điểm, hiện có " + diemHienTai + " điểm"));
        }

        kh.setDiem(diemHienTai - diemCan);
        khachHangRepository.save(kh);

        Voucher voucher = new Voucher();
        voucher.setMaVoucher("VC" + System.currentTimeMillis());
        voucher.setSdt(sdt);
        voucher.setPhanTramGiam(phanTram);
        voucher.setDiemDaDoi(diemCan);
        voucher.setNgayTao(LocalDateTime.now());
        voucher.setDaSuDung(false);
        voucherRepository.save(voucher);

        return ResponseEntity.ok(Map.of(
                "message", "Đổi voucher giảm " + phanTram + "% thành công (-" + diemCan + " điểm)",
                "voucher", voucher,
                "diemConLai", kh.getDiem(),
                "hang", hang
        ));
    }

    // Danh sách voucher của 1 khách hàng
    @GetMapping("/{sdt}/voucher")
    public ResponseEntity<?> getVouchers(@PathVariable String sdt) {
        if (!khachHangRepository.existsById(sdt)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(voucherRepository.findBySdtOrderByNgayTaoDesc(sdt));
    }

    // Xóa khách hàng
    @DeleteMapping("/{sdt}")
    public ResponseEntity<?> delete(@PathVariable String sdt) {
        if (!khachHangRepository.existsById(sdt)) return ResponseEntity.notFound().build();
        khachHangRepository.deleteById(sdt);
        return ResponseEntity.ok(Map.of("message", "Đã xóa khách hàng thành công"));
    }

    private KhachHangResponse toResponse(KhachHang kh) {
        int diem = kh.getDiem() == null ? 0 : kh.getDiem();
        String hang = HangThanhVien.tinhHang(diem);
        return new KhachHangResponse(kh.getSdt(), kh.getTen(), diem, hang, HangThanhVien.voucherKhaDung(hang));
    }
}
