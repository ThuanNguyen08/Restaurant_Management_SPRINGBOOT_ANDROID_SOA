package com.example.BillAndDetailBil.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.BillAndDetailBil.config.BadRequestException;
import com.example.BillAndDetailBil.config.ResourceNotFoundException;
import com.example.BillAndDetailBil.entities.BillDetail;
import com.example.BillAndDetailBil.repository.BillDetailRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@Service
public class BillDetailService {
    @Autowired
    private BillDetailRepository billDetailRepository;
    
    @Autowired
    private RequestOtherPortService requestOtherPortService;

    // Tính tổng tiền
    public Integer calculateTotalAmount2(Integer billId) {
        List<BillDetail> details = billDetailRepository.findByBill_BillID(billId);
        int total = 0;	
        
        for(BillDetail detail : details) {
            if(detail.getPrice() != null && detail.getQuantity() != null) {
                total += detail.getPrice() * detail.getQuantity();
            } else {
                // Log lỗi hoặc ném exception nếu gặp dữ liệu null
                throw new BadRequestException("Lỗi: Giá hoặc số lượng bị null cho billDetailID: " 
                    + detail.getBillDetailID());
            }
        }
        
        return total;
    }
    // Thêm món hoặc cập nhật số lượng nếu món đã tồn tại
    public BillDetail createBillDetail(BillDetail billDetail, String token) {
        try {
            // Lấy giá món ăn từ food service
            String foodInfo = requestOtherPortService.getFoodInfo(billDetail.getFoodID(), token);
            
            // Parse JSON response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode foodNode = mapper.readTree(foodInfo);
            
            // Lấy giá từ response và chuyển đổi sang Integer
            String priceStr = foodNode.get("price").asText();
            // Loại bỏ dấu "," hoặc "." nếu có và chuyển thành Integer
            Integer price = Integer.parseInt(priceStr.replaceAll("[^0-9]", ""));
            
            // Kiểm tra món đã tồn tại trong bill chưa
            Optional<BillDetail> existingDetail = billDetailRepository
                .findByBill_BillIDAndFoodID(billDetail.getBillID(), billDetail.getFoodID());
            
            if (existingDetail.isPresent()) {
                // Nếu món đã tồn tại, cộng thêm số lượng và giữ nguyên giá
                BillDetail existing = existingDetail.get();
                existing.setQuantity(existing.getQuantity() + billDetail.getQuantity());
                existing.setPrice(price); // Đảm bảo giá luôn được cập nhật
                return billDetailRepository.save(existing);
            } else {
                // Nếu món chưa tồn tại, tạo mới với giá từ food service
                billDetail.setPrice(price);
                return billDetailRepository.save(billDetail);
            }
        } catch (Exception e) {
            throw new BadRequestException("Lỗi khi thêm món: " + e.getMessage());
        }
    }

    // Cập nhật số lượng món
    public BillDetail updateBillDetail(Integer billId, Integer foodId, Integer newQuantity) {
        BillDetail detail = billDetailRepository.findByBill_BillIDAndFoodID(billId, foodId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy món trong hóa đơn"));
        
        if (newQuantity <= 0) {
            throw new BadRequestException("Số lượng phải lớn hơn 0");
        }
        
        detail.setQuantity(newQuantity);
        return billDetailRepository.save(detail);
    }

    // Xóa món khỏi bill
    public void deleteBillDetail(Integer billId, Integer foodId) {
        BillDetail detail = billDetailRepository.findByBill_BillIDAndFoodID(billId, foodId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy món trong hóa đơn"));
        billDetailRepository.delete(detail);
    }

    // Lấy tất cả món theo billId
    public List<BillDetail> getBillDetailsByBillId(Integer billId) {
        return billDetailRepository.findByBill_BillID(billId);
    }

    // Xóa tất cả món theo billId
    @Transactional
    public void deleteAllByBillId(Integer billId) {
        billDetailRepository.deleteByBill_BillID(billId);
    }
  
    //Tìm kiếm billid và fooodid
    public boolean checkFoodInDetailBill(Integer billId, Integer foodId) {
    	return billDetailRepository.existsByBill_BillIDAndFoodID(billId, foodId);
    }
}