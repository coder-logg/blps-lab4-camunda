package edu.itmo.blps.service;

import edu.itmo.blps.model.basket.Basket;
import edu.itmo.blps.model.basket.BasketRepository;
import edu.itmo.blps.model.customer.Customer;
import edu.itmo.blps.model.device.Device;
import edu.itmo.blps.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
//@Secured({"ROLE_ADMIN", "ROLE_CUSTOMER"})
public class BasketService {

	@Autowired
	private DeviceService deviceService;

	@Autowired
	private BasketRepository basketRepository;

	@Autowired
	private UserService userService;

	@Transactional
	public Basket saveBasket(Basket basket) {
		return basketRepository.save(basket);
	}

	@Transactional
	public Basket createBasketForCustomerId(Integer customerId){
		Basket newBasket = new Basket();
		newBasket.setCustomerId(customerId);
		return basketRepository.save(newBasket);
	}

	@Transactional
	public Basket createBasketForCustomerUsername(String customerUsername){
		Basket newBasket = new Basket();
		newBasket.setCustomer((Customer) userService.getByUsername(customerUsername));
		return basketRepository.save(newBasket);
	}

	public Basket findByUsername(String username) {
		return basketRepository.findByCustomer_Username(username)
				.orElseThrow(() -> new EntityNotFoundException("Cart was not found for this username=" + username));
	}

	@Transactional
	public <T> Basket putDeviceInBasket(T device, String username, Function<T, Device> func) {
		Basket basket = createBasketIfAbsent(username);
		basket.addDevice(func.apply(device));
		return basketRepository.save(basket);
	}

	@Transactional
	public Basket putFewDevicesInBasket(Integer deviceId, Integer amount, String username) {
		return basketRepository.save(createBasketIfAbsent(username)
				.addFewDevices(deviceService.getDevice(deviceId), amount).updateTotalPrice(deviceService));
	}

	@Transactional
	public Basket createBasketIfAbsent(String username) {
		return basketRepository.findByCustomer_Username(username).orElseGet(() -> createBasketForCustomerUsername(username));
	}

	@Transactional
	public Basket clearBasket(String username){
		Basket basket = findByUsername(username);
		basket.clear();
		return basketRepository.save(basket);
	}
}
