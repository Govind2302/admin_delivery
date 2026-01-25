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

    Optional<DeliveryPerson> findByUser_UserId(Long userId);

    //User_UserId tells spring jpa to find a user object that has user_id

    Page<DeliveryPerson> findByStatus(DeliveryPerson.VerificationStatus status, Pageable pageable);

    Page<DeliveryPerson> findByOperatingArea(String operatingArea, Pageable pageable);

    Page<DeliveryPerson> findByStatusAndOperatingArea(
            DeliveryPerson.VerificationStatus status,
            String operatingArea,
            Pageable pageable
    );




    List<DeliveryPerson> findAllByStatus(DeliveryPerson.VerificationStatus status);
}