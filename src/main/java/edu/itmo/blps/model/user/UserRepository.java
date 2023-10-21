package edu.itmo.blps.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	boolean existsByUsername(String username);
	boolean existsByUsernameAndPassword(String username, String password);
	User getByEmail(String email);
	Optional<User> findByUsername(String username);
	@Query("select us.email from User us")
	Set<String> getUserEmails();
	User getByUsername(String username);
}
