package com.example.tbuserinfo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.tbuserinfo.entities.InfoUser;
import com.example.tbuserinfo.repository.InfoUserRepository;
import com.example.tbuserinfo.request.InfoUserRequest;

@Service
 public class InfoUserService {

	
	@Autowired
	private InfoUserRepository repo;
	
	public List<InfoUser> getAll(){
		return repo.findAll();
	}
	
	public boolean addInfo(InfoUserRequest userRequest , int id) {
		Optional<InfoUser> existingUser = repo.findByAccountId(id);
		InfoUser user = new InfoUser();
	    if (existingUser.isPresent()) {
	    	user = existingUser.get();
	    	user.setFullName(userRequest.getFullName());
	        user.setEmail(userRequest.getEmail());
	        user.setPhoneNumber(userRequest.getPhoneNumber());
	        user.setSex(userRequest.getSex());
	    } else {
	        user = new InfoUser();
	        user.setAccountId(id);
	        user.setFullName(userRequest.getFullName());
	        user.setEmail(userRequest.getEmail());
	        user.setPhoneNumber(userRequest.getPhoneNumber());
	        user.setSex(userRequest.getSex());
	    }
	    repo.save(user);
	    return true;
	}
	
	public InfoUser getById(int accountId) {
		Optional<InfoUser> userById = repo.findByAccountId(accountId);
		InfoUser user = new InfoUser();
		user = userById.get();
		return user;
	}
	
	public int getUserID(int accountId) {
		Optional<InfoUser> userById = repo.findByAccountId(accountId);
		return userById.get().getUserInfoId();
	}
}
