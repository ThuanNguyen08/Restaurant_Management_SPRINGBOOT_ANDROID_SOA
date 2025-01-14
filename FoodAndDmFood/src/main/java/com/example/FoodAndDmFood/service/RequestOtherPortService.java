package com.example.FoodAndDmFood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RequestOtherPortService {

	@Autowired
	private RestTemplate restTemplate;

	public Boolean auth(String token) {
		String url = "http://localhost:8080/api/v1/auth";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", token); 
		HttpEntity<String> entity = new HttpEntity<>(headers);

		return restTemplate.exchange(url, HttpMethod.GET, entity, Boolean.class).getBody();
	}
	
	public void deleteFoodsByCategory(String token, int dmFoodId) {
	    String url = "http://localhost:8083/api/v1/food/category/delete/" + dmFoodId;
	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Authorization", token); 
	    HttpEntity<String> entity = new HttpEntity<>(headers);
	    
	    restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
	}
}
