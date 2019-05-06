package com.etomcup.URL_shortener.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.etomcup.URL_shortener.dao.AccountRepository;
import com.etomcup.URL_shortener.dao.UrlRegisterRepository;
import com.etomcup.URL_shortener.model.Account;
import net.bytebuddy.utility.RandomString;

@Controller
public class AccountController {

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	UrlRegisterRepository urlRegisterRepository;

	@PostMapping(value = "/account", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> makeAccount(@Valid @RequestBody Account account,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (accountRepository.findByAccountId(account.getAccountId()) == null) {
			String password = RandomString.make(8);
			account.setPassword(password);
			accountRepository.save(account);

			map.put("success", true);
			map.put("description", "Your account is opened");
			map.put("password", password);
			response.setStatus(HttpServletResponse.SC_CREATED);

		} else {

			map.put("success", false);
			map.put("description", "Account with same Id already exists");
			response.setStatus(HttpServletResponse.SC_CONFLICT);

		}
		return map;

	}

}
