package com.example.tbfood.service;

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
	
	public String getById(String token ,int id ) {
		String url = "http://localhost:8082/api/v1/dmFood/" + id;;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", token); 
		HttpEntity<String> entity = new HttpEntity<>(headers);
		return restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
	}
	
	
}
