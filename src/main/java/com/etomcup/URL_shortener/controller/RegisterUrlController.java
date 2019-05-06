package com.etomcup.URL_shortener.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import com.etomcup.URL_shortener.dao.AccountRepository;
import com.etomcup.URL_shortener.dao.UrlRegisterRepository;
import com.etomcup.URL_shortener.exception.ConflictException;
import com.etomcup.URL_shortener.exception.InvalidUrlException;
import com.etomcup.URL_shortener.model.Account;
import com.etomcup.URL_shortener.model.UrlRegister;
import com.etomcup.URL_shortener.util.BasicAuthorizationHelper;
import com.etomcup.URL_shortener.util.IDConverter;
import com.etomcup.URL_shortener.util.LongUrl;

@Controller
public class RegisterUrlController {

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	UrlRegisterRepository urlRegisterRepository;

	@PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> registerUrl(@Valid @RequestBody LongUrl longUrl,
			@Valid @RequestHeader(value = "Authorization", defaultValue = "NOT SET") String authorizationHeader,
			HttpServletResponse response, HttpServletRequest request) {
		Map<String, String> returnJson = new HashMap<String, String>();

		BasicAuthorizationHelper basicAuthorizationHelper = new BasicAuthorizationHelper(authorizationHeader);

		basicAuthorizationHelper.checkIfBasicAuthorizationIsUsed();

		String[] credentials = basicAuthorizationHelper.getUsernameAndPassword();
		String usernameFromHeader = credentials[0];
		String passwordFromHeader = credentials[1];

		basicAuthorizationHelper.checkIfValidUsernameAndPassword(usernameFromHeader, passwordFromHeader,
				accountRepository);

		UrlValidator urlValidator = new UrlValidator();
		if (!urlValidator.isValid(longUrl.getUrl())) {
			throw new InvalidUrlException();
		}

		Account account = new Account();
		account.setAccountId(usernameFromHeader);
		account.setPassword(passwordFromHeader);

		// if given url is already registered by this account
		if (urlRegisterRepository.findByAccountIdAndLongUrl(account.getAccountId(), longUrl.getUrl()) != null) {
			throw new ConflictException();
		}

		UrlRegister urlRegister = new UrlRegister();
		urlRegister.setLongUrl(longUrl.getUrl());
		urlRegister.setNumberOfVisits(0);
		urlRegister.setAccountId(account.getAccountId());
		urlRegister.setRedirectType(longUrl.getRedirectType());
		urlRegisterRepository.save(urlRegister);

		String currentUrl = request.getRequestURL().toString();
		String baseUrl = currentUrl.substring(0, currentUrl.length() - 8);

		String newEndpoint = IDConverter.INSTANCE.createUniqueID(urlRegister.getId());

		returnJson.put("shortUrl", baseUrl + newEndpoint);
		response.setStatus(HttpServletResponse.SC_CREATED);

		return returnJson;

	}

}
