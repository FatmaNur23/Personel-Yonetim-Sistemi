package com.example.personellistesi.repo;

import com.example.personellistesi.model.Izin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IzinRepository extends JpaRepository<Izin, String> {
}
