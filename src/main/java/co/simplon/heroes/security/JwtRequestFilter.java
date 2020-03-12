package co.simplon.heroes.security;


import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import co.simplon.heroes.model.User;


@Configuration
public class JwtRequestFilter extends OncePerRequestFilter{

	@Autowired
	private UserService userService;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		// Récupérer le header authorization
		final String authorizationHeader = request.getHeader("Authorization");
		String pseudo = null;
		String jwt = null;
		
		// Si le header n'est pas null et qu'il commence par Bearer on récupere le token
		// et on extrait le pseudo du token avec JwtUtil
		if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			jwt = authorizationHeader.substring(7);
			pseudo = jwtUtil.extractPseudo(jwt);
		}
		
		// Si le pseudo n'est pas null et qu'il n'y a aucune authentification dans le contexte
		// on vérifie la validité du token avec JwtUtil
		if(pseudo != null  && SecurityContextHolder.getContext().getAuthentication() == null) {
			User user = this.userService.loadUserByUsername(pseudo);
			
			// Si le token est valide on met l'authentification dans le contexte
			if(jwtUtil.validateToken(jwt, user)) {
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
						new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
				usernamePasswordAuthenticationToken
					.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				
			}
		}
		// On passe la main au prochain filtre
		filterChain.doFilter(request, response);
	}

}