package edu.itmo.blps.service;

import edu.itmo.blps.model.transaction.Transaction;

import java.util.List;

public interface TransactionService {
	Transaction addTransaction(Transaction transaction);

	List<Transaction> getTransactions(Integer userId);
//
//	void test();
}
