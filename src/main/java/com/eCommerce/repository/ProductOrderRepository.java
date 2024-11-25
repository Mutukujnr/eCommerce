package com.eCommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eCommerce.model.ProductOrder;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer>{

	List<ProductOrder> findByUserId(Integer userId);

}
