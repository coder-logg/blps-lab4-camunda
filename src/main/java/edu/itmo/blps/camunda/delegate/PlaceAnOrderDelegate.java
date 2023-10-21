package edu.itmo.blps.camunda.delegate;

import edu.itmo.blps.JwtUtils;
import edu.itmo.blps.model.transaction.Transaction;
import edu.itmo.blps.service.BasketService;
import edu.itmo.blps.service.TransactionServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.Spin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class PlaceAnOrderDelegate implements JavaDelegate {
	@Autowired
	private BasketService basketService;

	@Autowired
	private TransactionServiceImpl transactionService;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		log.info("Request for place an order: variables={}", execution.getVariables());
		String token = (String) execution.getVariable(HttpHeaders.AUTHORIZATION);
		if (!JwtUtils.validateAccessToken(token))
			throw new BpmnError("jwtCorrupted");;
		List<Transaction> transactions = transactionService.
				addMultipleTransactionsAndClearBasket(basketService.findByUsername(JwtUtils.parseToken(token).getSubject()));
		execution.setVariable("requestResult", Spin.JSON(transactions));
	}
}
