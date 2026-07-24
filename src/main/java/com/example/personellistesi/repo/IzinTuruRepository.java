package com.example.personellistesi.repo;

import com.example.personellistesi.model.Izin;
import com.example.personellistesi.model.IzinTuru;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IzinTuruRepository extends JpaRepository<IzinTuru, String> {

}