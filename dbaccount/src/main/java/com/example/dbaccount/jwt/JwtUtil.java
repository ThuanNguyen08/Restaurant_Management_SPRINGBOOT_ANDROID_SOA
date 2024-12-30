package com.example.dbaccount.jwt;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtUtil {

	// Khóa bí mật dùng để ký và xác minh chữ ký JWT
	protected static final String secretKey = "WVQJqCgK/g1cIfTjhl5P5VDoNxbEjUpLG5iIozlOM+ouJkBCPRFJArIAC9tRuhx5\r\n";

	// tạo token
	public String generateToken(String UserName) {
		
		// Tạo phần header cho JWT với thuật toán HMAC SHA-512
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

		// Tạo phần claims (dữ liệu) của JWT, bao gồm các thông tin như:    
		JWTClaimsSet jwtClaimSet = new JWTClaimsSet.Builder()
										.subject(UserName) // - subject: username của người dùng
										.issuer("phatne") // - issuer: người phát hành token
										.issueTime(new Date()) // - issueTime: thời gian phát hành token
										.expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli())) // - expirationTime: thời gian hết hạn token (1 giờ kể từ lúc phát hành)
										.claim("customeClaim", "Custom") // - claim tùy chỉnh "customeClaim": có thể chứa các thông tin bổ sung
										.build();

		// Chuyển phần claims thành payload (dữ liệu) của token
		Payload payload = new Payload(jwtClaimSet.toJSONObject());

		// Tạo đối tượng JWS (JSON Web Signature) với header và payload
		JWSObject jwsObject = new JWSObject(header, payload);

		try {
			// Ký JWT với khóa bí mật sử dụng thuật toán HMAC
			jwsObject.sign(new MACSigner(secretKey.getBytes()));
			
			// Trả về JWT đã được ký dưới dạng chuỗi
			return jwsObject.serialize();
		} catch (JOSEException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean introspect(String token) throws JOSEException, ParseException {

		// Tạo đối tượng JWSVerifier để xác minh chữ ký của token với khóa bí mật
		JWSVerifier verifier = new MACVerifier(secretKey.getBytes());

		// Chuyển đổi chuỗi token thành đối tượng SignedJWT
		SignedJWT signedJWT = SignedJWT.parse(token);

		// Lấy thời gian hết hạn của token từ phần claims trong JWT
		Date expityTime = signedJWT.getJWTClaimsSet().getExpirationTime();

		// Kiểm tra xem chữ ký của token có hợp lệ không
		boolean verified = signedJWT.verify(verifier);

		// Trả về true nếu chữ ký hợp lệ và token chưa hết hạn, ngược lại trả về false
		return verified && expityTime.after(new Date());
	}
	public String getUserNameFromToken(String token) {
	    try {
	        // Phân tích token và tạo đối tượng SignedJWT
	        SignedJWT signedJWT = SignedJWT.parse(token);
	        
	        // Trích xuất claims và lấy "subject" (username)
	        return signedJWT.getJWTClaimsSet().getSubject();
	    } catch (ParseException e) {
	        throw new RuntimeException("Token không hợp lệ", e);
	    }
	}

	
}
