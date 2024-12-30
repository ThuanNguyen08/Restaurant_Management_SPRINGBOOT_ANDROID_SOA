package com.example.dbaccount.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.example.dbaccount.filter.JwtFilter;
import com.example.dbaccount.jwt.JwtUtil;

@Component
public class FilterConfig {

	@Bean //đánh dấu phương thức này sẽ tạo ra một bean trong Spring Application Context. Bean này có thể được quản lý và sử dụng bởi Spring Framework.
	// FilterRegistrationBean<JwtFilter>: đăng ký một bộ lọc (filter) vào trong ứng dụng để thực hiện một số công việc như kiểm tra tính hợp lệ của yêu cầu, ghi lại nhật ký, xử lý các vấn đề bảo mật, v.v.
	// JwtFilter là bộ lọc tùy chỉnh
	// jwtFilter(JwtUtil jwtUtil): khai báo một FilterRegistrationBean<JwtFilter> cho bộ lọc JwtFilter, nhận một tham số JwtUtil - một lớp tiện ích dùng để xử lý các công việc liên quan đến JWT, như giải mã token và xác thực.
	// JwtUtil có thể được sử dụng bên trong JwtFilter để kiểm tra xem token có hợp lệ không và để xử lý các tác vụ liên quan đến xác thực người dùng.
	public FilterRegistrationBean<JwtFilter> jwtFilter(JwtUtil jwtUtil) {
		FilterRegistrationBean<JwtFilter> registrationBean = new FilterRegistrationBean<>();
		
		// thiết lập bộ lọc cho registrationBean. 
		// Bộ lọc được tạo ra là một đối tượng của lớp JwtFilter và JwtUtil sẽ được truyền vào JwtFilter để giúp nó thực hiện các công việc cần thiết với JWT.
		// JwtFilter sẽ là nơi xử lý logic xác thực JWT từ các yêu cầu HTTP.
		registrationBean.setFilter(new JwtFilter(jwtUtil));
		
		// chỉ định rằng bộ lọc JwtFilter chỉ áp dụng cho các URL có tiền tố /auth/
		registrationBean.addUrlPatterns("/api/v1/auth/*");
		
		// trả về đối tượng registrationBean, đăng ký bộ lọc vào Spring Context
		// Spring Context (hoặc Spring Application Context) đóng vai trò như là một container quản lý các đối tượng và bean trong ứng dụng. 
		// giúp Spring quản lý và cấu hình các đối tượng của ứng dụng, cung cấp các dịch vụ cần thiết để các đối tượng này có thể làm việc với nhau.
		return registrationBean;
	}
}
