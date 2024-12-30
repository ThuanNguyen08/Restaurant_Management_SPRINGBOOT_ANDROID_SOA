package com.example.tbdstable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tbdstable.entities.DiningTable;

@Repository
public interface DiningTableRepository extends JpaRepository<DiningTable, Integer> {
    List<DiningTable> findByStatus(String status);
    boolean existsByTableName(String tableName);
}
