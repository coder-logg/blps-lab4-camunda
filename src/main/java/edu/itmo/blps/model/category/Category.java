package edu.itmo.blps.model.category;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name = "category")
public class Category implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected int id;

	@NotNull
	@NotEmpty
	private String name;

	private String description;

	public Category(int id) {
		this.id = id;
	}
}

