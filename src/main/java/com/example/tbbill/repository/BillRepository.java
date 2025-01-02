package com.example.tbbill.repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tbbill.entities.Bill;

@Repository
public interface BillRepository extends JpaRepository<Bill, Integer> {
    Optional<Bill> findByTableIDAndStatus(Integer tableId, String status);
    List<Bill> findByStatus(String status);
    List<Bill> findByUserInfoID(Integer userInfoId);
    List<Bill> findByStatusAndBillDateBetweenOrderByBillDateDesc(
            String status, 
            LocalDateTime startDate, 
            LocalDateTime endDate
        );
}