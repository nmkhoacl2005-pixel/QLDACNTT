package com.example.CCHKT.repository;

import com.example.CCHKT.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, String> {
    List<Voucher> findBySdtOrderByNgayTaoDesc(String sdt);
}
