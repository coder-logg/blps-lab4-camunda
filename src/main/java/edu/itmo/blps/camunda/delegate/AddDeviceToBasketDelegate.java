package edu.itmo.blps.camunda.delegate;

import edu.itmo.blps.JwtUtils;
import edu.itmo.blps.model.basket.Basket;
import edu.itmo.blps.service.AuthService;
import edu.itmo.blps.service.BasketService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.Spin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AddDeviceToBasketDelegate implements JavaDelegate {

	@Autowired
	private BasketService basketService;

	@Autowired
	private AuthService authService;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		log.info("Request for add device to basket: variables={}", execution.getVariables());
		String token = (String) execution.getVariable(HttpHeaders.AUTHORIZATION);
		if (!JwtUtils.validateAccessToken(token))
			throw new BpmnError("jwtCorrupted");
		Basket basket = basketService.putDeviceInBasket((Integer) execution.getVariable("deviceId"),
				JwtUtils.parseToken(token).getSubject());
//		execution.setVariable("basket", Spin.JSON(basket));
	}
}
