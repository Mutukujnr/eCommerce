package com.eCommerce.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eCommerce.model.Category;
import com.eCommerce.model.User;
import com.eCommerce.service.CategoryService;
import com.eCommerce.service.ProductService;
import com.eCommerce.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private UserService userService;
	
	
	@ModelAttribute
	public void getUserDetails(Principal p,Model m) {
		if(p!=null) {
			String email = p.getName();
			User user = userService.findByEmail(email);
			
			m.addAttribute("user", user);
		}
		
		List<Category> categories = categoryService.findAllActiveCategories();
		m.addAttribute("categories", categories);
	}

	@GetMapping("/")
	public String home() {
		return "user/index";
	}
}
