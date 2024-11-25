package com.eCommerce.service.impl;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eCommerce.model.Cart;
import com.eCommerce.model.OrderAddress;
import com.eCommerce.model.OrderRequest;
import com.eCommerce.model.ProductOrder;
import com.eCommerce.repository.CartRepository;
import com.eCommerce.repository.ProductOrderRepository;
import com.eCommerce.repository.ProductRepository;
import com.eCommerce.repository.UserRepository;
import com.eCommerce.service.OrderService;
import com.eCommerce.utils.OrderStatus;

@Service
public class ProductOrderServiceImpl implements OrderService{
	
	@Autowired
	private ProductOrderRepository productOrderRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public void saveOrder(Integer userId, OrderRequest orderRequest) {
		
		List<Cart> cart = cartRepository.findByUserId(userId);
		
		for(Cart c: cart) {
			
			ProductOrder order = new ProductOrder();
			
			order.setOrderId(UUID.randomUUID().toString());
			order.setOrderDate(LocalDate.now());
			order.setProduct(c.getProduct());
			order.setPrice(c.getProduct().getDiscountPrice());
			order.setQuantity(c.getQuantity());
			order.setUser(c.getUser());
			order.setStatus(OrderStatus.IN_PROGRESS.getName());
			
			OrderAddress address = new OrderAddress();
			
			address.setName(orderRequest.getName());
			address.setMobileNumber(orderRequest.getMobileNumber());
			address.setEmail(orderRequest.getEmail());
			address.setAddress(orderRequest.getAddress());
			address.setCity(orderRequest.getCity());
			address.setState(orderRequest.getState());
			address.setPinCode(orderRequest.getPinCode());
			
			order.setAddress(address);
			
			productOrderRepository.save(order);
		}
		
	}

	

	@Override
	public List<ProductOrder> getUserOrders(Integer userId) {
		
		List<ProductOrder> items = productOrderRepository.findByUserId(userId);
		
		return items;
	}



	@Override
	public boolean updateStatus(Integer id, String status) {
		
		Optional<ProductOrder> findById = productOrderRepository.findById(id);
		
		if(findById.isPresent()) {
			ProductOrder productOrder = findById.get();
			productOrder.setStatus(status);
			productOrderRepository.save(productOrder);
			
			return true;
		}
	
		
		return false;
	}

}