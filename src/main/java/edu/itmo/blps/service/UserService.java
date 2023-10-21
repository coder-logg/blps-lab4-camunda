package edu.itmo.blps.service;

import edu.itmo.blps.model.company.Company;
import edu.itmo.blps.model.company.CompanyRepository;
import edu.itmo.blps.model.user.User;
import edu.itmo.blps.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
	private final UserRepository userRepository;
	private final CompanyRepository companyRepository;

	@Autowired
	@Lazy
	private PasswordEncoder encoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("'" + username + "' doesn't exist"));
	}

	public List<Company> getAllCompanies(Integer pageNo, Integer pageSize, String sortBy) {
		return companyRepository.findAll(PageRequest.of(pageNo, pageSize, Sort.by(sortBy))).getContent();
	}

	public User getById(Integer userId) {
		return userRepository.getById(userId);
	}

	public Set<User> getAllById(Set<Integer> ids){
		return ids.stream().map(this::getById).collect(Collectors.toSet());
	}

	public User getByUsername(String username) {
		return userRepository.getByUsername(username);
	}

	@Transactional
	public User save(User user) {
		return userRepository.save(user);
	}

	public User getByEmail(String email) {
		return userRepository.getByEmail(email);
	}

	public Set<String> getUserEmails() {
		return userRepository.getUserEmails();
	}

	@Transactional
	public User updateProfile(User user, String username) {
		// todo try-catch for username sql-constraint
		User old = getByUsername(username);
		if (old != null) {
			user.setId(old.getId());
			user.setPassword(encoder.encode(user.getPassword()));
			User changed = userRepository.save(user);
			return changed;
		}
		else throw new UsernameNotFoundException("Username '" + username + "' was not found");
	}

	@Transactional
	public User saveIfNotExists(User user) throws EntityExistsException {
		if (userRepository.existsByUsername(user.getUsername()))
			throw new EntityExistsException("User with username '" + user.getUsername() + "' already exists");
		return save(user);

	}
}
