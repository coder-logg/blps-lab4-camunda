package edu.itmo.blps.model.basket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.itmo.blps.model.company.Company;
import edu.itmo.blps.model.customer.Customer;
import edu.itmo.blps.model.device.Device;
import edu.itmo.blps.service.DeviceService;
import lombok.Data;
import org.aspectj.lang.annotation.After;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;

import javax.persistence.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hibernate.annotations.CascadeType;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@Entity
@Table(name = "basket")
public class Basket {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	@JsonIgnore
	private Customer customer;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "devices_in_basket", joinColumns = @JoinColumn(name = "basket_id"))
	@Column(name = "amount")
	@MapKeyColumn(name = "device_id")
	@Cascade(value = {CascadeType.ALL})
	private Map<Integer, Integer> devices = new HashMap<>();

	@Transient
	private int totalPrice;

	@JsonProperty
	public Basket setCustomerId(Integer id) {
		customer = new Customer(id);
		return this;
	}

	@JsonProperty
	public Integer getCustomerId() {
		if (customer != null)
			return customer.getId();
		return null;
	}

	public void addDevice(Device device) {
		addFewDevices(device, 1);
	}

	public Basket addFewDevices(Device device, Integer amount) {
		devices.computeIfPresent(device.getId(), (k, v) -> v = v + amount);
		devices.putIfAbsent(device.getId(), amount);
		return this;
	}

	public <T> Basket addFewDevices(T a, Integer amount, Function<T, Device> deviceFunction) {
		return addFewDevices(deviceFunction.apply(a), amount);
	}

	public int computeTotalPrice(@Autowired DeviceService deviceService){
		return devices.keySet().stream().mapToInt(a -> devices.get(a) * deviceService.getDevice(a).getPrice()).sum();
	}

	public Basket updateTotalPrice(@Autowired DeviceService deviceService){
		totalPrice = computeTotalPrice(deviceService);
		return this;
	}

	public void removeDevice(Device device) {
		devices.computeIfPresent(device.getId(), (k, v) -> v > 1 ? v = v - 1 : devices.remove(k));
	}

	public static Set<Integer> getCompanies(Map<Device, Integer> devicesAmounts){
		return devicesAmounts.keySet().stream().map(Device::getCompany).map(Company::getId).collect(Collectors.toSet());
	}

	public Map<Integer, Integer> getDevices() {
		return new HashMap<>(devices);
	}

	public List<Integer> getDevicesIds() {
		return new ArrayList<>(devices.keySet());
	}

	public void clear(){
		devices.clear();
	}
}
