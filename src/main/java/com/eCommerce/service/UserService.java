package com.eCommerce.service;

import com.eCommerce.model.User;

public interface UserService {

	public User saveUser(User user);
	
	public User findByEmail(String email);
}
