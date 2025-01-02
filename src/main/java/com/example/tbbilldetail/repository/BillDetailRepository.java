package com.example.tbbilldetail.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tbbilldetail.entities.BillDetail;

@Repository
public interface BillDetailRepository extends JpaRepository<BillDetail, Integer> {
    List<BillDetail> findByBillID(Integer billID);
    Optional<BillDetail> findByBillIDAndFoodID(Integer billID, Integer foodID);
    void deleteByBillID(Integer billID);
    
    boolean existsByBillIDAndFoodID(Integer billID, Integer foodId);
}
