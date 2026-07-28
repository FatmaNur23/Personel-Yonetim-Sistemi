package com.example.personellistesi.repo;

import com.example.personellistesi.model.BildirimLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BildirimLogRepository extends JpaRepository<BildirimLog, String> {

    // Alıcı adresine göre bildirim loglarını listelemek için
    List<BildirimLog> findByToAddress(String toAddress);

    // Belirli bir tarih aralığındaki bildirim loglarını listelemek için
    List<BildirimLog> findBySendTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    // İstersen her ikisini aynı anda kullanarak da filtreleme yapabilirsin
    List<BildirimLog> findByToAddressAndSendTimeBetween(String toAddress, LocalDateTime startDate, LocalDateTime endDate);


}
