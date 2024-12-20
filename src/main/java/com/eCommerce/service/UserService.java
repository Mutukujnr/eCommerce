package com.eCommerce.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.eCommerce.model.User;

public interface UserService {

	public User saveUser(User user);
	
	public User findByEmail(String email);
	
	public List<User> getUsers(String role);

	

	Boolean updateStatus(Boolean status, Integer id);
	
	public void increaseFailAttempts(User user);
	
	public void userAccountLock(User user);
	
	public boolean unlockTimeExpired(User user);
	
	public void resetAttempts(Integer userId);

	public void updateUserResetToken(String email, String resetToken);
	
	public User findUserByResetToken(String token);
	
	public User updateUser(User user);
	
	public User updateUserProfile(User user,MultipartFile  image) throws IOException;
	
	
	public User saveAdmin(User user);
	
	public long getUserCount();
	
	
	
}
