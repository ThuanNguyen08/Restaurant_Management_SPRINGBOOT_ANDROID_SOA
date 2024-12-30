package com.example.tbbilldetail.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.tbbilldetail.entities.BillDetail;
import com.example.tbbilldetail.service.BillDetailService;
import com.example.tbbilldetail.service.RequestOtherPortService;

@RestController
@RequestMapping("/api/v1/billdetails")
public class BillDetailController {
    @Autowired
    private BillDetailService billDetailService;
    
    @Autowired
    private RequestOtherPortService requestOtherPortService;

    public void Authentication(String token) {
        if (!requestOtherPortService.auth(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed");
        }
    }
    // Tính tổng tiền theo billID
    @GetMapping("/bill/total/{billId}")
    public ResponseEntity<?> getTotalAmount(
            @PathVariable Integer billId,
            @RequestHeader("Authorization") String token) {
        try {
            Authentication(token);
            Integer total = billDetailService.calculateTotalAmount2(billId);
            return ResponseEntity.ok(total);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Thêm món vào bill
    @PostMapping
    public ResponseEntity<?> addBillDetail(
            @RequestBody BillDetail billDetail,
            @RequestHeader("Authorization") String token) {
        try {
            Authentication(token);
            return new ResponseEntity<>(
                billDetailService.createBillDetail(billDetail, token), 
                HttpStatus.CREATED
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Cập nhật số lượng món
    @PutMapping("/{billId}/{foodId}")
    public ResponseEntity<?> updateBillDetail(
            @PathVariable Integer billId,
            @PathVariable Integer foodId,
            @RequestBody Integer newQuantity,
            @RequestHeader("Authorization") String token) {
        try {
            Authentication(token);
            return ResponseEntity.ok(
                billDetailService.updateBillDetail(billId, foodId, newQuantity)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Xóa món khỏi bill
    @DeleteMapping("/{billId}/{foodId}")
    public ResponseEntity<?> deleteBillDetail(
            @PathVariable Integer billId,
            @PathVariable Integer foodId,
            @RequestHeader("Authorization") String token) {
        try {
            Authentication(token);
            billDetailService.deleteBillDetail(billId, foodId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Lấy tất cả món theo billId
    @GetMapping("/bill/{billId}")
    public ResponseEntity<?> getBillDetailsByBillId(
            @PathVariable Integer billId,
            @RequestHeader("Authorization") String token) {
        try {
            Authentication(token);
            return ResponseEntity.ok(billDetailService.getBillDetailsByBillId(billId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Xóa tất cả món theo billId
    @DeleteMapping("/bill/{billId}")
    public ResponseEntity<?> deleteAllByBillId(
            @PathVariable Integer billId,
            @RequestHeader("Authorization") String token) {
        try {
            Authentication(token);
            billDetailService.deleteAllByBillId(billId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}