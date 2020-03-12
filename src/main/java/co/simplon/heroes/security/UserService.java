package co.simplon.heroes.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.simplon.heroes.model.User;
import co.simplon.heroes.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public User loadUserByUsername(String pseudo) throws UsernameNotFoundException {
		User user = userRepository.findByPseudo(pseudo)
				.orElseThrow(() -> new UsernameNotFoundException("L'utilisateur n'existe pas"));
		return user;
	}
	
	public User save(User user) {
		// Hash du mot de passe avant de sauvegarder l'utilisateur en base de donnée
		String hash = passwordEncoder.encode(user.getMotDePasse());
		user.setMotDePasse(hash);
		return userRepository.save(user);
	}

}
