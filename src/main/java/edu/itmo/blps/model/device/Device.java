package edu.itmo.blps.model.device;

import edu.itmo.blps.model.category.Category;
import edu.itmo.blps.model.company.Company;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "device")
@Builder
public class Device {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	@Column(nullable = false, name = "name")
	private String name;

	private String description;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "company_id")
	private Company company;

	@Column(nullable = false, name="price")
	private Integer price;

	public Device(Integer id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Device device = (Device) o;
		return Objects.equals(id, device.id)
				&& Objects.equals(category, device.category)
				&& Objects.equals(name, device.name)
				&& Objects.equals(company, device.company);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, company);
	}
}
