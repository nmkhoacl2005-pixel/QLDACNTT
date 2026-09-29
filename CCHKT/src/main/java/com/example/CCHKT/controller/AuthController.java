package com.example.CCHKT.controller;

import com.example.CCHKT.dto.request.LoginRequest;
import com.example.CCHKT.dto.response.LoginResponse;
import com.example.CCHKT.entity.NhanSu;
import com.example.CCHKT.repository.NhanSuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private NhanSuRepository nhanSuRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Optional<NhanSu> optionalNhanSu = nhanSuRepository.findByTaiKhoanAndMatKhau(
                request.getUsername(),
                request.getPassword()
        );

        if (optionalNhanSu.isPresent()) {
            NhanSu user = optionalNhanSu.get();
            LoginResponse response = new LoginResponse(
                    true,
                    "Đăng nhập thành công!",
                    user.getRole().toLowerCase(),
                    user.getCccd(),
                    user.getHoTen(),
                    user.getSdt(),
                    user.getTaiKhoan()
            );
            return ResponseEntity.ok(response);
        } else {
            LoginResponse response = new LoginResponse(
                    false,
                    "Tài khoản hoặc mật khẩu không chính xác!",
                    null, null, null, null, null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}