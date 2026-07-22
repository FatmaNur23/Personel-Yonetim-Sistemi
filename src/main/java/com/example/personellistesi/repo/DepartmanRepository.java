package com.example.personellistesi.repo;

import com.example.personellistesi.model.Departman;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmanRepository extends JpaRepository<Departman, String> {
}
