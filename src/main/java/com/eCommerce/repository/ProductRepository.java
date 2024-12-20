package com.eCommerce.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eCommerce.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>{

	Boolean existsByTitle(String title);

	List<Product> findByIsActiveTrue();

	List<Product> findByCategory(String category);
	
	List<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String search,String search2);

	Page<Product> findByIsActiveTrue(Pageable pageable);

	Page<Product> findByCategory(Pageable pageable,String category);
	
	Page<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String search,String search2, Pageable pageable);

	

}
