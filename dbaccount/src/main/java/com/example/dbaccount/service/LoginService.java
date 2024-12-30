package com.example.dbaccount.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.dbaccount.entities.User;
import com.example.dbaccount.jwt.JwtUtil;
import com.example.dbaccount.repository.UserRepository;

@Service
public class LoginService {

	@Autowired
	private UserRepository repo;

	@Autowired
	private JwtUtil JwtUtil;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // Mã hóa mật khẩu

	public String login(String username, String password) {

		Optional<User> user = repo.findByUserName(username);

		if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
			// Nếu đăng nhập thành công, sinh JWT token và trả về
			String token = JwtUtil.generateToken(user.get().getUserName());
			return token; // Trả về token JWT
		} else {
			return "Invalid credentials"; // Nếu thông tin đăng nhập sai
		}
	}
	
	public int getId(String userName) {
		Optional<User> user = repo.findByUserName(userName);
		return user.get().getAccountId();
	}
}
