package com.example.personellistesi.repo;

import com.example.personellistesi.model.Kullanıcı;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KullanıcıRepository extends JpaRepository<Kullanıcı, String> {
    Optional<Kullanıcı> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<Kullanıcı> findByActivationToken(String activationToken);


}
