package com.example.dbaccount.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.dbaccount.entities.User;
import com.example.dbaccount.repository.UserRepository;
import com.example.dbaccount.request.UserRequest;

@Service
public class RegisterService {

	@Autowired
	private UserRepository repo;

	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public boolean registerUser(String username, String password , String type) {
		
		Optional<User> checkUserExist = repo.findByUserName(username);
		
		if(!checkUserExist.isPresent()) {
			// Mã hóa mật khẩu
			String encodedPassword = passwordEncoder.encode(password);

			User u = new User();

			u.setUserName(username);
			u.setPassword(encodedPassword);
			u.setAccountType(type);

			repo.save(u);
			
			Optional<User> status = repo.findByUserName(username);
			
			if (status.isPresent()) {
				return true;
			} 
			return false;
		}
		return false;
	}
	public int accountId_Register(String username) {
		Optional<User> getUser = repo.findByUserName(username);
		
		return getUser.get().getAccountId();
		
		
	}
}
