package com.eCommerce.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.eCommerce.model.Cart;
import com.eCommerce.model.Category;
import com.eCommerce.model.OrderRequest;
import com.eCommerce.model.Product;
import com.eCommerce.model.ProductOrder;
import com.eCommerce.model.User;
import com.eCommerce.service.CartService;
import com.eCommerce.service.CategoryService;
import com.eCommerce.service.OrderService;
import com.eCommerce.service.ProductService;
import com.eCommerce.service.UserService;
import com.eCommerce.utils.CommonUtils;
import com.eCommerce.utils.OrderStatus;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

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
	
	@Autowired
	OrderService orderService;
	/*
	 * @Autowired private CommonUtils commonUtils;
	 */
	@Autowired
	PasswordEncoder encoder;
	
	
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

 
    	User loggedInUser = getUser(p);
    	
    	
    	
    	
    	List<Cart> order = cartService.checkOutItems(user);
    	
    	Double totalOrderCost = 0.0;
    	for(Cart c: order) {
    		totalOrderCost+=c.getTotalPrice();
    	}
    	
    	m.addAttribute("order", order);
    	m.addAttribute("total", totalOrderCost);
    	m.addAttribute("user", loggedInUser);
    	
    
        return "user/checkout";

    }
    
    
    @PostMapping("/saveOrder")
    public String saveOrder(Principal p,@ModelAttribute OrderRequest orderRequest) {
    	System.out.println(orderRequest);
    	
    	User user = getUser(p);
    	
    	orderService.saveOrder(user.getId(), orderRequest);
    	
    	
    	
    	
		return "redirect:/user/track-order";
    	
    }
    
    @GetMapping("/track-order")
    public String trackOrder(Principal p,Model m) {
    	
    	User user = getUser(p);
    	
    	List<ProductOrder> userOrders = orderService.getUserOrders(user.getId());
    	
    	List<Cart> order = cartService.checkOutItems(user.getId());
    	
    	Double totalOrderCost = 0.0;
    	for(Cart c: order) {
    		totalOrderCost+=c.getTotalPrice();
    	}
    	
     	m.addAttribute("items", userOrders);
     	m.addAttribute("total", totalOrderCost);
		return "/user/order-success";
    	
    }
    
    @GetMapping("/update-status")
    public String updateStatus(@RequestParam Integer id, @RequestParam Integer status,HttpSession session) {
    	
    	OrderStatus[] orderStatus = OrderStatus.values();
    	
    	String orderSt = null;
    	for(OrderStatus st: orderStatus) {
    		if(st.getId().equals(status)) {
    			
    			orderSt=st.getName();
    		}
    	}
    	
    	ProductOrder updateStatus = orderService.updateStatus(id, orderSt);
		/*
		 * try { commonUtils.sendProductOrderMail(updateStatus,orderSt); } catch
		 * (Exception e) {
		 * 
		 * e.printStackTrace(); }
		 */
    	
    	if(!ObjectUtils.isEmpty(updateStatus)) {
    		session.setAttribute("succMsg", "You have cancelled the order");
		}else { 
			session.setAttribute("errMsg", "Failed to cancel order");
		
    	}
		return "redirect:/user/track-order";
    	
    }
    
    @GetMapping("/profile")
public String showProfile() {

		
		return "/user/profile";

	}
    
    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute User user, MultipartFile  image,HttpSession session) throws IOException {

    	User updateUserProfile = userService.updateUserProfile(user, image);
    	
    		if(!ObjectUtils.isEmpty(updateUserProfile)) {
    			session.setAttribute("succMsg", "Your profile has been updated");
    		}else { 
    			session.setAttribute("errMsg", "Failed to update");
    		}
    		return "redirect:/user/profile";

    	}
    
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword, @RequestParam String newPassword,@RequestParam String confirmPassword,Principal p,HttpSession session) {
    	
    	User user = getUser(p);
    	
    	
    	
    	boolean matches = encoder.matches(currentPassword, user.getPassword());
    	
    	if(matches) {
    		if(newPassword.equals(confirmPassword)) {
    			String encodePassword= encoder.encode(newPassword);
    			user.setPassword(encodePassword);
    			
    			userService.updateUser(user);
    			
    			session.setAttribute("succMsg", "Your password has been successfully updated");
    			
    		}else {
    			session.setAttribute("errMsg", " Password mismatch");
    		}
    	}else {
    		session.setAttribute("errMsg", "Incorrect Password");
    	}
		return "redirect:/user/profile";
    	
    }

}
