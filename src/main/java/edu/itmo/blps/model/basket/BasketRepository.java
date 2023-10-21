package edu.itmo.blps.model.basket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BasketRepository extends JpaRepository<Basket, Integer> {
	Optional<Basket> findByCustomer_Id(Integer id);
	Optional<Basket> findByCustomer_Username(String username);
}
