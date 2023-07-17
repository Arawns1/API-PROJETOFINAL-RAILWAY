package br.gov.rj.teresopolis.prefeitura.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import br.gov.rj.teresopolis.prefeitura.domain.security.Role;
import br.gov.rj.teresopolis.prefeitura.domain.security.User;
import br.gov.rj.teresopolis.prefeitura.dto.security.UserDTO;
import br.gov.rj.teresopolis.prefeitura.services.UserDetailsImpl;
import br.gov.rj.teresopolis.prefeitura.services.UserService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${app.jwt.secret}")
	private String jwtSecret;

	@Value("${app.jwt.expiration.ms}")
	private int jwtExpirationMs;

	@Autowired
	UserService userService;

	public String generateJwtToken(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		SecretKey sKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

		return Jwts.builder().setSubject((userPrincipal.getUsername())).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)).signWith(sKey).compact();
	}

	public String generateJwtTokenWithUserData(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		SecretKey sKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

		User user = userService.findByUsername(userPrincipal.getUsername());
		
		UserDTO userDto = new UserDTO();
		userDto.setId(user.getId());
		userDto.setUsername(user.getUsername());
		userDto.setEmail(user.getEmail());

		Map<String, Object> userMap = new HashMap<String, Object>();
		userMap.put("user_id", userDto.getId());
		userMap.put("name", userDto.getUsername());
		userMap.put("email", userDto.getEmail());
		
		 Map<String, Object> rolesMap = new HashMap<>();
		 
		 int[] index = { 0}; 
		 authentication.getAuthorities().forEach(auth -> {
		     rolesMap.put(Integer.toString(index[0]), auth.getAuthority());
		     index[0]++;
		 });

			/*
			 * authentication.getAuthorities().forEach(authority ->
			 * System.out.println(authority));
			 */
		 userMap.put("roles", rolesMap);
		 
		return Jwts.builder()
				.setSubject(userPrincipal.getUsername())
				.setIssuedAt(new Date())
				// .claim("user", userJson)
				.setClaims(userMap)
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(sKey)
				.compact();
	}

	public String getUserNameFromJwtToken(String token) {
		try {
			SecretKey sKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
			return (String) Jwts.parserBuilder().setSigningKey(sKey).build().parseClaimsJws(token).getBody().get("name");
			/*
			 * return
			 * Jwts.parserBuilder().setSigningKey(sKey).build().parseClaimsJws(token).
			 * getBody().getSubject();
			 */
		} catch (JwtException e) {
			logger.error("Token JWT inválido: {}", e.getMessage());
		}
		return null;
	}

	public boolean validateJwtToken(String authToken) {
		try {
			SecretKey sKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
			Jwts.parserBuilder().setSigningKey(sKey).build().parseClaimsJws(authToken).getBody().getSubject();
			return true;
		} catch (JwtException e) {
			logger.error("Token JWT inválido: {}", e.getMessage());
		}
		return false;
	}
}