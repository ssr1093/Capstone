package com.Medicare.Ecomm.Model;

import java.util.Date;
import java.util.List;

public class Orderinfo {

	private String id;
	private Date orderDate;
	private int orderNum;
	private double amount;

	private String customerName;
	private String customerAddress;
	private String customerEmail;
	private String customerPhone;

	private List<OrderDetailinfo> details;

	public Orderinfo() {

    }

	// Using for Hibernate Query.
	public Orderinfo(String id, Date orderDate, int orderNum, //
            double amount, String customerName, String customerAddress, //
            String customerEmail, String customerPhone) {
        this.id = id;
        this.orderDate = orderDate;
        this.orderNum = orderNum;
        this.amount = amount;

        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public int getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	public List<OrderDetailinfo> getDetails() {
		return details;
	}

	public void setDetails(List<OrderDetailinfo> details) {
		this.details = details;
	}
}
