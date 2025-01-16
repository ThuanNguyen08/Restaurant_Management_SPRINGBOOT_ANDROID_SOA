package com.example.dbaccount.controller;

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

import com.example.dbaccount.jwt.JwtUtil;
import com.example.dbaccount.request.UserRequest;
import com.example.dbaccount.service.LoginService;
import com.example.dbaccount.service.RegisterService;
import com.example.dbaccount.service.RequestOtherPortService;
import com.example.dbaccount.service.UserService;

@RestController
@RequestMapping("/api/v1")
public class UserController {
	@Autowired
	private RegisterService RegisterService;
	@Autowired
	private JwtUtil util;
	@Autowired
	private LoginService loginService;

	@Autowired
	private UserService userService;
	@Autowired
	private RequestOtherPortService requestOtherPortService;
	
	public void Authentication(String token) {
		if (!requestOtherPortService.auth(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed");
		}
	}
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

	@GetMapping("/auth/get-accountType")
	public ResponseEntity<?> getAccountType(@RequestHeader("Authorization") String authorizationHeader) {
		try {
			String token = authorizationHeader.replace("Bearer ", "");
			String userName = util.getUserNameFromToken(token);

			// Lấy accountType từ username qua service
			String accountType = loginService.getAccountType(userName);

			return ResponseEntity.ok(accountType);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ");
		}
	}

	@PostMapping("/login")
	
	public ResponseEntity<?> login(@RequestBody UserRequest user) {
		String token = loginService.login(user.getUsername(), user.getPassword());
		if(token != null) {
			return ResponseEntity.ok(token);
		}
		else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai username hoặc password.");
		}		
	}

	@GetMapping("/auth")
	public boolean auth() {
		return true;
	}

	@PostMapping("/register")
	private String register(@RequestBody UserRequest user) {

		boolean status = RegisterService.registerUser(user.getUsername(), user.getPassword(), user.getAccountType());
		
		if (status) {
			int accountId = RegisterService.accountId_Register(user.getUsername());
			requestOtherPortService.add_Info(accountId);
			return "Đăng ký thành công";
		} else {
			return "Tài khoản đã tồn tại";
		}
	}
	@DeleteMapping("/delete/{accountId}")
    public ResponseEntity<?> deleteInfo(@PathVariable int accountId, 
                                      @RequestHeader("Authorization") String authorizationHeader) {
        try {
        	Authentication(authorizationHeader);
            boolean deleted = userService.deleteByAccountId(accountId);
            if (deleted) {
            	requestOtherPortService.deleteAcountId(accountId ,authorizationHeader);
                return ResponseEntity.ok("Xóa tài khoản thành công");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy tài khoản để xóa");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xóa tài khoản: " + e.getMessage());
        }
    }
}
