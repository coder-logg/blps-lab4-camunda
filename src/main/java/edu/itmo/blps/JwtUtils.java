package edu.itmo.blps;

import edu.itmo.blps.model.user.User;
import io.jsonwebtoken.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class JwtUtils {
	private static final long EXPIRATION = Application.getApplicationContext()
			.getEnvironment().getProperty("app.jwt.expiration", Integer.class, 1440) * 1000 * 60;;
	private static final String TOKEN_SECRET = Application.getApplicationContext()
			.getEnvironment().getProperty("app.jwt.secret", "privateKey");

	public static String generateToken(User user){
		return Jwts.builder()
				.setSubject(user.getUsername())
				.signWith(SignatureAlgorithm.HS256, TOKEN_SECRET)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
				.compact();
	}

	public static Claims parseToken(String token){
		return  Jwts
				.parserBuilder()
				.setSigningKey(TOKEN_SECRET)
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	public static boolean validateAccessToken(@NonNull String accessToken) {
		try {
			return validateToken(accessToken, TOKEN_SECRET);
		} catch (JwtException e){
			return false;
		}
	}

	public static boolean validateAccessTokenOrThrow(@NonNull String accessToken) throws JwtException{
		return validateToken(accessToken, TOKEN_SECRET);
	}

	private static boolean validateToken(@NonNull String token, @NonNull String secret) {
		try {
			Jwts.parserBuilder()
					.setSigningKey(secret)
					.build()
					.parseClaimsJws(token);
			return true;
		} catch (MalformedJwtException e) {
			log.error("Invalid JWT token: {}", e.getMessage());
			throw e;
		} catch (ExpiredJwtException e) {
			log.error("JWT token is expired: {}", e.getMessage());
			throw e;
		} catch (UnsupportedJwtException e) {
			log.error("JWT token is unsupported: {}", e.getMessage());
			throw e;
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty: {}", e.getMessage());
			throw e;
		}
	}
}
