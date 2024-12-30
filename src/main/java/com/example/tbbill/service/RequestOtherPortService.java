package com.example.tbbill.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.tbbill.config.BadRequestException;

@Service
public class RequestOtherPortService {
    @Autowired
    private RestTemplate restTemplate;
    
    private static final String TABLE_SERVICE_URL = "http://localhost:8084/api/v1/tables";
    
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
    
    public Integer calculateTotalAmount(Integer billId, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Sửa lại URL - thêm dấu / giữa total và billId
            String url = BILL_DETAIL_SERVICE_URL + "/bill/total/" + billId ;
            return restTemplate.exchange(url, HttpMethod.GET, entity, Integer.class).getBody();
        } catch (Exception e) {
            throw new BadRequestException("Lỗi khi tính tổng tiền: " + e.getMessage());
        }
    }
    
    public void deleteBillDetails(Integer billId, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        String url = BILL_DETAIL_SERVICE_URL + "/bill/" + billId;
        restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
    }
}
