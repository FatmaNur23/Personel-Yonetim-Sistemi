package com.example.personellistesi.service;

import com.example.personellistesi.model.BildirimLog;
import java.util.List;

public interface IBildirimLogService {

    BildirimLog saveLog(BildirimLog bildirimLog);

    BildirimLog getLogById(String id);

    List<BildirimLog> getAllLogs();

    List<BildirimLog> getLogsByToAddress(String toAddress);
}