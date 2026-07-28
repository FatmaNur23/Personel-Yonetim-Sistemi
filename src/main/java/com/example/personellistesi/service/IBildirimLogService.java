package com.example.personellistesi.service;

import com.example.personellistesi.model.BildirimLog;
import java.util.List;

public interface IBildirimLogService {

    // Yeni bir bildirim logu kaydetmek için
    BildirimLog saveLog(BildirimLog bildirimLog);

    // ID'ye göre belirli bir logu getirmek için
    BildirimLog getLogById(String id);

    // Tüm logları listelemek için
    List<BildirimLog> getAllLogs();

    // Alıcı e-posta adresine göre listelemek için
    List<BildirimLog> getLogsByToAddress(String toAddress);
}