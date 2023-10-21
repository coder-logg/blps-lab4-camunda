package edu.itmo.blps.model.customer;

import edu.itmo.blps.model.user.User;
import edu.itmo.blps.model.user.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true, of = {})
@NoArgsConstructor
@Data
@SuperBuilder(toBuilder = true)
@Entity
public class Customer extends User {
	private String firstName;
	private String lastName;
	private String deliveryAddress;

	{
		addRole(Role.CUSTOMER);
	}

	public Customer(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public Customer(String username, String password, String firstName, String lastName) {
		super(username, password);
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public Customer(Integer id) {
		super(id);
	}

	@Override
	public String toString() {
		return "Customer{" +
				"id=" + id +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", deliveryAddress='" + deliveryAddress + '\'' +
				", authorities=" + authorities +
				'}';
	}
}
