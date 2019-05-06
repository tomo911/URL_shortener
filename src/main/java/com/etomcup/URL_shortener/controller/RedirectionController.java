package com.etomcup.URL_shortener.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import com.etomcup.URL_shortener.dao.AccountRepository;
import com.etomcup.URL_shortener.dao.UrlRegisterRepository;
import com.etomcup.URL_shortener.exception.PageNotFoundException;
import com.etomcup.URL_shortener.exception.UnauthorizedException;
import com.etomcup.URL_shortener.model.UrlRegister;
import com.etomcup.URL_shortener.util.BasicAuthorizationHelper;
import com.etomcup.URL_shortener.util.IDConverter;

@Controller
public class RedirectionController {

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	UrlRegisterRepository urlRegisterRepository;

	@RequestMapping(value = "/{shortenedUri}")
	public RedirectView redirect(@PathVariable String shortenedUri,
			@RequestHeader(value = "Authorization", defaultValue = "NOT SET") String authorizationHeader) {

		long urlId = IDConverter.INSTANCE.getDictionaryKeyFromUniqueID(shortenedUri);
		UrlRegister urlRegister = urlRegisterRepository.findById(urlId);

		// check if page exist
		if (urlRegister == null) {
			throw new PageNotFoundException();
		}
		BasicAuthorizationHelper basicAuthorizationHelper = new BasicAuthorizationHelper(authorizationHeader);
		basicAuthorizationHelper.checkIfBasicAuthorizationIsUsed();

		String[] credentials = basicAuthorizationHelper.getUsernameAndPassword();
		String usernameFromHeader = credentials[0];
		String passwordFromHeader = credentials[1];
		basicAuthorizationHelper.checkIfValidUsernameAndPassword(usernameFromHeader, passwordFromHeader,
				accountRepository);

		// check if user trying to make request registered this URI
		if (!urlRegister.getAccountId().equals((usernameFromHeader))) {
			throw new UnauthorizedException();
		}

		RedirectView rv = new RedirectView();
		rv.setUrl(urlRegister.getLongUrl());
		HttpStatus redirectType = (urlRegister.getRedirectType() == 301) ? HttpStatus.MOVED_PERMANENTLY
				: HttpStatus.MOVED_TEMPORARILY;
		rv.setStatusCode(redirectType);
		urlRegister.setNumberOfVisits(urlRegister.getNumberOfVisits() + 1);
		urlRegisterRepository.save(urlRegister);

		return rv;

	}

}
