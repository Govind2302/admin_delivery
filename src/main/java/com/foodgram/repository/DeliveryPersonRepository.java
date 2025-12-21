package com.foodgram.repository;

import com.foodgram.model.DeliveryPerson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryPersonRepository extends JpaRepository<DeliveryPerson, Long> {

    Optional<DeliveryPerson> findByUserId(Long userId);

    Page<DeliveryPerson> findByVerificationStatus(DeliveryPerson.VerificationStatus status, Pageable pageable);

    Page<DeliveryPerson> findByOperatingArea(String operatingArea, Pageable pageable);

    Page<DeliveryPerson> findByVerificationStatusAndOperatingArea(
            DeliveryPerson.VerificationStatus status,
            String operatingArea,
            Pageable pageable
    );

    List<DeliveryPerson> findAllByVerificationStatus(DeliveryPerson.VerificationStatus status);
}