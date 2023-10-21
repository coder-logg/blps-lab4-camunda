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

//	public Map<Device, Integer> getAllDevices(String username){
//		Map<Integer, Integer> ids = findByUsername(username).getDevices();
//		Map<Device, Integer> res = new HashMap<>();
//		ids.forEach((k, v) -> res.put(deviceService.getDevice(k), v));
//		return res;
//	}

	public Basket findByUsername(String username) {
		return basketRepository.findByCustomer_Username(username)
				.orElseThrow(() -> new EntityNotFoundException("Cart was not found for this username=" + username));
	}

	@Transactional
	public Basket putDeviceInBasket(Device device, String username) {
		Basket basket = basketRepository.findByCustomer_Username(username).orElseGet(() -> createBasketForCustomerUsername(username));
		basket.addDevice(deviceService.findDevice(device));
		return basketRepository.save(basket);
	}

	public Basket putDeviceInBasket(Integer deviceId, String username) {
		Basket basket = basketRepository.findByCustomer_Username(username).orElseGet(() -> createBasketForCustomerUsername(username));
		basket.addDevice(deviceService.getDevice(deviceId));
		return basketRepository.save(basket);
	}

//	@Transactional
//	public Basket removeDeviceFromBasket(Device device, String username) {
//		device = deviceService.findDevice(device);
//		Basket basket = findByUsername(username);
//		if (basket.getDevices().contains(device)) {
//			basket.removeDevice(device);
//			return basketRepository.save(basket);
//		}
//		else
//			throw new EntityNotFoundException("Cart doesn't contain device: " + device);
//	}

	@Transactional
//	@PreAuthorize("#principal.username == username")
	public Basket clearBasket(String username){
		Basket basket = findByUsername(username);
		basket.getDevices().clear();
		return basketRepository.save(basket);
	}
	
	
//	public boolean existDeviceInBasket(Integer deviceId, String username) {
//		return findByUsername(username).getDevices().contains(deviceService.getDevice(deviceId));
//	}
//
//	public Optional<Device> getDeviceIfPresentInBasket(Integer deviceId, String username) {
//		Device deviceFromDb = deviceService.getDevice(deviceId);
//		if (Objects.isNull(deviceFromDb))
//			return Optional.empty();
//		Basket basket = findByUsername(username);
//		if (basket.getDevices().contains(deviceFromDb))
//			return Optional.of(deviceFromDb);
//		return Optional.empty();
//	}
//
//	@Transactional
//	public Basket removeDeviceFromBasket(Integer deviceId, String username) {
//		Basket basket = findByUsername(username);
//		Device device = deviceService.getDeviceOrThrow(deviceId);
//		if (basket.getDevices().contains(device)) {
//			basket.removeDevice(device);
//			return basketRepository.save(basket);
//		}
//		else
//			throw new EntityNotFoundException("Cart doesn't contain device: " + deviceId);
//	}
}
