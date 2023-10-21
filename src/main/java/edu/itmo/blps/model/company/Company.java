package edu.itmo.blps.model.company;

import edu.itmo.blps.model.user.Role;
import edu.itmo.blps.model.user.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true, of = "name")
@Entity
@NoArgsConstructor
@Table(name = "company")
@SuperBuilder(toBuilder = true)
public class Company extends User {
	private String name;
	private String description;

	{
		addRole(Role.COMPANY);
	}

	public Company(Integer id) {
		super(id);
	}

	public Company(String name, String password) {
		super(name, password);
	}

	public Company(String username, String password, String name) {
		super(username, password);
		this.name = name;
	}

	public Company(String username, String password, String name, String description) {
		super(username, password);
		this.name = name;
		this.description = description;
	}

	@Override
	public String toString() {
		return "Company{" +
				"id=" + id +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", authorities=" + authorities +
				'}';
	}
}
