package edu.itmo.blps.model.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Integer> {
	List<Transaction> findByCustomer_id(Integer userId);
	List<Transaction> findByCompany_id(Integer userId);

}
