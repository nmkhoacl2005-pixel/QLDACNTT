package com.example.CCHKT.controller;

import com.example.CCHKT.dto.request.HoaDonRequest;
import com.example.CCHKT.entity.*;
import com.example.CCHKT.repository.*;
import com.example.CCHKT.util.HangThanhVien;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hoadon")
@CrossOrigin(origins = "*")
public class HoaDonController {

    @Autowired private HoaDonRepository hoaDonRepository;
    @Autowired private ChiTietHoaDonRepository chiTietHoaDonRepository;
    @Autowired private SanPhamRepository sanPhamRepository;
    @Autowired private KhachHangRepository khachHangRepository;
    @Autowired private VoucherRepository voucherRepository;

    // Danh sách hóa đơn (mới nhất trước)
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAll() {
        List<Map<String, Object>> result = hoaDonRepository.findAllByOrderByNgayDatDesc()
                .stream().map(this::toSummary).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // Chi tiết 1 hóa đơn
    @GetMapping("/{maHd}")
    public ResponseEntity<?> getByMaHd(@PathVariable String maHd) {
        return hoaDonRepository.findById(maHd)
                .map(hd -> ResponseEntity.ok(toDetail(hd)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Tạo hóa đơn mới (chốt đơn tại quầy POS)
    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@RequestBody HoaDonRequest req) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Giỏ hàng đang trống"));
        }

        // Khách hàng (tùy chọn - có thể là khách lẻ không có SĐT)
        KhachHang khachHang = null;
        if (req.getSdt() != null && !req.getSdt().isBlank()) {
            Optional<KhachHang> opt = khachHangRepository.findById(req.getSdt());
            if (opt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("message", "Không tìm thấy khách hàng với SĐT " + req.getSdt()));
            }
            khachHang = opt.get();
        }

        // Kiểm tra & khóa từng sản phẩm, tính tổng tiền hàng gốc
        List<ChiTietHoaDon> chiTietList = new ArrayList<>();
        BigDecimal tongTienGoc = BigDecimal.ZERO;
        for (HoaDonRequest.Item item : req.getItems()) {
            if (item.getMaSp() == null || item.getSoLuong() == null || item.getSoLuong() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("message", "Dữ liệu sản phẩm trong giỏ hàng không hợp lệ"));
            }
            Optional<SanPham> optSp = sanPhamRepository.findById(item.getMaSp());
            if (optSp.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("message", "Không tìm thấy sản phẩm mã " + item.getMaSp()));
            }
            SanPham sp = optSp.get();
            if (sp.getSoLuong() == null || sp.getSoLuong() < item.getSoLuong()) {
                return ResponseEntity.status(400).body(Map.of("message",
                        "Sản phẩm '" + sp.getTenSp() + "' không đủ tồn kho (còn " + (sp.getSoLuong() == null ? 0 : sp.getSoLuong()) + ")"));
            }

            ChiTietHoaDon ct = new ChiTietHoaDon();
            ChiTietHoaDonId id = new ChiTietHoaDonId();
            id.setMaSp(sp.getMaSp());
            ct.setId(id);
            ct.setSanPham(sp);
            ct.setSoLuong(item.getSoLuong());
            ct.setDonGiaBan(sp.getDonGia());
            chiTietList.add(ct);

            tongTienGoc = tongTienGoc.add(sp.getDonGia().multiply(BigDecimal.valueOf(item.getSoLuong())));

            // Trừ tồn kho
            sp.setSoLuong(sp.getSoLuong() - item.getSoLuong());
            sanPhamRepository.save(sp);
        }

        // Áp dụng voucher (nếu có)
        BigDecimal tongTienFinal = tongTienGoc;
        Voucher voucher = null;
        if (req.getMaVoucher() != null && !req.getMaVoucher().isBlank()) {
            if (khachHang == null) {
                return ResponseEntity.status(400).body(Map.of("message", "Cần chọn khách hàng thành viên để áp dụng voucher"));
            }
            Optional<Voucher> optV = voucherRepository.findById(req.getMaVoucher());
            if (optV.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("message", "Không tìm thấy voucher " + req.getMaVoucher()));
            }
            voucher = optV.get();
            if (!voucher.getSdt().equals(khachHang.getSdt())) {
                return ResponseEntity.status(400).body(Map.of("message", "Voucher này không thuộc về khách hàng đã chọn"));
            }
            if (Boolean.TRUE.equals(voucher.getDaSuDung())) {
                return ResponseEntity.status(400).body(Map.of("message", "Voucher đã được sử dụng trước đó"));
            }
            BigDecimal giam = tongTienGoc.multiply(BigDecimal.valueOf(voucher.getPhanTramGiam()))
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
            tongTienFinal = tongTienGoc.subtract(giam);
            voucher.setDaSuDung(true);
            voucher.setNgaySuDung(LocalDateTime.now());
            voucherRepository.save(voucher);
        }

        // Tạo hóa đơn
        HoaDon hd = new HoaDon();
        hd.setMaHd("HD" + System.currentTimeMillis());
        hd.setKhachHang(khachHang);
        hd.setNgayDat(LocalDateTime.now());
        hd.setTongTien(tongTienFinal);
        hd.setMaVoucher(voucher != null ? voucher.getMaVoucher() : null);
        hd.setTenNhanVien(req.getTenNhanVien());

        for (ChiTietHoaDon ct : chiTietList) {
            ct.getId().setMaHd(hd.getMaHd());
            ct.setHoaDon(hd);
        }
        hd.setChiTietHoaDons(chiTietList);
        HoaDon saved = hoaDonRepository.save(hd);

        // Tích điểm cho khách hàng (50.000đ = 1 điểm), tính trên số tiền thực trả
        int diemCong = 0;
        if (khachHang != null) {
            diemCong = tongTienFinal.divide(BigDecimal.valueOf(50000), 0, RoundingMode.DOWN).intValue();
            int diemHienTai = khachHang.getDiem() == null ? 0 : khachHang.getDiem();
            khachHang.setDiem(diemHienTai + diemCong);
            khachHangRepository.save(khachHang);
        }

        Map<String, Object> response = toDetail(saved);
        response.put("diemVuaTichDuoc", diemCong);
        return ResponseEntity.status(201).body(response);
    }

    private Map<String, Object> toSummary(HoaDon hd) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("maHd", hd.getMaHd());
        map.put("ngayDat", hd.getNgayDat());
        map.put("tongTien", hd.getTongTien());
        map.put("tenNhanVien", hd.getTenNhanVien());
        map.put("soLuongMatHang", hd.getChiTietHoaDons() != null ? hd.getChiTietHoaDons().size() : 0);
        if (hd.getKhachHang() != null) {
            map.put("khachHangSdt", hd.getKhachHang().getSdt());
            map.put("khachHangTen", hd.getKhachHang().getTen());
        }
        return map;
    }

    private Map<String, Object> toDetail(HoaDon hd) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("maHd", hd.getMaHd());
        map.put("ngayDat", hd.getNgayDat());
        map.put("tongTien", hd.getTongTien());
        map.put("maVoucher", hd.getMaVoucher());
        map.put("tenNhanVien", hd.getTenNhanVien());

        BigDecimal tongTienGoc = BigDecimal.ZERO;
        List<Map<String, Object>> items = new ArrayList<>();
        if (hd.getChiTietHoaDons() != null) {
            for (ChiTietHoaDon ct : hd.getChiTietHoaDons()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("maSp", ct.getSanPham() != null ? ct.getSanPham().getMaSp() : ct.getId().getMaSp());
                item.put("tenSp", ct.getSanPham() != null ? ct.getSanPham().getTenSp() : "");
                item.put("soLuong", ct.getSoLuong());
                item.put("donGiaBan", ct.getDonGiaBan());
                BigDecimal thanhTien = ct.getDonGiaBan().multiply(BigDecimal.valueOf(ct.getSoLuong()));
                item.put("thanhTien", thanhTien);
                tongTienGoc = tongTienGoc.add(thanhTien);
                items.add(item);
            }
        }
        map.put("items", items);
        map.put("tongTienGoc", tongTienGoc);
        map.put("giamGia", tongTienGoc.subtract(hd.getTongTien()));

        if (hd.getKhachHang() != null) {
            KhachHang kh = hd.getKhachHang();
            int diem = kh.getDiem() == null ? 0 : kh.getDiem();
            Map<String, Object> khMap = new LinkedHashMap<>();
            khMap.put("sdt", kh.getSdt());
            khMap.put("ten", kh.getTen());
            khMap.put("diem", diem);
            khMap.put("hang", HangThanhVien.tinhHang(diem));
            map.put("khachHang", khMap);
        } else {
            map.put("khachHang", null);
        }
        return map;
    }
}