package com.eCommerce.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.eCommerce.dto.CategoryDTO;
import com.eCommerce.dto.ProductDTO;
import com.eCommerce.model.Category;
import com.eCommerce.model.Product;
import com.eCommerce.model.ProductOrder;
import com.eCommerce.model.User;
import com.eCommerce.repository.CategoryRepository;
import com.eCommerce.service.CartService;
import com.eCommerce.service.CategoryService;
import com.eCommerce.service.OrderService;
import com.eCommerce.service.ProductService;
import com.eCommerce.service.UserService;
import com.eCommerce.utils.OrderStatus;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private CartService cartService;

	@Autowired
	OrderService orderService;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	CategoryRepository repo;
	/*
	 * @Autowired private CommonUtils commonUtils;
	 */

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			User user = userService.findByEmail(email);

			m.addAttribute("user", user);

			Integer cartCount = cartService.getCartCount(user.getId());

			m.addAttribute("cartCount", cartCount);
		}

		List<Category> categories = categoryService.findAllActiveCategories();
		m.addAttribute("categories", categories);
	}

	@GetMapping("/")
	public String index(Model m,@RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
			@RequestParam(name = "pageSize", defaultValue = "3") int pageSize) {
		
		m.addAttribute("customers", userService.getUserCount());
		m.addAttribute("cat", categoryService.countActiveCategories());
		//m.addAttribute("orders", orderService.getAllOrders());

		
		Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);
		m.addAttribute("orders", page);
		m.addAttribute("search", false);

		// model.addAttribute("productsSize", categories.size());
		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		return "admin/index";

	}

	@GetMapping("/add-product")
	public String addProduct(Model model, @ModelAttribute ProductDTO productDTO, BindingResult bindingResult) {

		model.addAttribute("categories", categoryService.findAllCategories());
		return "admin/add-product";

	}

	@GetMapping("/category")
	public String addCategory(Model model, @ModelAttribute CategoryDTO categoryDTO,
			@RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
			@RequestParam(name = "pageSize", defaultValue = "4") int pageSize) {

		Page<Category> page = categoryService.getAllCategoriesPagination(pageNo, pageSize);

		// model.addAttribute("categories", categoryService.findAllCategories());

		List<Category> categories = page.getContent();
		model.addAttribute("categories", categories);

		// model.addAttribute("productsSize", categories.size());
		model.addAttribute("pageNo", page.getNumber());
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("totalElements", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("isFirst", page.isFirst());
		model.addAttribute("isLast", page.isLast());

		return "admin/category";

	}

	@PostMapping("/addCategory")
	public String saveCategory(@ModelAttribute CategoryDTO categoryDTO, BindingResult bindingResult,
			HttpSession session) throws IOException {

		MultipartFile image = categoryDTO.getImageName();
		String storageFileName = image.getOriginalFilename();

		try {
			String uploadDir = "public/Images";
			String category = uploadDir + "/categories/";
			Path uploadPath = Paths.get(category);

			System.out.println(uploadPath);

			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			try (InputStream inputStream = image.getInputStream()) {
				Files.copy(inputStream, Paths.get(category + storageFileName), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}

		Boolean existsCategory = categoryService.existsCategory(categoryDTO.getName());

		if (existsCategory) {
			session.setAttribute("errMsg", "The category already exists");
		} else {
			/*
			 * Category category = new Category(); category.setName(categoryDTO.getName());
			 * category.setIsActive(categoryDTO.getIsActive());
			 * category.setImageName(storageFileName); Category saveCategory =
			 * repo.save(category);
			 */

			Category saveCategory = categoryService.saveCategory(categoryDTO);

			if (!ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("succMsg", "Category saved successfully");
			} else {
				session.setAttribute("errMsg", "Internal server error");
			}
		}

		return "redirect:/admin/category";

	}

	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id, HttpSession session) {

		boolean deleteCategoryById = categoryService.deleteCategoryById(id);
		if (deleteCategoryById) {
			session.setAttribute("succMsg", "Category deleted successfully");
		} else {
			session.setAttribute("errMsg", "Oops!! an error occured");
		}
		return "redirect:/admin/category";

	}

	@GetMapping("/editCategory/{id}")
	public String editCategory(@PathVariable int id, Model model) {

		try {
			Category category = categoryService.getCategoryById(id);
			model.addAttribute("category", category);

			CategoryDTO categoryDTO = new CategoryDTO();
			categoryDTO.setName(category.getName());
			category.setIsActive(category.getIsActive());

			model.addAttribute("categoryDTO", categoryDTO);

		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "admin/category";
		}

		return "admin/edit_category";

	}

	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute CategoryDTO categoryDTO, @RequestParam Integer id,Model model,
			HttpSession session) throws IOException {

		
		try {
			Category category = categoryService.getCategoryById(id);
			System.out.println(id);
			System.out.println(category);
			//model.addAttribute("category", category);

			if(!categoryDTO.getImageName().isEmpty()) {
				String upload = "public/Images";
				String uploadDir = upload + "/categories/";
				Path oldImage = Paths.get(uploadDir + category.getImageName());
				
				try {
					Files.delete(oldImage);
				}catch(Exception e) {
					e.printStackTrace();
				}
				
				MultipartFile image = categoryDTO.getImageName();
				String storageFileName = image.getOriginalFilename();
				
				try (InputStream inputStream = image.getInputStream()) {
					Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
				}
				
				category.setImageName(storageFileName);
			} 
			category.setName(categoryDTO.getName());
			category.setIsActive(categoryDTO.getIsActive());
			
			categoryService.saveCategory(category);
			session.setAttribute("succMsg", "category updated successfully");

		} catch (Exception e) {
			System.out.println(e.getMessage());
			session.setAttribute("errMsg", "category update failed");
			return "redirect:/admin/category";
		}
		
		
		
		
		//Category existingCategory = categoryService.getCategoryById(category.getId());
		/*
		 * String imageName = file.isEmpty() ? existingCategory.getImageName() :
		 * file.getOriginalFilename();
		 * 
		 * if (!ObjectUtils.isEmpty(existingCategory)) {
		 * 
		 * existingCategory.setName(category.getName());
		 * existingCategory.setIsActive(category.getIsActive());
		 * existingCategory.setImageName(imageName); }
		 * 
		 * Category updatedCategory = categoryService.saveCategory(existingCategory);
		 * 
		 * if (!ObjectUtils.isEmpty(updatedCategory)) {
		 * 
		 * if (!file.isEmpty()) { File saveFile = new
		 * ClassPathResource("static/Images").getFile(); Path path =
		 * Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" +
		 * File.separator + file.getOriginalFilename());
		 * 
		 * System.out.println(path); Files.copy(file.getInputStream(), path,
		 * StandardCopyOption.REPLACE_EXISTING); }
		 */
			
		return "redirect:/admin/category";

	}

	@GetMapping("/products")
	public String loadProducts(@ModelAttribute ProductDTO productDTO, BindingResult bindingResult, Model model,
			@RequestParam(defaultValue = "") String search,
			@RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
			@RequestParam(name = "pageSize", defaultValue = "4") int pageSize) {

		/*
		 * ProductDTO productDTO = new ProductDTO(); model.addAttribute("productDTO",
		 * productDTO);
		 */
		Page<Product> page = null;

		// Page<Product> productPage = productService.findPage(pageNo, pageSize,
		// sortBy);

		if (search != null && search.length() > 0) {
			page = productService.searchProductPagination(pageNo, pageSize, search);
			model.addAttribute("products", page);

		} else {
			page = productService.findAllProductsPagination(pageNo, pageSize);

		}

		model.addAttribute("products", page.getContent());

		// model.addAttribute("productsSize", categories.size());
		model.addAttribute("pageNo", page.getNumber());
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("totalElements", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("isFirst", page.isFirst());
		model.addAttribute("isLast", page.isLast());

		return "admin/products";

	}

	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute ProductDTO productDTO, BindingResult bindingResult, HttpSession session)
			throws IOException {

		MultipartFile image = productDTO.getImage();
		String storageFileName = image.getOriginalFilename();

		try {
			String uploadDir = "public/Images";
			String products = uploadDir + "/products/";
			Path uploadPath = Paths.get(products);

			System.out.println(uploadPath);

			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			try (InputStream inputStream = image.getInputStream()) {
				Files.copy(inputStream, Paths.get(products + storageFileName), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}

		Product saveProduct = productService.saveProduct(productDTO);

		if (!ObjectUtils.isEmpty(saveProduct)) {

			session.setAttribute("succMsg", "Product saved successfully");
		} else {

			session.setAttribute("errorMsg", " failed");

		}

		return "redirect:/admin/products";

	}

	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {

		boolean deleteProductyById = productService.deleteProductById(id);
		if (deleteProductyById) {
			session.setAttribute("succMsg", "product deleted successfully");
		} else {
			session.setAttribute("errMsg", "Oops!! an error occured");
		}
		return "redirect:/admin/products";

	}

	@GetMapping("/editProduct/{id}")
	public String editProduct(@PathVariable int id, Model model) {

		model.addAttribute("product", productService.getProductById(id));

		return "admin/edit-product";

	}

	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile file,
			HttpSession session) {

		if (product.getDiscount() < 0 || product.getDiscount() > 100) {
			session.setAttribute("errMsg", "Invalid Discount amount");
		} else {
			Product updateProduct = productService.updateProduct(product, file);

			if (ObjectUtils.isEmpty(updateProduct)) {
				session.setAttribute("errMsg", "Internal server error");
			} else {

				session.setAttribute("succMsg", "Product saved successfully");
			}
		}
		return "redirect:/admin/products";

	}

	@GetMapping("/users")
	public String getAllUsers(Model model) {

		model.addAttribute("users", userService.getUsers("ROLE_USER"));
		return "admin/users";

	}

	@GetMapping("/updateStatus")
	public String updateAccountStatus(@RequestParam Boolean status, @RequestParam Integer id, HttpSession session) {

		Boolean f = userService.updateStatus(status, id);

		if (f) {
			session.setAttribute("succMsg", "Account status updated successfully");
		} else {
			session.setAttribute("errorMsg", "Error in updating account status");
		}
		return "redirect:/admin/users";

	}

	@GetMapping("/orders")
	public String getOrders(Model m, @RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
			@RequestParam(name = "pageSize", defaultValue = "3") int pageSize

	) {

		/*
		 * List<ProductOrder> allOrders = orderService.getAllOrders();
		 * m.addAttribute("orders", allOrders); m.addAttribute("search", false);
		 */

		Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);
		m.addAttribute("orders", page);
		m.addAttribute("search", false);

		// model.addAttribute("productsSize", categories.size());
		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		return "/admin/orders";

	}

	@PostMapping("/update-order-status")
	public String updateStatus(@RequestParam Integer id, @RequestParam Integer status, HttpSession session) {

		OrderStatus[] orderStatus = OrderStatus.values();

		String orderSt = null;
		for (OrderStatus st : orderStatus) {
			if (st.getId().equals(status)) {

				orderSt = st.getName();
			}
		}

		ProductOrder updateStatus = orderService.updateStatus(id, orderSt);
		/*
		 * try { commonUtils.sendProductOrderMail(updateStatus,orderSt); } catch
		 * (Exception e) {
		 * 
		 * e.printStackTrace(); }
		 */

		if (!ObjectUtils.isEmpty(updateStatus)) {
			session.setAttribute("succMsg", "Order Status updated");
		} else {
			session.setAttribute("errMsg", "Failed to update order status");

		}
		return "redirect:/admin/orders";

	}

	@GetMapping("/search-order")
	public String search(@RequestParam String orderRef, Model m, HttpSession session) {

		ProductOrder orderById = orderService.getOrderById(orderRef.trim());

		if (!ObjectUtils.isEmpty(orderById)) {
			m.addAttribute("orderDtls", orderById);

		} else {
			m.addAttribute("orderDtls", null);
			session.setAttribute("errMsg", "Incorrect Order Id");

		}

		m.addAttribute("search", true);
		return "/admin/orders";
	}

	@GetMapping("/add-admin")
	public String getAddAdminPage() {

		return "admin/add-admin";

	}

	@PostMapping("/save-admin")
	public String saveAdmin(@ModelAttribute User user, @RequestParam("file") MultipartFile file, HttpSession session)
			throws IOException {

		String profileImage = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
		user.setProfile(profileImage);
		User saveAdmin = userService.saveAdmin(user);

		if (!ObjectUtils.isEmpty(saveAdmin)) {

			if (!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/Images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile" + File.separator);

				System.out.println(path);

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			}
			session.setAttribute("succMsg", "Registration successful");
		} else {

			session.setAttribute("errorMsg", "Registration failed");

		}
		return "redirect:/admin/add-admin";
	}

	@GetMapping("/profile")
	public String showProfile() {

		return "/admin/profile";

	}

	@PostMapping("/update-profile")
	public String updateProfile(@ModelAttribute User user, MultipartFile image, HttpSession session)
			throws IOException {

		User updateUserProfile = userService.updateUserProfile(user, image);

		if (!ObjectUtils.isEmpty(updateUserProfile)) {
			session.setAttribute("succMsg", "Your profile has been updated");
		} else {
			session.setAttribute("errMsg", "Failed to update");
		}
		return "redirect:/user/profile";

	}

	/*
	 * @PostMapping("/change-password") public String changePassword(@RequestParam
	 * String currentPassword, @RequestParam String newPassword,@RequestParam String
	 * confirmPassword,Principal p,HttpSession session) {
	 * 
	 * User user = getUser(p);
	 * 
	 * 
	 * 
	 * boolean matches = encoder.matches(currentPassword, user.getPassword());
	 * 
	 * if(matches) { if(newPassword.equals(confirmPassword)) { String
	 * encodePassword= encoder.encode(newPassword);
	 * user.setPassword(encodePassword);
	 * 
	 * userService.updateUser(user);
	 * 
	 * session.setAttribute("succMsg",
	 * "Your password has been successfully updated");
	 * 
	 * }else { session.setAttribute("errMsg", " Password mismatch"); } }else {
	 * session.setAttribute("errMsg", "Incorrect Password"); } return
	 * "redirect:/user/profile";
	 * 
	 * }
	 */
}
