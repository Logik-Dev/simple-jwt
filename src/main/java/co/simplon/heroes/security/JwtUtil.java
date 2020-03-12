package co.simplon.heroes.security;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, user.getUsername());
	}
	
	// Créer le token avec le pseudo et définir la date d'expiration
	private String createToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 heures
				.signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
	}
	
	// Vérifier la validité du token 
	public Boolean validateToken(String token, User user) {
		final String pseudo = extractPseudo(token);
		return (pseudo.equals(user.getUsername()) && !isTokenExpired(token));
		
	}
	
	// Extraire le pseudo du token
	public String extractPseudo(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	// Méthode générique pour extraire un des champs du token
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	
	// Extraire tout les champs du token
	private Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
	}
	
	// Extraire la date d'expiration du token
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
	
	// Savoir si le token à expiré
	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}
}