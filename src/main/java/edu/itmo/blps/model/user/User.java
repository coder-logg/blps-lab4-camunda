package edu.itmo.blps.model.user;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import edu.itmo.blps.model.company.Company;
import edu.itmo.blps.model.customer.Customer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(of = {"id", "username", "email"})
@NoArgsConstructor
@Table(name = "_user")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		property = "role",
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		defaultImpl = User.class)
@JsonSubTypes({@Type(value = Company.class, name = "company"),
		@Type(value = Customer.class, name = "customer")})
public class User implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Integer id;

	@Column(unique = true)
	protected String username;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected String password;

	@Email
//	@NotNull
//	@NotEmpty
	@Column(unique = true)
	@JsonProperty(required = true)
	private String email;

	@JsonIgnore
	@Transient
	protected final Set<GrantedAuthority> authorities = new HashSet<>();

	public void addRole(GrantedAuthority authority) {
		authorities.add(authority);
	}

	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public User(Integer id) {
		this.id = id;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isEnabled() {
		return true;
	}
}
