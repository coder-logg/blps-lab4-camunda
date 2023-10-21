package edu.itmo.blps.model.basket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.itmo.blps.model.customer.Customer;
import edu.itmo.blps.model.device.Device;
import lombok.Data;

import javax.persistence.*;
import java.util.*;

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

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "devices_in_basket",
			joinColumns = @JoinColumn(name = "basket_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "device_id", referencedColumnName = "id"))
	private List<Device> devices = new ArrayList<>();

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
		devices.add(device);
//		devices.computeIfPresent(device.getId(), (k, v) -> v = v + 1);
//		devices.putIfAbsent(device.getId(), 1);
	}

	public void removeDevice(Device device) {
		devices.remove(device);
//		devices.computeIfPresent(device.getId(), (k, v) -> v > 1 ? v = v - 1 : devices.remove(k));
	}

	public ArrayList<Device> getDevices() {
		return new ArrayList<>(devices);
	}
}
