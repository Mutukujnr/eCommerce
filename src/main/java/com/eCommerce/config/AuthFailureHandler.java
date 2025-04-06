package com.eCommerce.config;

import com.eCommerce.model.User;
import com.eCommerce.repository.UserRepository;
import com.eCommerce.service.UserService;
import com.eCommerce.utils.AppConstraint;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler{
	
	@	Autowired
	private UserRepository userRepository;
	
	@	Autowired
	private UserService userService;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {


		String email = request.getParameter("username");
		
		User user = userRepository.findByEmail(email);
		if(user != null ) {
		if(user.getIsEnabled()) {
			
			if(user.getAccountNonLocked()) {
				
				if(user.getFailAttempts() < AppConstraint.ATTEMPT_LIMIT) {
					
					userService.increaseFailAttempts(user);
				}else {
					userService.userAccountLock(user);
					exception = new LockedException("your account is locked. You have reached your Failed attempts limit");
				}
				
			}else {
				
				if(userService.unlockTimeExpired(user)) {
					exception = new LockedException("your account is unlocked!! Please login");
				}else {
				exception = new LockedException("your account is locked!! Please try again  later");
				}
			}
		}else {
			exception = new LockedException("your account is inactive");
		}
		}else {
			exception = new LockedException("invalid email and password");
		}
		
		super.setDefaultFailureUrl("/signin?error");
		super.onAuthenticationFailure(request, response, exception);
	}

	
}
