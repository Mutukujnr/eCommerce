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

import javax.swing.plaf.multi.MultiPanelUI;

import com.eCommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.eCommerce.dto.ProductDTO;
import com.eCommerce.model.Category;
import com.eCommerce.model.Product;
import com.eCommerce.model.ProductOrder;
import com.eCommerce.model.User;
import com.eCommerce.service.CategoryService;
import com.eCommerce.service.OrderService;
import com.eCommerce.service.ProductService;
import com.eCommerce.service.UserService;
import com.eCommerce.utils.CommonUtils;
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
	private CommonUtils commonUtils;
	
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
	public String index() {

		return "admin/index";

	}

	@GetMapping("/add-product")
	public String addProduct(Model model) {

		model.addAttribute("categories", categoryService.findAllCategories());
		return "admin/add-product";

	}

	@GetMapping("/category")
	public String addCategory(Model model) {

		model.addAttribute("categories", categoryService.findAllCategories());
		return "admin/category";

	}

	@PostMapping("/addCategory")
	public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
		category.setImageName(imageName);

		Boolean existsCategory = categoryService.existsCategory(category.getName());

		if (existsCategory) {
			session.setAttribute("errMsg", "The category already exists");
		} else {
			Category saveCategory = categoryService.saveCategory(category);

			if (ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errMsg", "Internal server error");
			} else {

				File saveFile = new ClassPathResource("static/Images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
						+ file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				session.setAttribute("succMsg", "Category saved successfully");
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

		model.addAttribute("category", categoryService.getCategoryById(id));

		return "admin/edit_category";

	}

	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		Category existingCategory = categoryService.getCategoryById(category.getId());
		String imageName = file.isEmpty() ? existingCategory.getImageName() : file.getOriginalFilename();

		if (!ObjectUtils.isEmpty(existingCategory)) {

			existingCategory.setName(category.getName());
			existingCategory.setIsActive(category.getIsActive());
			existingCategory.setImageName(imageName);
		}

		Category updatedCategory = categoryService.saveCategory(existingCategory);

		if (!ObjectUtils.isEmpty(updatedCategory)) {

			if (!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/Images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
						+ file.getOriginalFilename());

				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			session.setAttribute("succMsg", "category updated successfully");
		} else {
			session.setAttribute("errMsg", "category update failed");
		}
		return "redirect:/admin/category";

	}

	@GetMapping("/products")
	public String loadProducts(Model model) {

		model.addAttribute("products", productService.findAllProducts());
		return "admin/products";

	}

	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		// MultipartFile image = product.getImageFile();
		String storageFileName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();

		File saveFile = new ClassPathResource("static/Images").getFile();

		// Define the path where the file will be saved (profile_img directory)
		Path profileImgPath = Paths.get(saveFile.getAbsolutePath() + File.separator + "products_img");

		// Create the profile_img directory if it doesn't exist
		if (!Files.exists(profileImgPath)) {
			Files.createDirectories(profileImgPath);
		}

		product.setImage(storageFileName);
		product.setDiscount(0);
		product.setDiscountPrice(product.getPrice());

		Product saveProduct = productService.saveProduct(product);

		if (ObjectUtils.isEmpty(saveProduct)) {
			session.setAttribute("errMsg", "Internal server error");
		} else {

			// Now define the full path for the file to be saved
			Path path = Paths.get(profileImgPath.toString() + File.separator + file.getOriginalFilename());

			// Copy the uploaded file to the target location
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			session.setAttribute("succMsg", "Product saved successfully");
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
	public String getOrders(Model m) {

		List<ProductOrder> allOrders = orderService.getAllOrders();
		m.addAttribute("orders", allOrders);

		return "/admin/orders";

	}
	
	@PostMapping("/update-order-status")
    public String updateStatus(@RequestParam Integer id, @RequestParam Integer status,HttpSession session) {
    	
    	OrderStatus[] orderStatus = OrderStatus.values();
    	
    	String orderSt = null;
    	for(OrderStatus st: orderStatus) {
    		if(st.getId().equals(status)) {
    			
    			orderSt=st.getName();
    		}
    	}
    	
    	ProductOrder updateStatus = orderService.updateStatus(id, orderSt);
    	try {
			commonUtils.sendProductOrderMail(updateStatus,orderSt);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
    	
    	if(!ObjectUtils.isEmpty(updateStatus)) {
    		session.setAttribute("succMsg", "Order Status updated");
		}else { 
			session.setAttribute("errMsg", "Failed to update order status");
		
    	}
		return "redirect:/admin/orders";
    	
    }

	
	

}
