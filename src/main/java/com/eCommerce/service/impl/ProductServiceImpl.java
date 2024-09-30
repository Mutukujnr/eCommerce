package com.eCommerce.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.eCommerce.model.Category;
import com.eCommerce.model.Product;
import com.eCommerce.repository.ProductRepository;
import com.eCommerce.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService{

	@Autowired
	private ProductRepository productRepository;
	
	@Override
	public Product saveProduct(Product product) {
		
		return productRepository.save(product);
	}

	@Override
	public Boolean existsProduct(String title) {
	
		return productRepository.existsByTitle(title);
	}

	@Override
	public boolean deleteProductById(int id) {
Product product = productRepository.findById(id).orElse(null);
		
		if(!ObjectUtils.isEmpty(product)) {
			productRepository.deleteById(id);
			
			return true;
		}
		return false;
	}

	@Override
	public Product getProductById(int id) {
		Product product = productRepository.findById(id).orElse(null);
		return product;
	}

	@Override
	public List<Product> findAllProducts() {
		
		return productRepository.findAll();
	}

}
