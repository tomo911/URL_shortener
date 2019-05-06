package com.etomcup.URL_shortener.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.etomcup.URL_shortener.model.UrlRegister;

public interface UrlRegisterRepository extends CrudRepository<UrlRegister, String> {
	public UrlRegister findByAccountIdAndLongUrl(String accountId, String longUrl);

	public UrlRegister findById(long id);

	public UrlRegister findByIdAndAccountId(long id, String accountId);

	public List<UrlRegister> findByAccountId(String accountId);

}
