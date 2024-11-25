package com.eCommerce.model;

import java.time.LocalDate;
import java.util.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class ProductOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String orderId;
	
	private LocalDate orderDate;
	
	@ManyToOne
	private Product product;
	
	private Double price;
	
	private Integer quantity;
	
	@ManyToOne
	private User user;
	
	private String status;
	
	private String paymentType;
	
	@OneToOne(cascade = CascadeType.ALL)
	private OrderAddress address;

	public ProductOrder() {
		super();
	}

	public ProductOrder(String orderId, LocalDate orderDate, Product product, Double price, Integer quantity, User user,
			String status, String paymentType, OrderAddress address) {
		super();
		this.orderId = orderId;
		this.orderDate = orderDate;
		this.product = product;
		this.price = price;
		this.quantity = quantity;
		this.user = user;
		this.status = status;
		this.paymentType = paymentType;
		this.address = address;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public LocalDate getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDate orderDate) {
		this.orderDate = orderDate;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public OrderAddress getAddress() {
		return address;
	}

	public void setAddress(OrderAddress address) {
		this.address = address;
	}
	
	
	
	
}
