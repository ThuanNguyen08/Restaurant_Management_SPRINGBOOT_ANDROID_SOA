package com.example.BillAndDetailBil.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.BillAndDetailBil.entities.BillDetail;

@Repository
public interface BillDetailRepository extends JpaRepository<BillDetail, Integer> {
	List<BillDetail> findByBill_BillID(Integer billID);

	Optional<BillDetail> findByBill_BillIDAndFoodID(Integer billID, Integer foodID);

	void deleteByBill_BillID(Integer billID);

	boolean existsByBill_BillIDAndFoodID(Integer billID, Integer foodId);
}
