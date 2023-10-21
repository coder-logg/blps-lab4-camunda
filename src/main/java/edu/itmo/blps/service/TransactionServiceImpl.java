package edu.itmo.blps.service;

import edu.itmo.blps.model.basket.Basket;
import edu.itmo.blps.model.company.Company;
import edu.itmo.blps.model.customer.Customer;
import edu.itmo.blps.model.device.Device;
import edu.itmo.blps.model.transaction.Transaction;
import edu.itmo.blps.model.transaction.TransactionRepository;
import edu.itmo.blps.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
	private final UserService userService;
	private final TransactionRepository transactionRepository;
	private final DeviceService deviceService;
	private final BasketService basketService;

	@Transactional
	public Transaction addSingleTransactionAndClearBasket(Basket basket){
		Transaction transaction = addSingleCompanyTransaction(
				deviceService.getDevice(basket.getDevicesIds().get(0)).getCompany(),
				basket.getCustomer(),
				basket.getDevices());
		basketService.clearBasket(basket.getCustomer().getUsername());
		return transaction;
	}

	@Transactional
	public List<Transaction> addMultipleTransactionsAndClearBasket(Basket basket){
		Map<Company, Map<Integer, Integer>> collect = basket.getDevices().keySet().stream()
				.map(deviceService::getDevice)
				.collect(Collectors.groupingBy(Device::getCompany,
						Collectors.toMap(Device::getId, a -> basket.getDevices().get(a.getId()))));
		List<Transaction> transactions = addMultipleCompanyTransaction(collect,basket.getCustomer());
		basketService.clearBasket(basket.getCustomer().getUsername());
		return transactions;
	}

	@Transactional
	public List<Transaction> addMultipleCompanyTransaction(
			Map<Company, Map<Integer, Integer>> companyDevicesAmounts, Customer customer){
		List<Transaction> result = new ArrayList<>();
		companyDevicesAmounts.keySet().forEach(a -> {
			result.add(addSingleCompanyTransaction(a, customer, companyDevicesAmounts.get(a)));
		});
		return result;
	}

	@Transactional
	public Transaction addSingleCompanyTransaction(Company company, Customer customer, Map<Integer, Integer> devicesAmounts){
		Transaction transaction = Transaction.builder()
				.customer(customer)
				.company(company)
				.discount(0)
				.devices(devicesAmounts)
				.dateTime(new Date())
				.build();
		log.info("new transaction will be added: {}", transaction);
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
