package com.eCommerce.service;

import java.util.List;

import com.eCommerce.model.Product;

public interface ProductService {
public Product saveProduct(Product Product);
	
	public Boolean existsProduct(String title);
	
	public boolean deleteProductById(int id);
	
	public Product getProductById(int id);
	
	List<Product> findAllProducts();
}
