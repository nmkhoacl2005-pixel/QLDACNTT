/**
 * common.js - Dùng chung cho toàn bộ trang quản trị MINIMART.CRM
 * - Bảo vệ trang (chỉ cho vào khi đã đăng nhập)
 * - Tô sáng mục sidebar đang active theo data-page trên <body>
 * - Đổ thông tin người dùng đăng nhập vào sidebar/header
 * - Xử lý đăng xuất
 */
const API_BASE_ROOT = 'http://localhost:8080';

function getCurrentUser() {
    try {
        const raw = sessionStorage.getItem('currentUser');
        return raw ? JSON.parse(raw) : null;
    } catch (e) {
        return null;
    }
}

function requireAuth() {
    const user = getCurrentUser();
    if (!user) {
        window.location.href = '../login.html';
        return null;
    }
    return user;
}

function logout() {
    sessionStorage.removeItem('currentUser');
    sessionStorage.removeItem('role');
    window.location.href = '../login.html';
}

function initials(name) {
    if (!name) return 'ND';
    const parts = name.trim().split(/\s+/);
    const last = parts[parts.length - 1] || '';
    const first = parts[0] || '';
    return ((first[0] || '') + (last[0] || '')).toUpperCase() || 'ND';
}

function paintUserInfo(user) {
    const roleLabel = user.role === 'admin' ? 'Quản trị viên' : 'Nhân viên thu ngân';

    document.querySelectorAll('.js-user-name').forEach(el => el.textContent = user.hoTen || user.taiKhoan || 'Người dùng');
    document.querySelectorAll('.js-user-role').forEach(el => el.textContent = roleLabel);
    document.querySelectorAll('.js-user-avatar').forEach(el => el.textContent = initials(user.hoTen));
}

function highlightSidebar() {
    const page = document.body.dataset.page;
    if (!page) return;
    document.querySelectorAll('.sidebar-link').forEach(a => {
        a.classList.remove('text-gray-600', 'hover:bg-gray-100');
        a.classList.remove('bg-emerald-50', 'text-emerald-700', 'font-bold', 'border-l-4', 'border-emerald-600');
        if (a.dataset.key === page) {
            a.classList.add('bg-emerald-50', 'text-emerald-700', 'font-bold');
        } else {
            a.classList.add('text-gray-600', 'hover:bg-gray-100');
        }
    });
}

function wireLogoutButtons() {
    document.querySelectorAll('.js-logout-btn').forEach(btn => {
        btn.addEventListener('click', logout);
    });
}

function initAdminPage() {
    const user = requireAuth();
    if (!user) return null;
    paintUserInfo(user);
    highlightSidebar();
    wireLogoutButtons();
    return user;
}

document.addEventListener('DOMContentLoaded', initAdminPage);