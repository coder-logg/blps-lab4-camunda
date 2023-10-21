package edu.itmo.blps.camunda.delegate;

import edu.itmo.blps.model.company.Company;
import edu.itmo.blps.model.customer.Customer;
import edu.itmo.blps.model.user.Role;
import edu.itmo.blps.model.user.User;
import edu.itmo.blps.model.user.UserRepository;
import edu.itmo.blps.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.validation.ValidationException;

@Component
@Slf4j
public class RegistrationDelegate implements JavaDelegate {

	@Autowired
	private AuthService authService;

	@Override
	@Transactional
	public void execute(DelegateExecution execution) {
		log.info("Request for registration: variables={}", execution.getVariables());
		Role role = Role.valueOf(((String) execution.getVariable("role")).toUpperCase());
		String username = (String) execution.getVariable("username");
		String password = (String) execution.getVariable("password");
		String email = (String) execution.getVariable("email");
		User userFromDb = null;
		try {
			switch (role) {
				case COMPANY:
					userFromDb = authService.register(Company.builder()
							.name((String) execution.getVariable("company_name"))
							.description((String) execution.getVariable("company_description"))
							.username(username)
							.password(password)
							.email(email)
							.build());
					break;
				case CUSTOMER:
					userFromDb = authService.register(Customer.builder()
							.firstName((String) execution.getVariable("firstname"))
							.lastName((String) execution.getVariable("lastname"))
							.deliveryAddress((String) execution.getVariable("delivery_address"))
							.password(password)
							.username(username)
							.email(email)
							.build());
			}
		} catch (IllegalArgumentException | EntityExistsException ex) {
			throw new BpmnError("registrationError", ex.getMessage());
		} catch (ValidationException ex) {
			throw new BpmnError("registrationError", "invalid data was given: " + ex.getMessage());
		}
		if (userFromDb != null)
			execution.setVariable("userId", userFromDb.getId());
	}
}
