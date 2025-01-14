package com.example.tbuserinfo.controller;

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

import com.example.tbuserinfo.entities.InfoUser;
import com.example.tbuserinfo.request.InfoUserRequest;
import com.example.tbuserinfo.service.InfoUserService;
import com.example.tbuserinfo.service.RequestOtherPortService;

@RestController
@RequestMapping("/api/v1/infoUser")
public class InfoUserController {

	@Autowired
	private InfoUserService service;

	@Autowired
	private RequestOtherPortService RequestOtherPortService;

	public void Authentication(String token) {
		if (!RequestOtherPortService.auth(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed");
		}
	}

	@GetMapping()
	public List<InfoUser> getAllInfo(@RequestHeader("Authorization") String token) {
			Authentication(token);
			return service.getAll();
	}

	@GetMapping("/getId")
	public ResponseEntity<?> getByUserId(@RequestHeader("Authorization") String token) {

		try {
			Authentication(token);
			int accountId = RequestOtherPortService.getId(token);
			int id = service.getUserID(accountId);
			return ResponseEntity.status(HttpStatus.OK).body(id);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Có lỗi xảy ra rồi ");
		}
	}

	@GetMapping("/info")
	public ResponseEntity<?> getByAccountId(@RequestHeader("Authorization") String token) {
		try {
			Authentication(token);
			int accountId = RequestOtherPortService.getId(token);
			InfoUser user = new InfoUser();
			user = service.getById(accountId);
			return ResponseEntity.ok(user);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Có lỗi xảy ra rồi ");
		}

	}
	//Hàm tạo nội bộ ban đầu
	@PostMapping("/initial-info/{accountId}")
	public ResponseEntity<?> addInitialInfo(@RequestBody InfoUserRequest userRequest ,@PathVariable Integer accountId ) {
		try {
			boolean addInfo = service.addInfo(userRequest, accountId);
			if (addInfo) {
				return ResponseEntity.status(HttpStatus.OK).body("Thêm thành công");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Có lỗi xảy ra khi thêm");
			}

		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Server có lỗi ");
		}
	}
	@PostMapping("/add")
	public ResponseEntity<?> addInfo(@RequestBody InfoUserRequest userRequest,
			@RequestHeader("Authorization") String token) {
		try {
			Authentication(token);
			int accountId = RequestOtherPortService.getId(token);
			boolean addInfo = service.addInfo(userRequest, accountId);
			if (addInfo) {
				return ResponseEntity.status(HttpStatus.OK).body("Thêm thành công");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Có lỗi xảy ra khi thêm");
			}

		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Server có lỗi ");
		}

	}
	@DeleteMapping("/{userInfoId}")
    public ResponseEntity<?> deleteInfoByUserInfoID(@PathVariable int userInfoId, 
                                      @RequestHeader("Authorization") String token) {
        try {
            Authentication(token);
            
            boolean deleted = service.deleteInfo(userInfoId);
            if (deleted) {
                return ResponseEntity.ok("Xóa tài khoản thành công");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy tài khoản để xóa");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xóa tài khoản: " + e.getMessage());
        }
    }
	
	@DeleteMapping("/ByAcountId/{accountId}")
    public ResponseEntity<?> deleteInfoByAccountId(@PathVariable int accountId, 
                                      @RequestHeader("Authorization") String token) {
        try {
        	 Authentication(token);
            boolean deleted = service.deleteByAccountId(accountId);
            if (deleted) {
                return ResponseEntity.ok("Xóa tài khoản thành công");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy tài khoản để xóa");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xóa tài khoản: " + e.getMessage());
        }
    }

}
