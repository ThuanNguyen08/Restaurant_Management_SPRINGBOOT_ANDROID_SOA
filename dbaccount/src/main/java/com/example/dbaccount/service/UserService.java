package com.example.dbaccount.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dbaccount.entities.User;
import com.example.dbaccount.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository repo;
	public boolean deleteByAccountId(int accountId) {
		Optional<User> userOptional = repo.findByAccountId(accountId);
		User user = userOptional.get();
		if(userOptional.isPresent()) {
			repo.delete(user);
			return true;
		}
		return false;	
	}
}
