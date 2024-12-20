package com.eCommerce.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.eCommerce.model.Category;
import com.eCommerce.model.User;
import com.eCommerce.repository.UserRepository;
import com.eCommerce.service.UserService;
import com.eCommerce.utils.AppConstraint;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public User saveUser(User user) {
	
		user.setRole("ROLE_USER");
		user.setIsEnabled(true);
		String encode = passwordEncoder.encode(user.getPassword());
		user.setPassword(encode);
		user.setAccountNonLocked(true);
		user.setFailAttempts(0);
		user.setLockTime(null);
		
		return userRepository.save(user);
	}

	@Override
	public User findByEmail(String email) {
		// TODO Auto-generated method stub
		return userRepository.findByEmail(email);
	}

	@Override
	public List<User> getUsers(String role) {
	
		
		return userRepository.findByRole(role);
	}

	@Override
	public Boolean updateStatus(Boolean status, Integer id) {
		
		Optional<User> byId = userRepository.findById(id);
		
		if(byId.isPresent()) {
			User user = byId.get();
			user.setIsEnabled(status);
			userRepository.save(user);
			
			return true;
		}
		return false;
	}

	@Override
	public void increaseFailAttempts(User user) {
		int attempts = user.getFailAttempts()+1;
		user.setFailAttempts(attempts);
		userRepository.save(user);
		
	}

	@Override
	public void userAccountLock(User user) {
		user.setAccountNonLocked(false);
		user.setLockTime(new Date(0));
		userRepository.save(user);
		
	}

	@Override
	public boolean unlockTimeExpired(User user) {
		long lockTime = user.getLockTime().getTime();
		
		long unLockTime = lockTime + AppConstraint.UNLOCK_DURATION_TIME;
		
		long currentTime = System.currentTimeMillis();
		
		if(unLockTime < currentTime) {
			user.setAccountNonLocked(true);
			user.setFailAttempts(0);
			user.setLockTime(null);
			
			userRepository.save(user);
			return true;
		}
		
		return false;
	}

	@Override
	public void resetAttempts(Integer userId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateUserResetToken(String email, String resetToken) {
		User byEmail = userRepository.findByEmail(email);
		
		if(!ObjectUtils.isEmpty(byEmail)){
			byEmail.setResetToken(resetToken);
			
			userRepository.save(byEmail);
		}
		
	}

	@Override
	public User findUserByResetToken(String token) {
		
		User byToken = userRepository.findByResetToken(token);
		return byToken;
	}

	@Override
	public User updateUser(User user) {
		
		return userRepository.save(user);
	}

	@Override
	public User updateUserProfile(User user,MultipartFile  image) throws IOException {
		
		Optional<User> option  = userRepository.findById(user.getId());
		User existingUser = null;
		
		/*
		 * if(!image.isEmpty()) { existingUser.setProfile(image.getOriginalFilename());
		 * }
		 */
		
		if(option.isPresent()) {
			
			 existingUser = option.get();
			existingUser.setName(user.getName());
			existingUser.setMobileNumber(user.getMobileNumber());
			existingUser.setEmail(user.getEmail());
			existingUser.setAddress(user.getAddress());
			existingUser.setCity(user.getCity());
			existingUser.setState(user.getState());
			existingUser.setPinCode(user.getPinCode());
			
		
		
		
		
		try {
			
			if(!image.getOriginalFilename().isEmpty()) {
				String upload = "public/Images";
				String uploadDir = upload + "/profile/";
				Path oldImage = Paths.get(uploadDir + user.getProfile());
				
				try {
					Files.delete(oldImage);
				}catch(Exception e) {
					e.printStackTrace();
				}
				
				//MultipartFile profile = categoryDTO.getImageName();
				String storageFileName = image.getOriginalFilename();
				
				try (InputStream inputStream = image.getInputStream()) {
					Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
				}
				
				existingUser.setProfile(storageFileName);
			} 
			
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			
		}
		
		}
		
		
		/*
		 * if (!image.isEmpty()) { existingUser.setProfile(image.getOriginalFilename());
		 * File saveFile = new ClassPathResource("static/Images").getFile(); Path path =
		 * Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" +
		 * File.separator + image.getOriginalFilename());
		 * 
		 * System.out.println(path); Files.copy(image.getInputStream(), path,
		 * StandardCopyOption.REPLACE_EXISTING); }
		 */
		
		return userRepository.save(existingUser);
	}

	@Override
	public User saveAdmin(User user) {
		user.setRole("ROLE_ADMIN");
		user.setIsEnabled(true);
		String encode = passwordEncoder.encode(user.getPassword());
		user.setPassword(encode);
		user.setAccountNonLocked(true);
		user.setFailAttempts(0);
		user.setLockTime(null);
		return userRepository.save(user);
	}

	
	
	
	@Override
	public long getUserCount() {
		// TODO Auto-generated method stub
		return userRepository.countCustomers();
	}

}
