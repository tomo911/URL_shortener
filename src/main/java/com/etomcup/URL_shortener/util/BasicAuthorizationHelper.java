package com.etomcup.URL_shortener.util;

import java.util.Base64;

import com.etomcup.URL_shortener.dao.AccountRepository;
import com.etomcup.URL_shortener.exception.NotUsingBasicAuthException;
import com.etomcup.URL_shortener.exception.UnauthorizedException;

public class BasicAuthorizationHelper {
	private String authorizationHeader;

	public BasicAuthorizationHelper(String authorizationHeader) {
		this.authorizationHeader = authorizationHeader;
	}

	public String getAuthorizationHeader() {
		return authorizationHeader;
	}

	public void setAuthorizationHeader(String authorizationHeader) {
		this.authorizationHeader = authorizationHeader;
	}

	public void checkIfBasicAuthorizationIsUsed() {

		if (!authorizationHeader.startsWith("Basic")) {
			throw new NotUsingBasicAuthException();
		}
	}

	public String[] getUsernameAndPassword() {
		String[] usernameAndPassword = new String[2];
		String credentials = authorizationHeader.substring(authorizationHeader.indexOf(" ") + 1);
		byte[] decodedBytes;
		try {
			decodedBytes = Base64.getDecoder().decode(credentials);
		}catch (Exception e) {
			throw new UnauthorizedException();
		}
		String decodedString = new String(decodedBytes);
		int separator = decodedString.lastIndexOf(":");
		if (separator == -1) {
			throw new UnauthorizedException();
		}
		usernameAndPassword[0] = decodedString.substring(0, separator);
		usernameAndPassword[1] = decodedString.substring(separator + 1);

		return usernameAndPassword;

	}

	public void checkIfValidUsernameAndPassword(String username, String password, AccountRepository accountRepository) {
		if (accountRepository.findByAccountIdAndPassword(username, password) == null) {
			throw new UnauthorizedException();
		}
	}

}
