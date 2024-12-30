package com.example.tbbilldetail.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.tbbilldetail.config.ResourceNotFoundException;

@Service
public class RequestOtherPortService {
	 @Autowired
	    private RestTemplate restTemplate;
	    
	    private static final String BILL_SERVICE_URL = "http://localhost:8085/api/v1/bills";
	    private static final String FOOD_SERVICE_URL = "http://localhost:8083/api/v1/food";
	    private static final String AUTH_URL = "http://localhost:8080/api/v1/auth";

	    public Boolean auth(String token) {
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Authorization", token);
	        HttpEntity<String> entity = new HttpEntity<>(headers);
	        return restTemplate.exchange(AUTH_URL, HttpMethod.GET, entity, Boolean.class).getBody();
	    }

	    
	    // Kiểm tra bill tồn tại
	    public Map<String, Object> getBillById(Integer billId, String token) {
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Authorization", token);
	        HttpEntity<String> entity = new HttpEntity<>(headers);
	        String url = BILL_SERVICE_URL + "/" + billId;
	        return restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
	    }
	    public String getFoodInfo(Integer foodId, String token) {
	        try {
	            if (!token.startsWith("Bearer ")) {
	                token = "Bearer " + token;
	            }

	            HttpHeaders headers = new HttpHeaders();
	            headers.set("Authorization", token);
	            HttpEntity<String> entity = new HttpEntity<>(headers);
	            
	            try {
	                ResponseEntity<String> response = restTemplate.exchange(
	                    FOOD_SERVICE_URL + "/" + foodId, 
	                    HttpMethod.GET, 
	                    entity, 
	                    String.class
	                );
	                return response.getBody();
	            } catch (HttpClientErrorException e) {
	                // Nếu response có dữ liệu thì trả về luôn, không throw exception
	                if (e.getResponseBodyAsString() != null && !e.getResponseBodyAsString().isEmpty()) {
	                    return e.getResponseBodyAsString();
	                }
	                throw new ResourceNotFoundException("Không tìm thấy món ăn với id: " + foodId);
	            }
	        } catch (ResourceNotFoundException e) {
	            throw e;
	        } catch (Exception e) {
	            throw new ResourceNotFoundException("Không thể lấy thông tin món ăn: " + e.getMessage());
	        }
	    }
	
	    // Cập nhật tổng tiền trong bill
	    public void updateBillTotal(Integer billId, Integer totalAmount, String token) {
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Authorization", token);
	        Map<String, Integer> body = Map.of("totalAmount", totalAmount);
	        HttpEntity<Map<String, Integer>> entity = new HttpEntity<>(body, headers);
	        String url = BILL_SERVICE_URL + "/" + billId;
	        restTemplate.exchange(url, HttpMethod.PUT, entity, Map.class);
	    }
	}