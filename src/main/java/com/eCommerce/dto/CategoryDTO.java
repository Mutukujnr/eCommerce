package com.eCommerce.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;

public class CategoryDTO {

	//@NotEmpty(message = "The name is required")
	private String name;
	
	private MultipartFile imageName;
	
	//@NotEmpty(message="Select one of these")
	private Boolean isActive;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MultipartFile getImageName() {
		return imageName;
	}

	public void setImageName(MultipartFile imageName) {
		this.imageName = imageName;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
	
	
}
