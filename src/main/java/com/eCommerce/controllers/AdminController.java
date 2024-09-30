package com.eCommerce.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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

import com.eCommerce.model.Category;
import com.eCommerce.model.Product;
import com.eCommerce.service.CategoryService;
import com.eCommerce.service.ProductService;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.server.PathParam;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	CategoryService categoryService;
	
	@Autowired
	private ProductService productService;

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
	public String saveCategory(@ModelAttribute Category category,@RequestParam("file") MultipartFile file ,HttpSession session) throws IOException {

		String imageName = file!=null ? file.getOriginalFilename() : "default.jpg";
		category.setImageName(imageName);
		
		Boolean existsCategory = categoryService.existsCategory(category.getName());
		
		if (existsCategory) {
			session.setAttribute("errMsg", "The category already exists");
		} else {
			Category saveCategory = categoryService.saveCategory(category);

			if (ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errMsg", "Internal server error");
			}else {
				
				File saveFile = new ClassPathResource("static/Images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"category_img"+File.separator+file.getOriginalFilename());
				
				
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				session.setAttribute("succMsg", "Category saved successfully");
			}
		}

		return "redirect:/admin/category";

	}
	

	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id,HttpSession session) {

		boolean deleteCategoryById = categoryService.deleteCategoryById(id);
		if(deleteCategoryById) {
			session.setAttribute("succMsg", "Category deleted successfully");
		}else {
			session.setAttribute("errMsg", "Oops!! an error occured");
		}
		return "redirect:/admin/category";

	}
	
	@GetMapping("/editCategory/{id}")
	public String editCategory(@PathVariable int id,Model model) {

		model.addAttribute("category", categoryService.getCategoryById(id));
		
		return "admin/edit_category";

	}
	
	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,HttpSession session) throws IOException {

		
		Category existingCategory = categoryService.getCategoryById(category.getId());
		String imageName = file.isEmpty()?existingCategory.getImageName():file.getOriginalFilename();
		
		if(!ObjectUtils.isEmpty(existingCategory)) {
			
			existingCategory.setName(category.getName());
			existingCategory.setIsActive(category.getIsActive());
			existingCategory.setImageName(imageName);
		}
		
		Category updatedCategory = categoryService.saveCategory(existingCategory);
		
		if(!ObjectUtils.isEmpty(updatedCategory)) {
			
			if(!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/Images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"category_img"+File.separator+file.getOriginalFilename());
				
				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			session.setAttribute("succMsg", "category updated successfully");
		}else {
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
	public String saveProduct(@ModelAttribute Product product,@RequestParam("file") MultipartFile image,HttpSession session) throws IOException {

		String imageName = image!=null ? image.getOriginalFilename() : "default.jpg";
		product.setImage(imageName);
		
		Boolean existsProduct = productService.existsProduct(product.getTitle());
		
		if (existsProduct) {
			session.setAttribute("errMsg", "The product already exists");
		} else {
			Product saveProduct = productService.saveProduct(product);

			if (ObjectUtils.isEmpty(saveProduct)) {
				session.setAttribute("errMsg", "Internal server error");
			}else {
				
				File saveFile = new ClassPathResource("static/Images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"Products"+File.separator+image.getOriginalFilename());
				
				
				Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				session.setAttribute("succMsg", "product saved successfully");
			}
		}
		
		return "redirect:/admin/products";

	}
	
	

	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id,HttpSession session) {

		boolean deleteProductyById = productService.deleteProductById(id);
		if(deleteProductyById) {
			session.setAttribute("succMsg", "product deleted successfully");
		}else {
			session.setAttribute("errMsg", "Oops!! an error occured");
		}
		return "redirect:/admin/products";

	}
	
	@GetMapping("/editProduct/{id}")
	public String editProduct(@PathVariable int id,Model model) {

		model.addAttribute("product", productService.getProductById(id));
		
		return "admin/edit-product";

	}
	
	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile file,HttpSession session) throws IOException {

		
		Product existingProduct = productService.getProductById(product.getId());
		String imageName = file.isEmpty()?existingProduct.getImage():file.getOriginalFilename();
		
		if(!ObjectUtils.isEmpty(existingProduct)) {
			
			existingProduct.setTitle(product.getTitle());
			existingProduct.setDescription(product.getDescription());
			existingProduct.setCategory(product.getCategory());
			existingProduct.setPrice(product.getPrice());
			existingProduct.setStock(product.getStock());
			
			existingProduct.setImage(imageName);
		}
		
		Product updatedProduct = productService.saveProduct(existingProduct);
		
		if(!ObjectUtils.isEmpty(updatedProduct)) {
			
			if(!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/Images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"Products"+File.separator+file.getOriginalFilename());
				
				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			session.setAttribute("succMsg", "Product updated successfully");
		}else {
			session.setAttribute("errMsg", "Product update failed");
		}
		return "redirect:/admin/products";

	}

}
