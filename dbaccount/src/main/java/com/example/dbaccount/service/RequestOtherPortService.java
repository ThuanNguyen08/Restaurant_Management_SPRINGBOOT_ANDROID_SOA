package com.example.dbaccount.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.dbaccount.request.InfoUserRequest;

@Service
public class RequestOtherPortService {
	@Autowired
	private RestTemplate restTemplate;

	private static final String Add_Info = "http://localhost:8081/api/v1/infoUser/initial-info/";
	private static final String delete_Account = "http://localhost:8081/api/v1/infoUser/ByAcountId/";
	public void add_Info(int accountId) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		InfoUserRequest defaultInfo = new InfoUserRequest();
		defaultInfo.setFullName("");
		defaultInfo.setSex("");
		defaultInfo.setEmail("");
		defaultInfo.setPhoneNumber("");
		HttpEntity<InfoUserRequest> entity = new HttpEntity<>(defaultInfo, headers);
		String url = Add_Info + accountId;
		restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
	}

	
	public void deleteAcountId(int accountId, String token) {  
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.set("Authorization", token);  
	    
	    HttpEntity<InfoUserRequest> entity = new HttpEntity<>(headers);
	    String url_delete = delete_Account + accountId;
	    
	    restTemplate.exchange(
	        url_delete, 
	        HttpMethod.DELETE, 
	        entity, 
	        String.class
	    );
	    }
	public Boolean auth(String token) {
		String url = "http://localhost:8080/api/v1/auth";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", token); 
		HttpEntity<String> entity = new HttpEntity<>(headers);

		return restTemplate.exchange(url, HttpMethod.GET, entity, Boolean.class).getBody();
	}
}
