package com.eCommerce.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.eCommerce.dto.ProductDTO;
import com.eCommerce.model.Product;
import com.eCommerce.repository.ProductRepository;
import com.eCommerce.service.ProductService;

import jakarta.transaction.Transactional;

@Service
public class ProductServiceImpl implements ProductService{

	@Autowired
	private ProductRepository productRepository;
	
	
	  @Override public Product saveProduct(Product product) {
	  
			
	  
	  return productRepository.save(product); }
	
	
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


	@Override
	public Product updateProduct(Product product, MultipartFile image) {
		
		Product existingProduct = getProductById(product.getId());
		String imageName = image.isEmpty()?existingProduct.getImage():image.getOriginalFilename();
		
		existingProduct.setTitle(product.getTitle());
		  existingProduct.setDescription(product.getDescription());
		  existingProduct.setCategory(product.getCategory());
		  existingProduct.setPrice(product.getPrice());
		  existingProduct.setStock(product.getStock());
		  
		  existingProduct.setImage(imageName); 
		  
		  existingProduct.setDiscount(product.getDiscount());
		  Double discount = product.getPrice() * (product.getDiscount()/100.0);
		  Double discountPrice = product.getPrice() - discount;
		  existingProduct.setDiscountPrice(discountPrice);
		  existingProduct.setIsActive(product.getIsActive());
		  
		  Product updateProduct = productRepository.save(existingProduct);
		  
		  if(!ObjectUtils.isEmpty(updateProduct)) {
			  if(!image.isEmpty()) {
				  try {
					  File saveFile = new ClassPathResource("static/Images").getFile();
					  Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"products"+File.separator+image.getOriginalFilename());
						
						
						Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				  }catch(Exception e) {
					  
				  }
			  }
			  
			  return product;
		  }
		  
		return null;
	}


	@Override
	public List<Product> findAllActiveProducts(String category) {
		List<Product> products = null;
		
		if(ObjectUtils.isEmpty(category)) {
			products  = productRepository.findByIsActiveTrue();
		}else {
			
			products = productRepository.findByCategory(category);
		}
		
		
		return products;
	}

}
