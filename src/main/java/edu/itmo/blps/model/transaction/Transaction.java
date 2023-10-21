package edu.itmo.blps.model.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.itmo.blps.model.company.Company;
import edu.itmo.blps.model.device.Device;
import edu.itmo.blps.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction")
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
	@JoinTable(name = "devices_in_transaction",
			joinColumns = @JoinColumn(name = "transaction_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "device_id", referencedColumnName = "id"))
	private List<Device> devices = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "company_id")
	@JsonIgnore
	private Company company;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	@JsonIgnore
	private User customer;

	@Min(0)
	@Max(100)
	private Integer discount;

	@Basic
	@Column(name = "date_time")
	private LocalDateTime dateTime;

	@JsonProperty
	public Integer getSellerId() {
		return company.getId();
	}

	@JsonProperty
	public void setSellerId(Integer sellerId) {
		company = new Company(sellerId);
	}

	@JsonProperty
	public Integer getCustomerId() {
		return customer.getId();
	}

	@JsonProperty
	public void setCustomerId(Integer sellerId) {
		customer = new User(sellerId);
	}

	public void addDevice(Integer deviceId) {
//		if (devices.stream().map(Device::getId).anyMatch(x -> Objects.equals(x, deviceId)))
			devices.add(new Device(deviceId));
//		else
//			devices.put(deviceId, 1);
	}

	public Set<Integer> getDevicesIds(){
		return devices.stream().map(Device::getId).distinct().collect(Collectors.toSet());
	}
}
