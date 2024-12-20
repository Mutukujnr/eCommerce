package com.eCommerce.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class ProductDTO {

	//@NotEmpty(message = "Product name is required")
private String title;
	
	//@Size(min=10,message="Description should be atleast 10 characters")
	//@Size(max=1000,message="Description should not exceed 1000 characters")
	private String description;
	
	//@NotEmpty(message="Category is required")
	private String category;
	
//	@Size(min=0)
	private Double price;
	
	//@Size(min=1)
	private int stock;
	
	private MultipartFile image;
	
	//@Size(min=0)
private int discount;
	
	public int getDiscount() {
	return discount;
}

public void setDiscount(int discount) {
	this.discount = discount;
}

public Double getDiscountPrice() {
	return discountPrice;
}

public void setDiscountPrice(Double discountPrice) {
	this.discountPrice = discountPrice;
}

public Boolean getIsActive() {
	return isActive;
}

public void setIsActive(Boolean isActive) {
	this.isActive = isActive;
}

	private Double discountPrice;
	
	private Boolean isActive;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public MultipartFile getImage() {
		return image;
	}

	public void setImage(MultipartFile image) {
		this.image = image;
	}

	
	
	
}
