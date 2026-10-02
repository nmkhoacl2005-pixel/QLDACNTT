package com.example.CCHKT.controller;

import com.example.CCHKT.entity.NhaCungCap;
import com.example.CCHKT.repository.NhaCungCapRepository;
import com.example.CCHKT.repository.PhieuNhapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/nhacungcap")
@CrossOrigin(origins = "*")
public class NhaCungCapController {

    @Autowired private NhaCungCapRepository nhaCungCapRepository;
    @Autowired private PhieuNhapRepository phieuNhapRepository;

    // Danh sách nhà cung cấp (kèm số phiếu nhập đã lập)
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAll() {
        List<Map<String, Object>> result = nhaCungCapRepository.findAll().stream()
                .sorted(Comparator.comparing(NhaCungCap::getMaNcc))
                .map(this::toMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{maNcc}")
    public ResponseEntity<?> getByMa(@PathVariable String maNcc) {
        return nhaCungCapRepository.findById(maNcc)
                .map(n -> ResponseEntity.ok(toMap(n)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Thêm nhà cung cấp mới
    @PostMapping
    public ResponseEntity<?> create(@RequestBody NhaCungCap ncc) {
        if (ncc.getMaNcc() == null || ncc.getMaNcc().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Thiếu mã nhà cung cấp"));
        }
        if (ncc.getTenNcc() == null || ncc.getTenNcc().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Thiếu tên nhà cung cấp"));
        }
        if (nhaCungCapRepository.existsById(ncc.getMaNcc())) {
            return ResponseEntity.status(409).body(Map.of("message", "Mã nhà cung cấp đã tồn tại trong hệ thống"));
        }
        if (ncc.getTrangThai() == null || ncc.getTrangThai().isBlank()) ncc.setTrangThai(NhaCungCap.DANG_GIAO_DICH);
        if (!hopLe(ncc.getTrangThai())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Trạng thái không hợp lệ"));
        }
        NhaCungCap saved = nhaCungCapRepository.save(ncc);
        return ResponseEntity.status(201).body(toMap(saved));
    }

    // Cập nhật (tên, người liên hệ, SĐT, trạng thái)
    @PutMapping("/{maNcc}")
    public ResponseEntity<?> update(@PathVariable String maNcc, @RequestBody Map<String, Object> body) {
        Optional<NhaCungCap> opt = nhaCungCapRepository.findById(maNcc);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        NhaCungCap n = opt.get();

        if (body.containsKey("tenNcc")) {
            String ten = String.valueOf(body.get("tenNcc")).trim();
            if (ten.isEmpty()) return ResponseEntity.badRequest().body(Map.of("message", "Tên nhà cung cấp không được để trống"));
            n.setTenNcc(ten);
        }
        if (body.containsKey("nguoiLienHe")) n.setNguoiLienHe(body.get("nguoiLienHe") == null ? null : String.valueOf(body.get("nguoiLienHe")));
        if (body.containsKey("sdt")) n.setSdt(body.get("sdt") == null ? null : String.valueOf(body.get("sdt")));
        if (body.get("trangThai") != null) {
            String tt = String.valueOf(body.get("trangThai"));
            if (!hopLe(tt)) return ResponseEntity.badRequest().body(Map.of("message", "Trạng thái không hợp lệ"));
            n.setTrangThai(tt);
        }
        return ResponseEntity.ok(toMap(nhaCungCapRepository.save(n)));
    }

    // Xóa nhà cung cấp (không xóa được nếu đã có phiếu nhập)
    @DeleteMapping("/{maNcc}")
    public ResponseEntity<?> delete(@PathVariable String maNcc) {
        if (!nhaCungCapRepository.existsById(maNcc)) return ResponseEntity.notFound().build();
        if (phieuNhapRepository.countByNhaCungCap_MaNcc(maNcc) > 0) {
            return ResponseEntity.status(409).body(Map.of("message",
                    "Nhà cung cấp đã có phiếu nhập, không thể xóa. Hãy chuyển sang 'Ngừng giao dịch'"));
        }
        nhaCungCapRepository.deleteById(maNcc);
        return ResponseEntity.ok(Map.of("message", "Đã xóa nhà cung cấp thành công"));
    }

    private boolean hopLe(String tt) {
        return NhaCungCap.DANG_GIAO_DICH.equals(tt) || NhaCungCap.NGUNG_GIAO_DICH.equals(tt);
    }

    private Map<String, Object> toMap(NhaCungCap n) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("maNcc", n.getMaNcc());
        m.put("tenNcc", n.getTenNcc());
        m.put("nguoiLienHe", n.getNguoiLienHe());
        m.put("sdt", n.getSdt());
        m.put("trangThai", n.isHoatDong() ? NhaCungCap.DANG_GIAO_DICH : NhaCungCap.NGUNG_GIAO_DICH);
        m.put("soPhieuNhap", phieuNhapRepository.countByNhaCungCap_MaNcc(n.getMaNcc()));
        return m;
    }
}
