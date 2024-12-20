package com.eCommerce.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.eCommerce.dto.CategoryDTO;
import com.eCommerce.model.Category;

public interface CategoryService {

	public Category saveCategory(CategoryDTO categoryDTO);
	
	public Category saveCategory(Category category);
	
	public Boolean existsCategory(String name);
	
	public boolean deleteCategoryById(int id);
	
	public Category getCategoryById(int id);
	
	
	List<Category> findAllCategories();
	
	public List<Category> findAllActiveCategories();
	

	Page<Category> getAllCategoriesPagination(Integer pageNo, Integer pageSize);
	
	public long countActiveCategories();
}
