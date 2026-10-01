package com.example.CCHKT.controller;

import com.example.CCHKT.dto.request.PhieuNhapRequest;
import com.example.CCHKT.entity.*;
import com.example.CCHKT.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/phieunhap")
@CrossOrigin(origins = "*")
public class PhieuNhapController {

    @Autowired private PhieuNhapRepository phieuNhapRepository;
    @Autowired private NhaCungCapRepository nhaCungCapRepository;
    @Autowired private SanPhamRepository sanPhamRepository;

    // Danh sách phiếu nhập (mới nhất trước)
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAll() {
        List<Map<String, Object>> result = phieuNhapRepository.findAllByOrderByNgayNhapDesc()
                .stream().map(this::toSummary).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // Chi tiết 1 phiếu nhập
    @GetMapping("/{maPn}")
    public ResponseEntity<?> getByMa(@PathVariable String maPn) {
        return phieuNhapRepository.findById(maPn)
                .map(pn -> ResponseEntity.ok(toDetail(pn)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Lập phiếu nhập: lưu phiếu + cộng tồn kho
    // body: { "maNcc": "NCC-01", "tenNhanVien": "...", "items": [ { "maSp": "SKU1", "soLuong": 20 } ] }
    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@RequestBody PhieuNhapRequest req) {
        if (req.getMaNcc() == null || req.getMaNcc().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Thiếu nhà cung cấp"));
        }
        Optional<NhaCungCap> optNcc = nhaCungCapRepository.findById(req.getMaNcc());
        if (optNcc.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Không tìm thấy nhà cung cấp " + req.getMaNcc()));
        }
        NhaCungCap ncc = optNcc.get();
        if (!ncc.isHoatDong()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Nhà cung cấp đã ngừng giao dịch"));
        }
        if (req.getItems() == null || req.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Phiếu nhập chưa có sản phẩm nào"));
        }

        // Bước 1: kiểm tra toàn bộ, gộp các dòng trùng sản phẩm (chưa thay đổi dữ liệu)
        Map<String, Integer> gop = new LinkedHashMap<>();
        for (PhieuNhapRequest.Item item : req.getItems()) {
            if (item.getMaSp() == null || item.getSoLuong() == null || item.getSoLuong() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("message", "Dữ liệu sản phẩm trong phiếu không hợp lệ"));
            }
            gop.merge(item.getMaSp(), item.getSoLuong(), Integer::sum);
        }
        Map<String, SanPham> sanPhams = new LinkedHashMap<>();
        for (String maSp : gop.keySet()) {
            Optional<SanPham> optSp = sanPhamRepository.findById(maSp);
            if (optSp.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("message", "Không tìm thấy sản phẩm mã " + maSp));
            }
            sanPhams.put(maSp, optSp.get());
        }

        // Bước 2: tạo phiếu + cộng tồn kho
        PhieuNhap pn = new PhieuNhap();
        pn.setMaPn("PN" + System.currentTimeMillis());
        pn.setNhaCungCap(ncc);
        pn.setNgayNhap(LocalDateTime.now());
        pn.setTenNhanVien(req.getTenNhanVien());

        List<ChiTietPhieuNhap> chiTietList = new ArrayList<>();
        for (Map.Entry<String, Integer> e : gop.entrySet()) {
            SanPham sp = sanPhams.get(e.getKey());
            int tonCu = sp.getSoLuong() == null ? 0 : sp.getSoLuong();
            sp.setSoLuong(tonCu + e.getValue());
            sanPhamRepository.save(sp);

            ChiTietPhieuNhap ct = new ChiTietPhieuNhap();
            ChiTietPhieuNhapId id = new ChiTietPhieuNhapId();
            id.setMaPn(pn.getMaPn());
            id.setMaSp(sp.getMaSp());
            ct.setId(id);
            ct.setPhieuNhap(pn);
            ct.setSanPham(sp);
            ct.setSoLuong(e.getValue());
            chiTietList.add(ct);
        }
        pn.setChiTietPhieuNhaps(chiTietList);
        PhieuNhap saved = phieuNhapRepository.save(pn);

        return ResponseEntity.status(201).body(toDetail(saved));
    }

    private int tongSoLuong(PhieuNhap pn) {
        return pn.getChiTietPhieuNhaps() == null ? 0
                : pn.getChiTietPhieuNhaps().stream().mapToInt(ChiTietPhieuNhap::getSoLuong).sum();
    }

    private Map<String, Object> toSummary(PhieuNhap pn) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("maPn", pn.getMaPn());
        m.put("ngayNhap", pn.getNgayNhap());
        m.put("tenNhanVien", pn.getTenNhanVien());
        m.put("maNcc", pn.getNhaCungCap().getMaNcc());
        m.put("tenNcc", pn.getNhaCungCap().getTenNcc());
        m.put("soMatHang", pn.getChiTietPhieuNhaps() == null ? 0 : pn.getChiTietPhieuNhaps().size());
        m.put("tongSoLuong", tongSoLuong(pn));
        return m;
    }

    private Map<String, Object> toDetail(PhieuNhap pn) {
        Map<String, Object> m = toSummary(pn);
        List<Map<String, Object>> items = new ArrayList<>();
        if (pn.getChiTietPhieuNhaps() != null) {
            for (ChiTietPhieuNhap ct : pn.getChiTietPhieuNhaps()) {
                Map<String, Object> it = new LinkedHashMap<>();
                it.put("maSp", ct.getSanPham() != null ? ct.getSanPham().getMaSp() : ct.getId().getMaSp());
                it.put("tenSp", ct.getSanPham() != null ? ct.getSanPham().getTenSp() : "");
                it.put("soLuong", ct.getSoLuong());
                items.add(it);
            }
        }
        m.put("items", items);
        return m;
    }
}
