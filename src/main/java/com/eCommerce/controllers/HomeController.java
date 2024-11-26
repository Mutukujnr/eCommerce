package com.eCommerce.controllers;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import com.eCommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.eCommerce.utils.CommonUtils;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;



@Controller
public class HomeController {
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private CartService cartService;
	
	//@Autowired
	//private CommonUtils commonUtils;
	
	@Autowired
	PasswordEncoder encoder;
	
	@ModelAttribute
	public void getUserDetails(Principal p,Model m) {
		if(p!=null) {
			String email = p.getName();
			User user = userService.findByEmail(email);
			Integer cartCount = cartService.getCartCount(user.getId());

			m.addAttribute("user", user);
			m.addAttribute("cartCount", cartCount);

		}else {
			m.addAttribute("user", null);
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
	public String product(Model model,@RequestParam(value = "category",defaultValue = "") String category,@RequestParam(name = "pageNo", defaultValue = "0")  int pageNo,@RequestParam(name = "pageSize",defaultValue = "2")  int pageSize) {
		model.addAttribute("categories", categoryService.findAllActiveCategories());
		model.addAttribute("products", productService.findAllActiveProducts(category));
		model.addAttribute("paramValue", category);
		
		Page<Product> page = productService.findAllActiveProductsWithPagination(pageNo, pageSize, category);
		
		List<Product> products = page.getContent();
		model.addAttribute("products", products);
		
		model.addAttribute("productsSize", products.size());
		model.addAttribute("pageNo", page.getNumber());
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("totalElements", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("isFirst", page.isFirst());
		model.addAttribute("isLast", page.isLast());

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
		
		 File saveFile = new ClassPathResource("static/Images").getFile();
		    
		    // Define the path where the file will be saved (profile_img directory)
		    Path profileImgPath = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img");
		    
		    // Create the profile_img directory if it doesn't exist
		    if (!Files.exists(profileImgPath)) {
		        Files.createDirectories(profileImgPath);
		    }

		
user.setProfile(profileImage);
		User saveUser = userService.saveUser(user);
		
		
		if(!ObjectUtils.isEmpty(saveUser)) {
			if (!file.isEmpty()) {
			    // Get the static/Images directory
			   
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
	
	
	//forgot password
	

	@GetMapping("/forgot-password")
	public String forgotPasswordPage() {
		
		return "forgot-password";
		
	}
	
	
	@GetMapping("/search")
	public String search(@RequestParam String search,Model m) {
		
		List<Product> list = productService.search(search);
		m.addAttribute("products", list);
		return "product";
	}
	
	/*
	 * @PostMapping("/forgot-password") public String forgotPassword(@RequestParam
	 * String email,HttpSession session,HttpServletRequest request) throws
	 * UnsupportedEncodingException, MessagingException {
	 * 
	 * User byEmail = userService.findByEmail(email);
	 * 
	 * if(ObjectUtils.isEmpty(byEmail)) { session.setAttribute("errorMsg",
	 * "invalid email"); }else {
	 * 
	 * String resetToken = UUID.randomUUID().toString();
	 * userService.updateUserResetToken(email, resetToken);
	 * 
	 * String siteUrl =
	 * commonUtils.generateURL(request)+"/reset-password?token="+resetToken;
	 * 
	 * Boolean sendEmail = commonUtils.sendEmail(siteUrl,email);
	 * 
	 * if(sendEmail) { session.setAttribute("succMsg",
	 * "Password reset link has been send to your email"); }else {
	 * session.setAttribute("errorMsg", "Error happened"); } }
	 * 
	 * return "redirect:/forgot-password";
	 * 
	 * }
	 */
	
	/*
	 * @GetMapping("/reset-password") public String resetPasswordPage(@RequestParam
	 * String token,HttpSession session,Model model) {
	 * 
	 * User user = userService.findUserByResetToken(token);
	 * 
	 * if(ObjectUtils.isEmpty(user)) { model.addAttribute("errorMsg",
	 * "Invalid Token");
	 * 
	 * return "error"; }else {
	 * 
	 * model.addAttribute("token", token);
	 * 
	 * } return "reset-password";
	 * 
	 * }
	 */
	
	/*@PostMapping("/reset-password")
	public String PasswordPage(@RequestParam String token,@RequestParam String password,@RequestParam String cpassword,HttpSession session,Model model) {
		
		User user = userService.findUserByResetToken(token);
		
		System.out.println(token);
		
		
		if(ObjectUtils.isEmpty(user)) {
			model.addAttribute("errorMsg", "Invalid Token");
			
			return "error";
		}else {
			
		
				user.setPassword(encoder.encode(password));
				user.setResetToken(null);
				
				userService.updateUser(user);
				
				session.setAttribute("succMsg", "You have successfully reset your password");
		
			
			return "redirect:/reset-password";
		}
		*/
		
		/*
		 * spring.mail.host=smtp.gmail.com spring.mail.port=587
		 * spring.mail.username=mutukujuniour@gmail.com
		 * spring.mail.password=lyoycpioliedmvwj
		 * spring.mail.properties.mail.smtp.auth=true
		 * spring.mail.properties.mail.smtp.starttls.enable=true
		 * spring.mail.test-connection=true
		 */
	
}
