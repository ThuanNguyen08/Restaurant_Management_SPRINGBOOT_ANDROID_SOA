package com.example.tbdstable.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.tbdstable.entities.DiningTable;
import com.example.tbdstable.service.DiningTableService;
import com.example.tbdstable.service.RequestOtherPortService;

@RestController
@RequestMapping("/api/v1/tables")
public class DiningTableController {
    @Autowired
    private DiningTableService diningTableService;
    
    @Autowired
    private RequestOtherPortService requestOtherPortService;

    public void Authentication(String token) {
        if (!requestOtherPortService.auth(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed");
        }
    }

    @PostMapping
    public ResponseEntity<DiningTable> createTable(
            @RequestBody DiningTable table, 
            @RequestHeader("Authorization") String token) {
        Authentication(token);
        return new ResponseEntity<>(diningTableService.createTable(table), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DiningTable>> getAllTables(
            @RequestHeader("Authorization") String token) {
        Authentication(token);
        return ResponseEntity.ok(diningTableService.getAllTables());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiningTable> updateTableStatus(
            @PathVariable Integer id,
            @RequestBody String status,
            @RequestHeader("Authorization") String token) {
        Authentication(token);
        return ResponseEntity.ok(diningTableService.updateTableStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String token) {
        Authentication(token);
        diningTableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiningTable> getTableById(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String token) {
        Authentication(token);
        return ResponseEntity.ok(diningTableService.getTableById(id));
    }
}