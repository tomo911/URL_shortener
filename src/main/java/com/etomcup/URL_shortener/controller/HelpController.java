package com.etomcup.URL_shortener.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelpController {

	@GetMapping(value = "/help")
	public String help() {
		return "help";
	}

}
