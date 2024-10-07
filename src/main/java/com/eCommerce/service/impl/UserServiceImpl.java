package com.eCommerce.service.impl;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

}
