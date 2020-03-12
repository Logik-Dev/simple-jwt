package co.simplon.heroes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.simplon.heroes.model.User;
import co.simplon.heroes.security.JwtUtil;
import co.simplon.heroes.security.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	UserService userService;
	
	@Autowired
	JwtUtil jwtUtil;
	
	@PostMapping("/login")
	public String login(@RequestBody User user) {
		// Essayer de s'authentifier
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getPseudo(), user.getPassword()));
		}
		// Mot de passe incorrect
		catch (BadCredentialsException e) {
			throw new BadCredentialsException("Identifiants incorrects");
		}
		// Authentification réussie
		final User authenticatedUSer = userService.loadUserByUsername(user.getPseudo());
		return jwtUtil.generateToken(authenticatedUSer);
	}
	
	@PostMapping
	public User create(@RequestBody User user) {
		return userService.save(user);
	}
	
	@GetMapping("/admin")
	public String admin() {
		return "Vous êtes bien autorisé en tant qu'administrateur";
	}
}
