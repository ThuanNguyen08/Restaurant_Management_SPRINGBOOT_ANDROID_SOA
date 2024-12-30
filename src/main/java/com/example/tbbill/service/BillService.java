package com.example.tbbill.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.tbbill.config.BadRequestException;
import com.example.tbbill.config.ResourceNotFoundException;
import com.example.tbbill.entities.Bill;
import com.example.tbbill.repository.BillRepository;

@Service
public class BillService {
	@Autowired
	private BillRepository billRepository;
	@Autowired
	private RequestOtherPortService requestOtherPortService;

	// Thêm bill mới
	// Tạo bill mới
	public Bill createBill(Bill bill , String token) {
		// Kiểm tra bàn đã có bill chưa
		billRepository.findByTableIDAndStatus(bill.getTableID(), "PENDING").ifPresent(existingBill -> {
			throw new BadRequestException("Bàn này đã có hóa đơn đang xử lý");
		});

		bill.setBillDate(LocalDateTime.now());
		bill.setStatus("PENDING");
		bill.setTotalAmount(0); // Khởi tạo tổng tiền = 0
		requestOtherPortService.updateTableStatus(bill.getTableID(), "OCCUPIED", token);
		return billRepository.save(bill);
	}

	// Cập nhật tổng tiền bill
	public Bill updateTotalAmount(Integer billId, String token) {
		Bill bill = getBillById(billId);

		// Gọi sang billdetail service để tính tổng tiền
		Integer totalAmount = requestOtherPortService.calculateTotalAmount(billId, token);
		bill.setTotalAmount(totalAmount);

		return billRepository.save(bill);
	}

	// Thanh toán bill
	public Bill payBill(Integer billId , String token) {
		Bill bill = getBillById(billId);
		if (!"PENDING".equals(bill.getStatus())) {
			throw new BadRequestException("Hóa đơn đã được thanh toán hoặc đã hủy");
		}
		bill.setStatus("PAID");
		requestOtherPortService.updateTableStatus(bill.getTableID(), "EMPTY", token);
		requestOtherPortService.deleteBillDetails(billId, token);
		return billRepository.save(bill);
	}

	 // Hủy bill
    public Bill cancelBill(Integer billId) {
        Bill bill = getBillById(billId);
        if (!"PENDING".equals(bill.getStatus())) {
            throw new BadRequestException("Hóa đơn đã được thanh toán hoặc đã hủy");
        }
        bill.setStatus("CANCELLED");
        return billRepository.save(bill);
    }
	// Lấy bill theo ID
	public Bill getBillById(Integer id) {
		return billRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với id: " + id));
	}

	// Lấy bill theo TableID
		public Bill getBillByTableId(Integer tableId) {
			return billRepository.findByTableIDAndStatus(tableId, "PENDING")
					.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với id: " + tableId));
		}
	
	// Cập nhật bill
	public Bill updateBill(Integer id, Bill bill) {
		Bill existingBill = getBillById(id);

		if ("PAID".equals(existingBill.getStatus()) || "CANCELLED".equals(existingBill.getStatus())) {
			throw new BadRequestException("Không thể cập nhật hóa đơn đã " + existingBill.getStatus());
		}

		existingBill.setTotalAmount(bill.getTotalAmount());
		return billRepository.save(existingBill);
	}

	// Xóa bill
	public void deleteBill(Integer id) {
		Bill bill = getBillById(id);
		if ("PENDING".equals(bill.getStatus())) {
			billRepository.deleteById(id);
		} else {
			throw new BadRequestException("Không thể xóa hóa đơn đã " + bill.getStatus());
		}
	}

	// Các phương thức tìm kiếm
	public List<Bill> getAllBills() {
		return billRepository.findAll();
	}

	public List<Bill> getBillsByStatus(String status) {
		return billRepository.findByStatus(status);
	}

	public List<Bill> getBillsByUserInfo(Integer userInfoId) {
		return billRepository.findByUserInfoID(userInfoId);
	}
}