package com.example.tbdmfood.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.tbdmfood.entities.DmFood;
import com.example.tbdmfood.service.DmFoodService;
import com.example.tbdmfood.service.RequestOtherPortService;

@RestController
@RequestMapping("/api/v1/dmFood")
public class DmFoodController {

	@Autowired
	private DmFoodService service;

	@Autowired
	private RequestOtherPortService RequestOtherPortService;

	public void Authentication(String token) {
		if (!RequestOtherPortService.auth(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed");
		}
	}

	@GetMapping
	public List<DmFood> getAllDmFood(@RequestHeader("Authorization") String token) {
		Authentication(token);
		return service.getAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<String> getById(@RequestHeader("Authorization") String token ,@PathVariable int  id) {
		try {
			Authentication(token);
			if (service.getById(id).length() > 0) {
				return ResponseEntity.status(HttpStatus.OK).body(service.getById(id));
			} else {
				return ResponseEntity.status(HttpStatus.OK).body("Id không tồn tại");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Có lỗi xảy ra rồi ");
		}
	}

	@PostMapping("/add")
	public ResponseEntity<?> addDmFood(@RequestHeader("Authorization") String token, @RequestBody DmFood dmFood) {
		try {
			Authentication(token);
			if (service.add(dmFood)) {
				return ResponseEntity.status(HttpStatus.OK).body("Thêm mới danh mục thành công");
			} else {
				return ResponseEntity.status(HttpStatus.OK).body("tên danh mục đã tồn tại");
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Có lỗi xảy ra rồi ");
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteById(@RequestHeader("Authorization") String token,@PathVariable int id) {
		try {
			Authentication(token);
			if (service.deleteById(id)) {
				return ResponseEntity.status(HttpStatus.OK).body("Xóa thành công");
			} else {
				return ResponseEntity.status(HttpStatus.OK).body("Xóa thất bại");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Có lỗi xảy ra rồi ");
		}
	}

}
