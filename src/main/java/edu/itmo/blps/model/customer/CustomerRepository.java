package edu.itmo.blps.model.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Integer> {

    boolean existsByUsername(String username);
    Optional<Customer> findByUsername(String username);

    Optional<Customer> findByUsernameAndPassword(String username, String password);
}
