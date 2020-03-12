package co.simplon.heroes.security;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import co.simplon.heroes.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


@Service
public class JwtUtil {
	
	private final String SECRET_KEY = "ultrasecretagent007";

	// On appelle cette méthode si l'utilisateur est authentifié en lui passant l'objet User
	public String generateToken(User user) {
		Map<String, Object> payload = new HashMap<>();
		String pseudo = user.getPseudo();
		
		// création du token
		return Jwts.builder().setClaims(payload).setSubject(pseudo).setIssuedAt(new Date(System.currentTimeMillis()))
				
				// Expire dans 10 heures
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
				
				// Signer le token avec la clef secrete
				.signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
	}
	
	// Extraire le payload du token
	private Claims extractPayload(String token) {
		return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
	}
	
	// Extraire le pseudo du token
	public String extractPseudo(String token) {
		return extractPayload(token).getSubject();
	}
		
	// Extraire la date d'expiration du token
	public Date extractExpiration(String token) {
		return extractPayload(token).getExpiration();
	}
	
	// Savoir si le token à expiré
	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}
	
	// Vérifier la validité du token 
	public Boolean validateToken(String token, User user) {
		final String pseudo = extractPseudo(token);
		return (pseudo.equals(user.getUsername()) && !isTokenExpired(token));	
	}
	
}