package com.example.personellistesi.repo;

import com.example.personellistesi.model.Personel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonelRepository extends JpaRepository<Personel, String> {

    Optional<Personel> findByTckn(String tckn);
    List<Personel> findAll();
    void deleteById(String id);

}

