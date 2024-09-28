package com.eCommerce.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String index() {
		
		return "index";
		
	}
	
	@GetMapping("/login")
	public String login() {
		
		return "login";
		
	}
	
	@GetMapping("/register")
	public String register() {
		
		return "register";
		
	}
	
	@GetMapping("/products")
	public String product() {
		
		return "product";
		
	}
	
	@GetMapping("/view-product")
	public String productDetails() {
		
		return "view-product";
		
	}
}
