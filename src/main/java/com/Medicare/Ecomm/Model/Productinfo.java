package com.Medicare.Ecomm.Model;

import com.Medicare.Ecomm.Entity.Product;

public class Productinfo {
	private String code;
	private String name;
	private double price;

	public Productinfo() {
    }

	public Productinfo(Product product) {
        this.code = product.getCode();
        this.name = product.getName();
        this.price = product.getPrice();
    }

	// Using in JPA/Hibernate query
	public Productinfo(String code, String name, double price) {
        this.code = code;
        this.name = name;
        this.price = price;
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
}
