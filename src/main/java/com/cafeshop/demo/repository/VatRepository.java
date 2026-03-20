package com.cafeshop.demo.repository;

import com.cafeshop.demo.mode.Vat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VatRepository extends JpaRepository<Vat, Long> {
    @Modifying
    @Query("UPDATE Vat v SET v.isDefault = false")
    void clearDefaults();

    boolean existsByIsDefaultTrue();

    Optional<Vat> findByIsDefaultTrueAndIsActiveTrue();

    Optional<Vat> findFirstByIsActiveTrueOrderByIsDefaultDescIdAsc();
}
