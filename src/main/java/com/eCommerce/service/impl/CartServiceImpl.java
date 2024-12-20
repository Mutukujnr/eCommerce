package com.eCommerce.service.impl;

import com.eCommerce.model.Cart;
import com.eCommerce.model.Product;
import com.eCommerce.model.User;
import com.eCommerce.repository.CartRepository;
import com.eCommerce.repository.ProductRepository;
import com.eCommerce.repository.UserRepository;
import com.eCommerce.service.CartService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements  CartService{
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Cart saveCart(Integer productId, Integer userId) {
        Product product = productRepository.findById(productId).get();
        User user = userRepository.findById(userId).get();

        Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);

        Cart cart = null;


        if(ObjectUtils.isEmpty(cartStatus)) {
            cart = new Cart();
            cart.setUser(user);
            cart.setProduct(product);
            cart.setQuantity(1);
            Double price = product.getDiscountPrice() == null ? product.getPrice() : product.getDiscountPrice();
            cart.setTotalPrice(price);
            

        }else{
            cart = cartStatus;
            cart.setQuantity(cart.getQuantity() + 1);
            Double price = cart.getProduct().getDiscountPrice() != null ? cart.getQuantity() * cart.getProduct().getDiscountPrice() : cart.getProduct().getPrice(); 
            cart.setTotalPrice(price);      
            }

        return cartRepository.save(cart); // This should save or update the cart
    }


    @Override
    public List<Cart> getCartsByUserId(Integer userId) {
        double totalOrderPrice = 0.0;
        List<Cart> cartItems = cartRepository.findByUserId(userId);

        List<Cart> updatedCart = new ArrayList<>();
        for (Cart c: cartItems){
           Double totalPrice = c.getTotalPrice();
           

           totalOrderPrice+=totalPrice;
           c.setTotalOrderPrice(totalOrderPrice);
           updatedCart.add(c);
        }


        return updatedCart;
    }

    @Override
    public Integer getCartCount(Integer userId) {

        return  cartRepository.countByUserId(userId);
    }


	@Override
	public void updateCartQuantity(String sy, Integer cid,Integer productId) {
		Cart cart = cartRepository.findById(cid).get();
		Product product = productRepository.findById(productId).get();
		Double price = product.getDiscountPrice();
		
		
		int updateQuantity;
		
		
		if(sy.equalsIgnoreCase("de")) {
			updateQuantity = cart.getQuantity()-1;
			
			if(updateQuantity <= 0) {
				cartRepository.delete(cart);
				
			}
				//cart.setQuantity(updateQuantity);
				cart.setTotalPrice(cart.getTotalPrice()-price);
				//cartRepository.save(cart);
			
		}else {
			updateQuantity = cart.getQuantity()+1;
			cart.setTotalPrice(cart.getTotalPrice()+price);
		}
		
		cart.setQuantity(updateQuantity);
		cartRepository.save(cart);
		
	}


	@Override
	public Cart getCartById(Integer cart) {
		
		Cart cartId = cartRepository.findById(cart).get();
		
		return cartId;
	
		
	}


	@Override
	public List<Cart> checkOutItems(Integer userId) {
		
		List<Cart> cart = cartRepository.findByUserId(userId);
		
		for(Cart c: cart) {
			System.out.println(c);
		
		}
		
		return cart;
	}
}
