package com.eCommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eCommerce.model.Product;

import com.eCommerce.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>{

	Boolean existsByTitle(String title);

}
