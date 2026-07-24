package com.example.personellistesi.repo;

import com.example.personellistesi.model.Departman;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DepartmanRepository extends JpaRepository<Departman, String> {

    Optional<Departman> findByAd(String ad);

}
