package com.eCommerce.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eCommerce.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

	public Boolean existsByName(String name);

	public List<Category> findByIsActiveTrue(Sort sort);
	
	 @Query("SELECT COUNT(c) FROM Category c")
	 Long countActiveCategories();
}
