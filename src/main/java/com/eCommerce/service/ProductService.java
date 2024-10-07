package com.eCommerce.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.eCommerce.dto.ProductDTO;
import com.eCommerce.model.Category;
import com.eCommerce.model.Product;

public interface ProductService {
	 public Product saveProduct(Product Product); 
	
	public Boolean existsProduct(String title);
	
	public boolean deleteProductById(int id);
	
	public Product getProductById(int id);
	
	public Product updateProduct(Product product, MultipartFile image);
	
	List<Product> findAllProducts();
	
	public List<Product> findAllActiveProducts(String category);
}
