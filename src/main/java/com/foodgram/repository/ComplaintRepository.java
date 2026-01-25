package com.foodgram.repository;

import com.foodgram.model.Complaint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    Page<Complaint> findByStatus(Complaint.ComplaintStatus status, Pageable pageable);

    Page<Complaint> findByUserId(Long userId, Pageable pageable);

    List<Complaint> findAllByUserId(Long userId);

    Page<Complaint> findByOrderId(Long orderId, Pageable pageable);

    List<Complaint> findAllByStatus(Complaint.ComplaintStatus status);

    long countByStatus(Complaint.ComplaintStatus status);
}