package com.example.personellistesi.service;

import com.example.personellistesi.model.Departman;
import com.example.personellistesi.model.Personel;
import com.example.personellistesi.repo.DepartmanRepository;
import com.example.personellistesi.repo.PersonelRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.ZoneId;
import java.util.*;

@Service
public class PersonelService {
    @Autowired
    private PersonelRepository personelRepository;

    @Autowired
    private DepartmanRepository departmanRepository;

    // ─── 1. EXCEL'DEN VERİ OKUMA VE GÜNCELLEME / EKLEME (IMPORT) ───
    @Transactional
    public String excelImport(MultipartFile file) throws Exception {
        List<Personel> exceldenOkunanlar = new ArrayList<>();
        Set<String> excelIciTcknSet = new HashSet<>();
        List<String> mukerrerTcknler = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Personel personel = new Personel();

                // TCKN Okuma
                String tckn = getCellValueAsString(row.getCell(0)).trim();
                // Hem uzunluk hem de sadece rakam kontrolünü birleştiriyoruz:
                if (tckn.isEmpty() || tckn.length() != 11 || !tckn.matches("\\d+")) {
                    throw new IllegalArgumentException("Hata: " + i + ". satırdaki TCKN geçersiz! 11 haneli ve sadece rakamlardan oluşmalıdır.");
                }

                // Excel'in kendi içinde mükerrer TCKN kontrolü
                if (!excelIciTcknSet.add(tckn)) {
                    mukerrerTcknler.add(tckn);
                }

                personel.setTckn(tckn);
                personel.setAd(getCellValueAsString(row.getCell(1)));
                personel.setSoyad(getCellValueAsString(row.getCell(2)));
                personel.setTelefon(getCellValueAsString(row.getCell(3)));

                // Yaş
                if (row.getCell(4) != null && row.getCell(4).getCellType() == CellType.NUMERIC) {
                    personel.setYas((int) row.getCell(4).getNumericCellValue());
                }

                // Maaş
                if (row.getCell(5) != null && row.getCell(5).getCellType() == CellType.NUMERIC) {
                    personel.setMaas((float) row.getCell(5).getNumericCellValue());
                }

                // İşe Giriş Tarihi
                if (row.getCell(6) != null && DateUtil.isCellDateFormatted(row.getCell(6))) {
                    Date date = row.getCell(6).getDateCellValue();
                    personel.setIseGirisTarihi(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                } else {
                    throw new IllegalArgumentException("Hata: " + i + ". satırdaki tarih formatı hatalı!");
                }

                exceldenOkunanlar.add(personel);
            }
        }

        if (!mukerrerTcknler.isEmpty()) {
            throw new RuntimeException("Excel listesinde aynı TCKN'ye sahip mükerrer kayıtlar bulundu! Mükerrer TCKN'ler: " + mukerrerTcknler);
        }

        int guncellenenSayisi = 0;
        int eklenenSayisi = 0;

        for (Personel excelPersonel : exceldenOkunanlar) {
            Optional<Personel> mevcutPersonelOpt = personelRepository.findByTckn(excelPersonel.getTckn());

            if (mevcutPersonelOpt.isPresent()) {
                // TCKN varsa: MEVCUT BİLGİYİ GÜNCELLE
                Personel mevcutPersonel = mevcutPersonelOpt.get();
                mevcutPersonel.setAd(excelPersonel.getAd());
                mevcutPersonel.setSoyad(excelPersonel.getSoyad());
                mevcutPersonel.setTelefon(excelPersonel.getTelefon());
                mevcutPersonel.setYas(excelPersonel.getYas());
                mevcutPersonel.setMaas(excelPersonel.getMaas());
                mevcutPersonel.setIseGirisTarihi(excelPersonel.getIseGirisTarihi());

                personelRepository.save(mevcutPersonel);
                guncellenenSayisi++;
            } else {
                // TCKN yoksa: YENİ EKLE
                personelRepository.save(excelPersonel);
                eklenenSayisi++;
            }
        }

        return "İşlem Başarılı! " + eklenenSayisi + " personel eklendi, " + guncellenenSayisi + " personel güncellendi.";
    }

    // ─── 2. VERİTABANINDAN EXCEL ÜRETİP İNDİRME (EXPORT) ───
    public ByteArrayInputStream exportToExcel() throws Exception {
        // Sistemdeki tüm personelleri çekiyoruz
        List<Personel> personeller = personelRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("com.example.personellistesi.com.example.personellistesi.model.Personel Listesi");

            // Başlık satırını tasarlıyoruz
            Row headerRow = sheet.createRow(0);
            String[] headers = {"TCKN", "Ad", "Soyad", "Telefon", "Yaş", "Maaş", "İşe Giriş Tarihi"};

            // Başlıklar için kalın yazı tipi (Font) stili oluşturuyoruz
            CellStyle headerCellStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.BLUE.getIndex());
            headerCellStyle.setFont(headerFont);

            for (int col = 0; col < headers.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(headers[col]);
                cell.setCellStyle(headerCellStyle);
            }

            // Veritabanından gelen verileri Excel satırlarına yazıyoruz
            int rowIdx = 1;
            for (Personel personel : personeller) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(personel.getTckn());
                row.createCell(1).setCellValue(personel.getAd());
                row.createCell(2).setCellValue(personel.getSoyad());
                row.createCell(3).setCellValue(personel.getTelefon() != null ? personel.getTelefon() : "");
                row.createCell(4).setCellValue(personel.getYas() != null ? personel.getYas() : 0);
                row.createCell(5).setCellValue(personel.getMaas() != null ? personel.getMaas() : 0.0);

                // Tarih hücresini formatlıyoruz
                Cell dateCell = row.createCell(6);
                if (personel.getIseGirisTarihi() != null) {
                    dateCell.setCellValue(java.sql.Date.valueOf(personel.getIseGirisTarihi()));

                    // Excel'de tarihin düzgün görünmesi için stil tanımlıyoruz (YYYY-MM-DD)
                    CellStyle dateCellStyle = workbook.createCellStyle();
                    CreationHelper createHelper = workbook.getCreationHelper();
                    dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd"));
                    dateCell.setCellStyle(dateCellStyle);
                }
            }

            // Sütun genişliklerini içeriğe göre otomatik sığdırıyoruz
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                long num = (long) cell.getNumericCellValue();
                yield String.valueOf(num);
            }
            default -> "";
        };
    }

    // ─── YENİ: PERSONEL EKLEME (POST) ───
    @Transactional
    public Personel personelEkle(Personel personel) {
        // Departman kontrolü
        if (personel.getDepartman() == null || personel.getDepartman().getId() == null) {
            throw new IllegalArgumentException("Departman ID eksik!");
        }

        Departman departman = departmanRepository.findById(personel.getDepartman().getId())
                .orElseThrow(() -> new IllegalArgumentException("Geçersiz Departman ID!"));

        personel.setDepartman(departman); // Doğrulanmış departmanı atıyoruz
        return personelRepository.save(personel);
    }

    // ─── YENİ: PERSONEL GÜNCELLEME (PUT) ───
    @Transactional
    public Personel personelGuncelle(String id, Personel guncelBilgiler) {
        Personel mevcutPersonel = personelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Güncellenecek personel bulunamadı!"));

        // Yeni departman atanmışsa doğrula ve güncelle
        if (guncelBilgiler.getDepartman() != null && guncelBilgiler.getDepartman().getId() != null) {
            Departman yeniDepartman = departmanRepository.findById(guncelBilgiler.getDepartman().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Atanmak istenen yeni departman bulunamadı!"));
            mevcutPersonel.setDepartman(yeniDepartman);
        }

        // Diğer bilgileri güncelle
        mevcutPersonel.setAd(guncelBilgiler.getAd());
        mevcutPersonel.setSoyad(guncelBilgiler.getSoyad());
        mevcutPersonel.setTelefon(guncelBilgiler.getTelefon());
        mevcutPersonel.setYas(guncelBilgiler.getYas());
        mevcutPersonel.setMaas(guncelBilgiler.getMaas());

        return personelRepository.save(mevcutPersonel);
    }

    // ─── TÜM PERSONELLERİ LİSTELEME SERVİSİ ───
    public List<Personel> tumunuGetir() {
        return personelRepository.findAll();
    }

    // ─── ID'YE GÖRE PERSONEL SİLME SERVİSİ ───
    @Transactional
    public void idIleSil(String id) {
        personelRepository.deleteById(id);
    }
}
