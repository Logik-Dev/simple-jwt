package co.simplon.heroes.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import co.simplon.heroes.model.User;

public interface UserRepository extends CrudRepository<User, Integer> {
	Optional<User> findByPseudo(String pseudo);
}
