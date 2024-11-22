package com.eCommerce.service;

import com.eCommerce.model.Cart;

import java.util.List;

public interface CartService {
    public Cart saveCart(Integer productId, Integer userId);

    public List<Cart> getCartsByUserId(Integer userId);

    public Integer getCartCount(Integer userId);

	public void updateCartQuantity(String sy, Integer cid);
}
