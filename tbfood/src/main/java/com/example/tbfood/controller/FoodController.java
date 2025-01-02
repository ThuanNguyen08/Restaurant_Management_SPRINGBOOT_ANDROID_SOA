package com.example.tbfood.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.tbfood.entities.Food;
import com.example.tbfood.repository.FoodRepository;
import com.example.tbfood.service.FoodService;
import com.example.tbfood.service.RequestOtherPortService;

@RestController
@RequestMapping("/api/v1/food")
public class FoodController {
	
	@Autowired
	private FoodService service;
	
	@Autowired
	private FoodRepository foodRepository;
	
	@Autowired
	private RequestOtherPortService RequestOtherPortService;
	
	public void Authentication(String token) {
		if (!RequestOtherPortService.auth(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed");
		}
	}

	@GetMapping
	public List<Food> getAll(@RequestHeader("Authorization") String token) {
		Authentication(token);
		return service.getAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@RequestHeader("Authorization") String token  , @PathVariable int id){
		try {
			Authentication(token);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(service.findById(id));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Có lỗi xảy ra rồi ");
		}
	}
	

	
	@GetMapping("/category/{dmFoodID}")
	public ResponseEntity<?> getFoodsByCategory(@RequestHeader("Authorization") String token, @PathVariable int dmFoodID) {
	    try {
	        // Xác thực token
	        Authentication(token);

	        // Lấy danh sách món ăn theo danh mục
	        List<Food> foods = service.getFoodsByCategory(dmFoodID);

	        // Kiểm tra danh sách có rỗng hay không
	        if (foods == null || foods.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy món ăn nào trong danh mục này.");
	        }

	        // Trả về danh sách món ăn
	        return ResponseEntity.ok(foods);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra: " + e.getMessage());
	    }
	}

	@PostMapping("/add")
    public ResponseEntity<?> addFood(@RequestHeader("Authorization") String token, @RequestBody Food food) {
        try {
            Authentication(token);
            String categoryName = RequestOtherPortService.getById(token, food.getDmFoodID());
            
            if(categoryName != null && categoryName.length() > 0) {
                service.addFood(food);
                return ResponseEntity.ok("Thêm thành công món ăn: " + food.getFoodName());
            } else {
                return ResponseEntity.badRequest().body("Danh mục chưa tồn tại");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra: " + e.getMessage());
        }
    }
	
	@PutMapping("/update/{foodId}")
	public ResponseEntity<?> updateFood(@RequestHeader("Authorization") String token, @PathVariable int foodId, @RequestBody Food updatedFood) {
	    try {
	        // Xác thực token
	        Authentication(token);

	        // Tìm món ăn theo ID
	        Food existingFood = service.findById(foodId);
	        if (existingFood == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy món ăn với ID: " + foodId);
	        }

	        // Cập nhật thông tin món ăn
	        existingFood.setFoodName(updatedFood.getFoodName());
	        existingFood.setPrice(updatedFood.getPrice());
	        existingFood.setDmFoodID(updatedFood.getDmFoodID());

	        // Lưu lại thay đổi
	        service.updateFood(existingFood);

	        return ResponseEntity.ok("Cập nhật món ăn thành công: " + existingFood.getFoodName());
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra: " + e.getMessage());
	    }
	}
	
	@DeleteMapping("/{foodID}")
	public ResponseEntity<?> deleteFood(@RequestHeader("Authorization") String token, @PathVariable int foodID) {
	    try {
	        // Xác thực token
	        Authentication(token);

	        // Xóa món ăn theo ID
	        boolean isDeleted = service.deleteFood(foodID);

	        if (isDeleted) {
	            return ResponseEntity.ok("Xóa món ăn thành công với ID: " + foodID);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy món ăn với ID: " + foodID);
	        }
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra: " + e.getMessage());
	    }
	}
	/// Xóa tất cả món ăn theo danh mục
	@DeleteMapping("/category/delete/{dmFoodID}")
	public ResponseEntity<?> deleteFoodsByCategory(@RequestHeader("Authorization") String token, @PathVariable int dmFoodID) {
	    try {
	        Authentication(token);
	        
	        List<Food> foods = service.getFoodsByCategory(dmFoodID);
	       
	        for (Food food : foods) {
	            service.deleteFood(food.getFoodID());
	        }
	        
	        return ResponseEntity.ok("Đã xóa tất cả món ăn thuộc danh mục: " + dmFoodID);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Có lỗi xảy ra khi xóa món ăn: " + e.getMessage());
	    }
	
	}
}
