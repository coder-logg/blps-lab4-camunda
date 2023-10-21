package edu.itmo.blps.service;

import edu.itmo.blps.JwtUtils;
import edu.itmo.blps.model.user.User;
import edu.itmo.blps.model.user.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
public class AuthService {
	@Autowired
	private UserService userService;

	@Autowired
	private BasketService basketService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder encoder;

	public String signIn(User user) throws AuthenticationException {
		final User userFromDb = (User) userService.loadUserByUsername(user.getUsername());
		if (encoder.matches(user.getPassword(), userFromDb.getPassword()))
			return JwtUtils.generateToken(userFromDb);
		else
			throw new BadCredentialsException("Invalid password");
	}

	@Transactional
	public User register(User newUser) {
		log.info("new user trying to register {}", newUser);
		boolean isValidStrings = Stream.of(newUser.getUsername(), newUser.getPassword())
				.noneMatch((x) -> Objects.isNull(x) | x.isEmpty());
		if (!isValidStrings)
			throw new IllegalArgumentException("You entered illegal username or password");
		newUser.setPassword(new BCryptPasswordEncoder().encode(newUser.getPassword()));
		User userFromDb = userService.saveIfNotExists(newUser);
		return userFromDb;
	}

	public Optional<String> getAccessToken(String refreshToken) {
		if (JwtUtils.validateAccessToken(refreshToken)) {
			final Claims claims = JwtUtils.parseToken(refreshToken);
			return Optional.of(JwtUtils.generateToken((User) userService.loadUserByUsername(claims.getSubject())));
		}
		return Optional.empty();
	}
}
