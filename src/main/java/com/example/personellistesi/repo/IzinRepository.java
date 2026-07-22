package com.example.personellistesi.repo;

import com.example.personellistesi.model.Izin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IzinRepository extends JpaRepository<Izin, String> {
    // Personel ID'sine göre o personelin tüm izinlerini getirecek özel metot
    List<Izin> findByPersonelId(String personelId);

}
