package com.eCommerce.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.eCommerce.model.Category;
import com.eCommerce.model.Product;
import com.eCommerce.model.User;
import com.eCommerce.service.CategoryService;
import com.eCommerce.service.ProductService;
import com.eCommerce.service.UserService;

import jakarta.servlet.http.HttpSession;



@Controller
public class HomeController {
	
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
	public String index(Model model) {
		model.addAttribute("categories", categoryService.findAllActiveCategories());
		//model.addAttribute("products", productService.findAllActiveProducts(category));
		return "index";
		
	}
	
	@GetMapping("/signin")
	public String login() {
		
		return "login";
		
	}
	
	@GetMapping("/register")
	public String register() {
		
		return "register";
		
	}
	
	@GetMapping("/products")
	public String product(Model model,@RequestParam(value = "category",defaultValue = "") String category) {
		model.addAttribute("categories", categoryService.findAllActiveCategories());
		model.addAttribute("products", productService.findAllActiveProducts(category));
		model.addAttribute("paramValue", category);

		return "product";
		
	}
	
	@GetMapping("/view-product/{id}")
	public String productDetails(@PathVariable int id,Model model) {
		
		Product product =productService.getProductById(id);
		model.addAttribute("product", product);
		
		return "view-product";
		
	}
	

	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute User user, @RequestParam("file") MultipartFile file,HttpSession session) throws IOException {
		
		String profileImage = file.isEmpty()?"default.jpg":file.getOriginalFilename();
		
		
user.setProfile(profileImage);
		User saveUser = userService.saveUser(user);
		
		
		if(!ObjectUtils.isEmpty(saveUser)) {
			if (!file.isEmpty()) {
			    // Get the static/Images directory
			    File saveFile = new ClassPathResource("static/Images").getFile();
			    
			    // Define the path where the file will be saved (profile_img directory)
			    Path profileImgPath = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img");
			    
			    // Create the profile_img directory if it doesn't exist
			    if (!Files.exists(profileImgPath)) {
			        Files.createDirectories(profileImgPath);
			    }

			    // Now define the full path for the file to be saved
			    Path path = Paths.get(profileImgPath.toString() + File.separator + file.getOriginalFilename());
			    
			    System.out.println(path);
			    // Copy the uploaded file to the target location
			    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			    // Set a success message in the session
			    session.setAttribute("succMsg", "Registration successful");
			}
else {
				session.setAttribute("errorMsg", "Registration failed");
			}
		}
		return "redirect:/register";
	}
}
