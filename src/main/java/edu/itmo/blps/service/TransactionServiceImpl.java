package edu.itmo.blps.service;

import edu.itmo.blps.model.basket.Basket;
import edu.itmo.blps.model.company.Company;
import edu.itmo.blps.model.customer.Customer;
import edu.itmo.blps.model.device.Device;
import edu.itmo.blps.model.transaction.Transaction;
import edu.itmo.blps.model.transaction.TransactionRepository;
import edu.itmo.blps.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
	private final UserService userService;
	private final TransactionRepository transactionRepository;
	private final DeviceService deviceService;
	private final BasketService basketService;

	@Transactional
	public Transaction addTransactionAndClearBasket(Basket basket){
		Transaction transaction = addTransactionForOneCompany(basket);
		basketService.clearBasket(basket.getCustomer().getUsername());
		return transaction;
	}

	public Transaction addMultipleCompanyTransaction(){return null;}

	@Transactional
	public Transaction addTransactionForOneCompany(Basket basket){
//		Map<Integer, Integer> deviceIds = basket.getDevices();
		Company company = basket.getDevices().get(0).getCompany();
		Transaction transaction = Transaction.builder()
				.customer(basket.getCustomer())
				.company(company)
				.discount(0)
				.devices(basket.getDevices())
				.build();
		return addTransaction(transaction);
	}

	@Transactional
	public Transaction addTransaction(Transaction transaction) {
		Objects.requireNonNull(transaction.getCustomer());
		User customer = transaction.getCustomer();
		if (customer.getId() != null)
			customer = userService.getById(transaction.getCustomerId());
		else if (customer.getUsername() != null)
			customer = userService.getByUsername(customer.getUsername());
		else if (customer.getEmail() != null)
			customer = userService.getByEmail(customer.getEmail());
		Device tmp, device = null;
		for (Integer id: transaction.getDevicesIds()) {
			tmp = device;
			device = deviceService.getDevice(id);
//			if (tmp != null && tmp.getCompany().equals(device.getCompany())) {
				if (transaction.getCompany() == null)
					transaction.setCompany(device.getCompany());
				else if (!transaction.getCompany().equals(device.getCompany()))
					throw new AccessDeniedException("requested resource not belong for principal");
				transaction.setCustomer(customer);
//			} else throw new AccessDeniedException("requested resource not belong for principal");
		}
		Transaction transactionFromDb = transactionRepository.save(transaction);
		return transactionFromDb;
	}

	public List<Transaction> getTransactions(Integer userId){
		User user = userService.getById(userId);
		if (user instanceof Customer)
			return transactionRepository.findByCustomer_id(userId);
		else if (user instanceof Company)
			return transactionRepository.findByCompany_id(userId);
		throw new IllegalStateException();
	}
}
