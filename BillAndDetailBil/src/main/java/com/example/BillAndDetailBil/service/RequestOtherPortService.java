package com.example.BillAndDetailBil.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.BillAndDetailBil.config.ResourceNotFoundException;

@Service
public class RequestOtherPortService {
    @Autowired
    private RestTemplate restTemplate;
    
    private static final String TABLE_SERVICE_URL = "http://localhost:8084/api/v1/tables";
    private static final String FOOD_SERVICE_URL = "http://localhost:8083/api/v1/food";
    private static final String BILL_DETAIL_SERVICE_URL = "http://localhost:8086/api/v1/billdetails";
    public Boolean auth(String token) {
        String url = "http://localhost:8080/api/v1/auth";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token); 
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, Boolean.class).getBody();
    }

    public Map<String, Object> getTableById(Integer tableId, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        String url = TABLE_SERVICE_URL + "/" + tableId;
        return restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
    }

    public void updateTableStatus(Integer tableId, String status, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(status, headers);
        
        String url = TABLE_SERVICE_URL + "/" + tableId;
        restTemplate.exchange(url, HttpMethod.PUT, entity, Object.class);
    }
    
 
    public int getId(String token) {
		String url = "http://localhost:8080/api/v1/get-accountID";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", token); 
		HttpEntity<String> entity = new HttpEntity<>(headers);
		return restTemplate.exchange(url, HttpMethod.GET, entity, Integer.class).getBody();
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
}
