package com.eCommerce.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.eCommerce.service.CommonService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class CommonServiceImpl implements CommonService{

	@Override
	public void removeSessionStorage() {
	
	HttpServletRequest httpServletRequest =	((ServletRequestAttributes)(RequestContextHolder.getRequestAttributes())).getRequest();
	HttpSession session = httpServletRequest.getSession();
	session.removeAttribute("succMsg");
	session.removeAttribute("errMsg");
		
	}

}
