package com.example.personellistesi.repo;

import com.example.personellistesi.model.BildirimLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BildirimLogRepository extends JpaRepository<BildirimLog, String> {

    List<BildirimLog> findByToAddress(String toAddress);

    List<BildirimLog> findBySendTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<BildirimLog> findByToAddressAndSendTimeBetween(String toAddress, LocalDateTime startDate, LocalDateTime endDate);

}
