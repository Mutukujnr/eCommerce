package com.eCommerce.service;

import java.util.List;

import com.eCommerce.model.User;

public interface UserService {

	public User saveUser(User user);
	
	public User findByEmail(String email);
	
	public List<User> getUsers(String role);

	

	Boolean updateStatus(Boolean status, Integer id);
}
