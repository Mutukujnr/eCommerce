package com.eCommerce.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.eCommerce.dto.CategoryDTO;
import com.eCommerce.model.Category;
import com.eCommerce.repository.CategoryRepository;
import com.eCommerce.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService{

	@Autowired
	CategoryRepository categoryRepository;
	
	@Override
	public Category saveCategory(CategoryDTO categoryDTO) {
		MultipartFile image = categoryDTO.getImageName();
		String storageFileName = image.getOriginalFilename();
		
		Category categorySave = new Category(categoryDTO.getName(),categoryDTO.getImageName().getOriginalFilename(), categoryDTO.getIsActive());
		
		return categoryRepository.save(categorySave);
	}

	@Override
	public List<Category> findAllCategories() {
	
		return categoryRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}

	@Override
	public Boolean existsCategory(String name) {
		
		
		return categoryRepository.existsByName(name);
	}

	@Override
	public boolean deleteCategoryById(int id) {
		Category category = categoryRepository.findById(id).orElse(null);
		
		if(!ObjectUtils.isEmpty(category)) {
			categoryRepository.deleteById(id);
			
			return true;
		}
		return false;
	}

	@Override
	public Category getCategoryById(int id) {
		Category category = categoryRepository.findById(id).orElse(null);
		return category;
	}

	@Override
	public List<Category> findAllActiveCategories() {
		
		List<Category> categories = categoryRepository.findByIsActiveTrue(Sort.by(Sort.Direction.ASC, "name"));
		return categories;
	}

	@Override
	public Page<Category> getAllCategoriesPagination(Integer pageNo, Integer pageSize) {
		
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		

		return categoryRepository.findAll(pageable);
	}



	@Override
	public Category saveCategory(Category category) {
		
		return categoryRepository.save(category);
	}

	@Override
	public long countActiveCategories() {
	
		return categoryRepository.countActiveCategories();
	}

}
