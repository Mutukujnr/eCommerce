package com.eCommerce.utils;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CommonUtils {
	
	@Autowired
	private  JavaMailSender mailSender;

	public  Boolean sendEmail(String url, String recipientEmail) throws UnsupportedEncodingException, MessagingException {
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		
		helper.setFrom("mutukujuniour@gmail.com","eCommerce");
		helper.setTo(recipientEmail);
		
		String content = "<p>Hello,</p>"
			    + "<p>You have requested to reset your password.</p>"
			    + "<p>Click the link below to reset your password:</p>" 
			    + "<p><a href='" + url + "'>Change my password</a></p>";

		
		helper.setSubject("password reset");
		helper.setText(content, true);
		
		mailSender.send(message);
		
		return true;
		
	}
	
public  String generateURL(HttpServletRequest request ) {
		
	
		String siteUrl = request.getRequestURL().toString();
		
		
		
		return  siteUrl.replace(request.getServletPath(), "");
		
		
	}
}
