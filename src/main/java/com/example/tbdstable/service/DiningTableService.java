package com.example.tbdstable.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.tbdstable.config.BadRequestException;
import com.example.tbdstable.config.ResourceNotFoundException;
import com.example.tbdstable.entities.DiningTable;
import com.example.tbdstable.repository.DiningTableRepository;

@Service
public class DiningTableService {
    @Autowired
    private DiningTableRepository diningTableRepository;

    public List<DiningTable> getAllTables() {
        return diningTableRepository.findAll();
    }

    public DiningTable getTableById(Integer id) {
        return diningTableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bàn với id: " + id));
    }

    public DiningTable createTable(DiningTable table) {
        // Kiểm tra tên bàn đã tồn tại chưa
        if(diningTableRepository.existsByTableName(table.getTableName())) {
            throw new BadRequestException("Tên bàn đã tồn tại: " + table.getTableName());
        }
        
        return diningTableRepository.save(table);
    }

    public DiningTable updateTable(Integer id, DiningTable table) {
        DiningTable existingTable = getTableById(id);
        
        // Kiểm tra nếu tên bàn mới khác tên bàn cũ và đã tồn tại
        if(!existingTable.getTableName().equals(table.getTableName()) 
           && diningTableRepository.existsByTableName(table.getTableName())) {
            throw new BadRequestException("Tên bàn đã tồn tại: " + table.getTableName());
        }
        
        existingTable.setTableName(table.getTableName());
        existingTable.setStatus(table.getStatus());
        
        return diningTableRepository.save(existingTable);
    }

    public DiningTable updateTableStatus(Integer id, String status) {
        DiningTable table = getTableById(id);
        table.setStatus(status);
       
        return diningTableRepository.save(table);
    }

    public void deleteTable(Integer id) {
        DiningTable table = getTableById(id);
        // Kiểm tra bàn có đang được sử dụng không
        if("OCCUPIED".equals(table.getStatus())) {
            throw new BadRequestException("Không thể xóa bàn đang có người ngồi");
        }
        
        diningTableRepository.deleteById(id);
    }
}
