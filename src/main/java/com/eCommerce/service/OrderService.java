package com.eCommerce.service;

import java.util.List;

import com.eCommerce.model.OrderRequest;
import com.eCommerce.model.ProductOrder;

public interface OrderService {

	public void saveOrder(Integer userId, OrderRequest orderRequest);
	
	public List<ProductOrder> getUserOrders(Integer userId);
	
	public boolean updateStatus(Integer id, String status);
}
