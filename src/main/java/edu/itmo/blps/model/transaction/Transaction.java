package edu.itmo.blps.model.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.itmo.blps.model.company.Company;
import edu.itmo.blps.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

	@ElementCollection
	@CollectionTable(name = "devices_in_transaction",
			joinColumns = @JoinColumn(name = "transaction_id"))
	@MapKeyColumn(name = "device_id")
	@Column(name = "amount")
	private Map<Integer, Integer> devices = new HashMap<>();

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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_time")
	private Date dateTime = new Date();

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
		if (devices.containsKey(deviceId))
			devices.compute(deviceId, (k, v) -> v += 1);
		else
			devices.put(deviceId, 1);
	}

	public Set<Integer> getDevicesIds(){
		return devices.keySet();
	}
}
