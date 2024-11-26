package com.eCommerce.utils;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.eCommerce.model.ProductOrder;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;


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

String msg = null;
public Boolean sendProductOrderMail(ProductOrder productOrder, String status) throws MessagingException, Exception {
	MimeMessage message = mailSender.createMimeMessage();
	MimeMessageHelper helper = new MimeMessageHelper(message);
	
	
	helper.setFrom("mutukujuniour@gmail.com","eCommerce");
	helper.setTo(productOrder.getAddress().getEmail());
	
	
	
	msg = "<p>Order : [[orderStatus]]</p>"
			+"Hello [[userName]]<br><p>Your order has been received. </p><br>"
			+"<p style='text-decoration:underline'>Order Details:</p>"
			+"<p>Name : [[productName]]</p>"
			+"<p>Category : [[category]]</p>"
			+"<p>Quantity : [[quantity]]</p>"
			+"<p>Price : [[price]]</p>"
			
			;
	
	
	helper.setSubject("Your Order");
	helper.setText(msg, true);
	
	
	
msg=msg.replace(" [[orderStatus]]", status);
	msg=msg.replace(" [[userName]]", productOrder.getUser().getName());
	msg=msg.replace(" [[productName]]", productOrder.getProduct().getTitle());
	msg=msg.replace(" [[category]]", productOrder.getProduct().getCategory());
	msg=msg.replace(" [[quantity]]", productOrder.getQuantity().toString());
	msg=msg.replace(" [[price]]", productOrder.getProduct().getDiscountPrice().toString());
	//msg=msg.replace(" [[totalPrice]]",  (productOrder.getQuantity() * productOrder.getProduct().getDiscountPrice()));
	
	
	
	mailSender.send(message);
	return null;
	
}
}
