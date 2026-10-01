package com.example.CCHKT.controller;

import com.example.CCHKT.entity.SanPham;
import com.example.CCHKT.repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sanpham")
@CrossOrigin(origins = "*")
public class SanPhamController {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    // Lấy toàn bộ danh sách sản phẩm
    @GetMapping
    public ResponseEntity<List<SanPham>> getAll() {
        return ResponseEntity.ok(sanPhamRepository.findAll());
    }

    // Lấy chi tiết 1 sản phẩm theo mã
    @GetMapping("/{maSp}")
    public ResponseEntity<?> getByMaSp(@PathVariable String maSp) {
        return sanPhamRepository.findById(maSp)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Thêm sản phẩm mới
    @PostMapping
    public ResponseEntity<?> create(@RequestBody SanPham sanPham) {
        if (sanPham.getMaSp() == null || sanPham.getMaSp().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Thiếu mã sản phẩm"));
        }
        if (sanPhamRepository.existsById(sanPham.getMaSp())) {
            return ResponseEntity.status(409).body(Map.of("message", "Mã sản phẩm đã tồn tại trong hệ thống"));
        }
        if (sanPham.getSoLuong() == null) sanPham.setSoLuong(0);
        if (sanPham.getDonGia() == null) sanPham.setDonGia(BigDecimal.ZERO);
        SanPham saved = sanPhamRepository.save(sanPham);
        return ResponseEntity.status(201).body(saved);
    }

    // Cập nhật sản phẩm (tên, số lượng tồn, đơn giá)
    @PutMapping("/{maSp}")
    public ResponseEntity<?> update(@PathVariable String maSp, @RequestBody Map<String, Object> body) {
        return sanPhamRepository.findById(maSp).map(sp -> {
            if (body.get("tenSp") != null) sp.setTenSp(String.valueOf(body.get("tenSp")));
            if (body.get("soLuong") != null) sp.setSoLuong(Integer.parseInt(String.valueOf(body.get("soLuong"))));
            if (body.get("donGia") != null) sp.setDonGia(new BigDecimal(String.valueOf(body.get("donGia"))));
            SanPham saved = sanPhamRepository.save(sp);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    // Xóa sản phẩm
    @DeleteMapping("/{maSp}")
    public ResponseEntity<?> delete(@PathVariable String maSp) {
        if (!sanPhamRepository.existsById(maSp)) return ResponseEntity.notFound().build();
        sanPhamRepository.deleteById(maSp);
        return ResponseEntity.ok(Map.of("message", "Đã xóa sản phẩm thành công"));
    }
}