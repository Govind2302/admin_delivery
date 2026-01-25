package com.foodgram.repository;

import com.foodgram.model.UserAddresses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddresses, Long> {

    List<UserAddresses> findByUserId(Long userId);
}