package com.example.dbaccount.filter;

import java.io.IOException;

import com.example.dbaccount.jwt.JwtUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtFilter implements Filter {

	private final JwtUtil JwtUtil;

	public JwtFilter(JwtUtil jwtUtil) {
		this.JwtUtil = jwtUtil;
	}

	@Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

		 	HttpServletRequest httpRequest = (HttpServletRequest) request;
	        HttpServletResponse httpResponse = (HttpServletResponse) response;
	        String token = httpRequest.getHeader("Authorization");

	        // Nếu không có token, trả về lỗi
	        if (token == null || !token.startsWith("Bearer ")) {
	        	//thiết lập mã trạng thái HTTP của phản hồi (response) mà server gửi lại cho client là 401 Unauthorized
	            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	            //gửi một thông báo lỗi (message) về client trong body của phản hồi HTTP (response) khi mã trạng thái là 401 Unauthorized
	            httpResponse.getWriter().write("Token does not exist or is invalid");
	            return;
	        }

	        // Loại bỏ "Bearer " khỏi token
	        token = token.substring(7);

	        try {
	            // Kiểm tra và xác thực token
	            if (JwtUtil.introspect(token)) {
	            	
	                // Token hợp lệ, tiếp tục xử lý request
	                chain.doFilter(request, response);
	            } else {
	            	
	                // Token không hợp lệ
	                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	                httpResponse.getWriter().write("Token invalid");
	            }
	        } catch (Exception e) {
	            // Xử lý ngoại lệ nếu có
	            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	            httpResponse.getWriter().write("Error processing token: " + e.getMessage());
	        }
    }
}
