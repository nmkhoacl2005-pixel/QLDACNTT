package com.example.CCHKT.dto.response;

public class LoginResponse {
    private boolean success;
    private String message;
    private String role;       // "admin" hoặc "nhanvien"
    private String cccd;
    private String hoTen;
    private String sdt;
    private String taiKhoan;

    public LoginResponse() {}

    public LoginResponse(boolean success, String message, String role, String cccd, String hoTen, String sdt, String taiKhoan) {
        this.success = success;
        this.message = message;
        this.role = role;
        this.cccd = cccd;
        this.hoTen = hoTen;
        this.sdt = sdt;
        this.taiKhoan = taiKhoan;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getTaiKhoan() { return taiKhoan; }
    public void setTaiKhoan(String taiKhoan) { this.taiKhoan = taiKhoan; }
}