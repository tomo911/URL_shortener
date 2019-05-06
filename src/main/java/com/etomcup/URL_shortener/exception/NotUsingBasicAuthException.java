package com.etomcup.URL_shortener.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Basic Authorization is not used")
public class NotUsingBasicAuthException extends RuntimeException {

}
