package com.example.dbaccount.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dbaccount.jwt.JwtUtil;
import com.example.dbaccount.request.UserRequest;
import com.example.dbaccount.service.LoginService;
import com.example.dbaccount.service.RegisterService;

@RestController
@RequestMapping("/api/v1")
public class UserController {
	@Autowired
	private RegisterService RegisterService;
	@Autowired
	private JwtUtil util;
	@Autowired
	private LoginService loginService;

	 @GetMapping("/get-accountID")
	    public ResponseEntity<?> getUserName(@RequestHeader("Authorization") String authorizationHeader) {
	        try {
	            String token = authorizationHeader.replace("Bearer ", "");
	            String userName = util.getUserNameFromToken(token);
	            int accountId = loginService.getId(userName);
	            return ResponseEntity.ok(accountId);
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ");
	        }
	    }

	
	@PostMapping("/login")
	// ResponseEntity dùng để đại diện cho toàn bộ phản hồi HTTP (HTTP response) trong một API. Nó giúp kiểm soát các phần của phản hồi HTTP, bao gồm:
	// Status Code (Mã trạng thái HTTP): chỉ định mã trạng thái HTTP cho phản hồi (ví dụ: 200 OK, 404 Not Found, 500 Internal Server Error).
	// Headers (Tiêu đề HTTP): tùy chỉnh các header của phản hồi (ví dụ: Content-Type, Authorization, Location, v.v.).
	// Body (Nội dung phản hồi): xác định nội dung của phản hồi (chẳng hạn như dữ liệu JSON, văn bản, hình ảnh, v.v.).
	public ResponseEntity<String> login(@RequestBody UserRequest user ) {
		String token = loginService.login(user.getUsername(), user.getPassword());

		if(token == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai username hoặc password.");
		}
		
		return ResponseEntity.ok(token);
	}

	@GetMapping("/auth")
    public boolean auth() {
        return true;
    }
	
	

	@PostMapping("/register")
	private String register(@RequestBody UserRequest user ) {

		boolean status = RegisterService.registerUser(user.getUsername(), user.getPassword(), user.getAccountType());

		if (status) {
			return "Đăng ký thành công";
		} else {
			return "Tài khoản đã tồn tại";
		}
	}
	
}
