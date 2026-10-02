package com.example.CCHKT.controller;

import com.example.CCHKT.dto.request.LoginRequest;
import com.example.CCHKT.dto.response.LoginResponse;
import com.example.CCHKT.entity.NhanSu;
import com.example.CCHKT.repository.NhanSuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Duration;
import java.time.LocalDateTime;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private NhanSuRepository nhanSuRepository;

        private static final int MAX_ATTEMPTS = 3;
        private static final int LOCK_SECONDS = 30;

        @PostMapping("/login")
        public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
            Optional<NhanSu> optionalNhanSu = nhanSuRepository.findByTaiKhoan(request.getUsername());
            if (optionalNhanSu.isEmpty()) {
                return fail(HttpStatus.UNAUTHORIZED, "Tài khoản hoặc mật khẩu không chính xác!");
            }
            NhanSu user = optionalNhanSu.get();

            // Tài khoản bị admin khóa (inactive)
            if ("inactive".equalsIgnoreCase(user.getTrangThai())) {
                return fail(HttpStatus.FORBIDDEN, "Tài khoản đã bị khóa, vui lòng liên hệ quản trị viên!");
            }

            // Đang bị đình chỉ 30s: chặn luôn, kể cả nhập đúng mật khẩu
            LocalDateTime now = LocalDateTime.now();
            if (user.getKhoaDen() != null && now.isBefore(user.getKhoaDen())) {
                long remain = Duration.between(now, user.getKhoaDen()).getSeconds() + 1;
                return fail(HttpStatus.LOCKED, "Tài khoản đang bị đình chỉ. Vui lòng thử lại sau " + remain + " giây.");
            }

            // Sai mật khẩu
            if (!user.getMatKhau().equals(request.getPassword())) {
                int attempts = (user.getSoLanSai() == null ? 0 : user.getSoLanSai()) + 1;
                if (attempts >= MAX_ATTEMPTS) {
                    user.setSoLanSai(0);
                    user.setKhoaDen(now.plusSeconds(LOCK_SECONDS));
                    nhanSuRepository.save(user);
                    return fail(HttpStatus.LOCKED, "Bạn đã nhập sai " + MAX_ATTEMPTS + " lần. Tài khoản bị đình chỉ " + LOCK_SECONDS + " giây.");
                }
                user.setSoLanSai(attempts);
                nhanSuRepository.save(user);
                return fail(HttpStatus.UNAUTHORIZED, "Sai mật khẩu. Bạn còn " + (MAX_ATTEMPTS - attempts) + " lần thử.");
            }

            // Đúng mật khẩu: reset bộ đếm
            user.setSoLanSai(0);
            user.setKhoaDen(null);
            nhanSuRepository.save(user);

            return ResponseEntity.ok(new LoginResponse(
                    true,
                    "Đăng nhập thành công!",
                    user.getRole().toLowerCase(),
                    user.getCccd(),
                    user.getHoTen(),
                    user.getSdt(),
                    user.getTaiKhoan()
            ));
        }

        private ResponseEntity<LoginResponse> fail(HttpStatus status, String message) {
            return ResponseEntity.status(status).body(
                    new LoginResponse(false, message, null, null, null, null, null));
        }
    }