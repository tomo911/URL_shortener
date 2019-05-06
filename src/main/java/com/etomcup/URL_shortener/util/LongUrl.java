package com.etomcup.URL_shortener.util;

import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Range;

public class LongUrl {
	@NotEmpty(message = "url needs to be set.")
	String url;

	@Range(min = 301, max = 302)
	int redirectType = 302;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getRedirectType() {
		return redirectType;
	}

	public void setRedirectType(int redirectType) {
		this.redirectType = redirectType;
	}

}
