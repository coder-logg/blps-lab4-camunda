package edu.itmo.blps.camunda.delegate;

import edu.itmo.blps.model.user.User;
import edu.itmo.blps.service.AuthService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class SignInDelegate implements JavaDelegate {
	@Autowired
	private AuthService authService;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		User user = new User((String) execution.getVariable("username"), (String) execution.getVariable("password"));
		String token = authService.signIn(user);
		execution.setVariable(HttpHeaders.AUTHORIZATION, token);
	}
}
