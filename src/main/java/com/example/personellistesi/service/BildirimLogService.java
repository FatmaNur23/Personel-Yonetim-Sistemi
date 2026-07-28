package com.example.personellistesi.service;

import com.example.personellistesi.model.BildirimLog;
import com.example.personellistesi.repo.BildirimLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BildirimLogService implements IBildirimLogService {

    private final BildirimLogRepository bildirimLogRepository;

    @Autowired
    public BildirimLogService(BildirimLogRepository bildirimLogRepository) {
        this.bildirimLogRepository = bildirimLogRepository;
    }

    @Override
    public BildirimLog saveLog(BildirimLog bildirimLog) {
        return bildirimLogRepository.save(bildirimLog);
    }

    @Override
    public BildirimLog getLogById(String id) {
        return bildirimLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hata: " + id + " numaralı log bulunamadı!"));
    }

    @Override
    public List<BildirimLog> getAllLogs() {
        return bildirimLogRepository.findAll();
    }

    @Override
    public List<BildirimLog> getLogsByToAddress(String toAddress) {
        return bildirimLogRepository.findByToAddress(toAddress);
    }
}
