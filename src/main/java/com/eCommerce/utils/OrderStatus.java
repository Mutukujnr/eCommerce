package com.eCommerce.utils;

public enum OrderStatus {

	IN_PROGRESS(1,"in progress"),
	ORDER_RECEIVED(2,"order received"),
	PRODUCT_PACKED(3,"product packed"),
	OUT_FOR_DELIVERY(4,"Out for Delivery"),
	DELIVERD(5,"Delivered"),CANCEL(6,"Cancelled"),SUCCESS(6,"Success");
	
	private Integer id;
	
	private String name;

	private OrderStatus(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
