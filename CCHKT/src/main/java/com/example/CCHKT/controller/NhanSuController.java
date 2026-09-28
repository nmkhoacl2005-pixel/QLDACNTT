package com.example.CCHKT.controller;

import com.example.CCHKT.entity.NhanSu;
import com.example.CCHKT.repository.NhanSuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/nhansu")
@CrossOrigin(origins = "*")
public class NhanSuController {

    @Autowired
    private NhanSuRepository nhanSuRepository;

    // Lấy toàn bộ danh sách
    @GetMapping
    public ResponseEntity<List<NhanSu>> getAll() {
        return ResponseEntity.ok(nhanSuRepository.findAll());
    }

    // Lấy chi tiết nhân sự theo CCCD
    @GetMapping("/{cccd}")
    public ResponseEntity<?> getByCccd(@PathVariable String cccd) {
        return nhanSuRepository.findById(cccd)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Cập nhật trạng thái theo CCCD
    @PutMapping("/{cccd}/trang-thai")
    public ResponseEntity<?> updateTrangThai(@PathVariable String cccd, @RequestBody Map<String, String> body) {
        return nhanSuRepository.findById(cccd).map(ns -> {
            ns.setTrangThai(body.get("trangThai"));
            nhanSuRepository.save(ns);
            return ResponseEntity.ok(Map.of("message", "Cập nhật trạng thái thành công"));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Thêm mới nhân sự
    @PostMapping
    public ResponseEntity<?> create(@RequestBody NhanSu nhanSu) {
        if (nhanSuRepository.existsById(nhanSu.getCccd())) {
            return ResponseEntity.status(409).body(Map.of("message", "Số CCCD đã tồn tại trong hệ thống"));
        }
        NhanSu saved = nhanSuRepository.save(nhanSu);
        return ResponseEntity.status(201).body(saved);
    }

    // Cập nhật tài khoản / mật khẩu / trạng thái theo CCCD
    @PutMapping("/{cccd}")
    public ResponseEntity<?> update(@PathVariable String cccd, @RequestBody Map<String, String> body) {
        return nhanSuRepository.findById(cccd).map(ns -> {
            if (body.get("taiKhoan") != null) ns.setTaiKhoan(body.get("taiKhoan"));
            if (body.get("matKhau") != null && !body.get("matKhau").isBlank()) ns.setMatKhau(body.get("matKhau"));
            if (body.get("trangThai") != null) ns.setTrangThai(body.get("trangThai"));
            nhanSuRepository.save(ns);
            return ResponseEntity.ok(Map.of("message", "Cập nhật thông tin thành công"));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Xóa nhân sự theo CCCD
    @DeleteMapping("/{cccd}")
    public ResponseEntity<?> delete(@PathVariable String cccd) {
        if (!nhanSuRepository.existsById(cccd)) {
            return ResponseEntity.notFound().build();
        }
        nhanSuRepository.deleteById(cccd);
        return ResponseEntity.ok(Map.of("message", "Đã xóa nhân sự thành công"));
    }
}