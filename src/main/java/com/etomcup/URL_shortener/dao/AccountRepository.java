package com.etomcup.URL_shortener.dao;

import org.springframework.data.repository.CrudRepository;

import com.etomcup.URL_shortener.model.Account;

public interface AccountRepository extends CrudRepository<Account, String> {
	public Account findByAccountId(String accountId);

	public Account findByAccountIdAndPassword(String accountId, String password);
}
