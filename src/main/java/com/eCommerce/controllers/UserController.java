package com.eCommerce.controllers;

import java.security.Principal;
import java.util.List;

import com.eCommerce.model.Cart;
import com.eCommerce.model.Product;
import com.eCommerce.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import com.eCommerce.model.Category;
import com.eCommerce.model.User;
import com.eCommerce.service.CategoryService;
import com.eCommerce.service.ProductService;
import com.eCommerce.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private CartService cartService;
	
	
	@ModelAttribute
	public void getUserDetails(Principal p,Model m) {
		if(p!=null) {
			String email = p.getName();
			User user = userService.findByEmail(email);
			
			m.addAttribute("user", user);

			Integer cartCount = cartService.getCartCount(user.getId());

			m.addAttribute("cartCount", cartCount);
		}
		
		List<Category> categories = categoryService.findAllActiveCategories();
		m.addAttribute("categories", categories);
	}

	@GetMapping("/")
	public String home() {
		return "user/index";
	}


	@GetMapping("/view-product/{id}")
	public String productDetails(@PathVariable int id,Model model) {

		Product product =productService.getProductById(id);
		model.addAttribute("product", product);

		return "view-product";

	}

	@GetMapping("/addToCart")
	public String addToCartSave(@RequestParam Integer pId, @RequestParam Integer uId, HttpSession session) {

			Cart cart = cartService.saveCart(pId, uId);

			if (ObjectUtils.isEmpty(cart)) {
				session.setAttribute("errorMsg", "Product adding to cart failed");
			} else {
				session.setAttribute("succMsg", "Product added to cart");
			}

		return "redirect:/view-product/"+pId;
	}



    private User getUser(Principal p){
        String email = p.getName();
        User user = userService.findByEmail(email);

        return user;
    }

    @GetMapping("/cart")
    public String loadCartPage(Principal p, Model m) {

    User user = getUser(p);
    List<Cart> cartItems = cartService.getCartsByUserId(user.getId());
    m.addAttribute("cartItems", cartItems);

    if(cartItems.size()>0) {
        Double totalOrderPrice = cartItems.get(cartItems.size() - 1).getTotalOrderPrice();
        m.addAttribute("totalOrderPrice", totalOrderPrice);
    }
        return "user/cart";

    }

    @GetMapping("/cartQuantityUpdate")
    public String updateCaertQuantity(@RequestParam String sy, @RequestParam Integer cid,@RequestParam Integer product) {

    cartService.updateCartQuantity(sy,cid,product);
    
        return "redirect:/user/cart";

    }
    
    @GetMapping("/checkout")
    public String checkOutPage(@RequestParam Integer user, Model m,Principal p) {

 
    	//User user = getUser(p);
    	List<Cart> order = cartService.checkOutItems(user);
    	
    	Double totalOrderCost = 0.0;
    	for(Cart c: order) {
    		totalOrderCost+=c.getTotalPrice();
    	}
    	
    	m.addAttribute("order", order);
    	m.addAttribute("total", totalOrderCost);
    	
    
        return "user/checkout";

    }

}
