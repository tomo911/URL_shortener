package com.etomcup.URL_shortener.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

import com.etomcup.URL_shortener.dao.AccountRepository;
import com.etomcup.URL_shortener.dao.UrlRegisterRepository;
import com.etomcup.URL_shortener.exception.PageNotFoundException;
import com.etomcup.URL_shortener.exception.UnauthorizedException;
import com.etomcup.URL_shortener.model.UrlRegister;
import com.etomcup.URL_shortener.util.BasicAuthorizationHelper;

@Controller
public class StatisticController {

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	UrlRegisterRepository urlRegisterRepository;

	@GetMapping(value = "/statistic/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Integer> getStatistic(@PathVariable String accountId,
			@RequestHeader(value = "Authorization", defaultValue = "NOT SET") String authorizationHeader) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		if (accountRepository.findByAccountId(accountId) == null) {
			throw new PageNotFoundException();
		}

		BasicAuthorizationHelper basicAuthorizationHelper = new BasicAuthorizationHelper(authorizationHeader);

		basicAuthorizationHelper.checkIfBasicAuthorizationIsUsed();

		String[] credentials = basicAuthorizationHelper.getUsernameAndPassword();
		String usernameFromHeader = credentials[0];
		String passwordFromHeader = credentials[1];

		basicAuthorizationHelper.checkIfValidUsernameAndPassword(usernameFromHeader, passwordFromHeader,
				accountRepository);
		if (!accountId.equals(usernameFromHeader)) {
			throw new UnauthorizedException();
		}

		List<UrlRegister> urlRegisterList = urlRegisterRepository.findByAccountId(accountId);
		for (UrlRegister urlRegister : urlRegisterList) {
			map.put(urlRegister.getLongUrl(), urlRegister.getNumberOfVisits());
		}

		return map;
	}

}
