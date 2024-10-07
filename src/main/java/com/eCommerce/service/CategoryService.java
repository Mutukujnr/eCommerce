package com.eCommerce.service;

import java.util.List;

import com.eCommerce.model.Category;

public interface CategoryService {

	public Category saveCategory(Category category);
	
	public Boolean existsCategory(String name);
	
	public boolean deleteCategoryById(int id);
	
	public Category getCategoryById(int id);
	
	List<Category> findAllCategories();
	
	public List<Category> findAllActiveCategories();
}
