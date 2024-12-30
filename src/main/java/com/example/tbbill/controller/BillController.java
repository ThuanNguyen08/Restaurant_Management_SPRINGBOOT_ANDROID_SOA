package com.example.tbbill.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.example.tbbill.entities.Bill;
import com.example.tbbill.service.BillService;
import com.example.tbbill.service.RequestOtherPortService;

@RestController
@RequestMapping("/api/v1/bills")
public class BillController {
    @Autowired
    private BillService billService;

    @Autowired
    private RequestOtherPortService requestOtherPortService;

    public void Authentication(String token) {
        if (!requestOtherPortService.auth(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed");
        }
    }

    // Thêm bill mới
    @PostMapping
    public ResponseEntity<?> createBill(
            @RequestBody Bill bill,
            @RequestHeader("Authorization") String token) {
        try {
            Authentication(token);
            return new ResponseEntity<>(billService.createBill(bill , token), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
        }
    }

    // Lấy bill theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getBillById(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String token) {
        try {
            Authentication(token);
            billService.updateTotalAmount(id, token);
            return ResponseEntity.ok(billService.getBillById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
        }
    }
    
    
    // Lấy bill theo TableID
    @GetMapping("/table/{tableId}")
    public ResponseEntity<?> getBillByTableId(
            @PathVariable Integer tableId,
            @RequestHeader("Authorization") String token) {
        try {
            Authentication(token);
            return ResponseEntity.ok(billService.getBillByTableId(tableId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
        }
    }

    // Cập nhật bill
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBill(
            @PathVariable Integer id,
            @RequestBody Bill bill,
            @RequestHeader("Authorization") String token) {
        try {
            Authentication(token);
            return ResponseEntity.ok(billService.updateBill(id, bill));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
        }
    }

    // Xóa bill
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBill(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String token) {
        try {
            Authentication(token);
            billService.deleteBill(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
        }
    }

    // Lấy tất cả bills
    @GetMapping
    public ResponseEntity<?> getAllBills(@RequestHeader("Authorization") String token) {
        try {
            Authentication(token);
            return ResponseEntity.ok(billService.getAllBills());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
        }
    }

    // Lấy bills theo status
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getBillsByStatus(
            @PathVariable String status,
            @RequestHeader("Authorization") String token) {
        try {
            Authentication(token);
            return ResponseEntity.ok(billService.getBillsByStatus(status));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
        }
    }
    
    
    
 // Cập nhật tổng tiền
    @PutMapping("/total/{id}")
    public ResponseEntity<?> updateTotalAmount(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String token) {
        try {
            Authentication(token);
            return ResponseEntity.ok(billService.updateTotalAmount(id, token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Thanh toán bill
    @PutMapping("/pay/{id}")
    public ResponseEntity<?> payBill(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String token) {
        try {
            Authentication(token);
            return ResponseEntity.ok(billService.payBill(id,token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Hủy bill
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBill(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String token) {
        try {
            Authentication(token);
            return ResponseEntity.ok(billService.cancelBill(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}