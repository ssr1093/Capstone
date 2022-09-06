package com.Medicare.Ecomm.Model;

import java.util.ArrayList;
import java.util.List;

public class CartInfo {

	private int orderNum;

	private Customerinfo customerInfo;

	private final List<CartLineinfo> cartLines = new ArrayList<CartLineinfo>();

	public CartInfo() {

	}

	public int getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}

	public Customerinfo getCustomerInfo() {
		return customerInfo;
	}

	public void setCustomerInfo(Customerinfo customerInfo) {
		this.customerInfo = customerInfo;
	}

	public List<CartLineinfo> getCartLines() {
		return this.cartLines;
	}

	private CartLineinfo findLineByCode(String code) {
		for (CartLineinfo line : this.cartLines) {
			if (line.getProductInfo().getCode().equals(code)) {
				return line;
			}
		}
		return null;
	}

	public void addProduct(Productinfo productInfo, int quantity) {
		CartLineinfo line = this.findLineByCode(productInfo.getCode());

		if (line == null) {
			line = new CartLineinfo();
			line.setQuantity(0);
			line.setProductInfo(productInfo);
			this.cartLines.add(line);
		}
		int newQuantity = line.getQuantity() + quantity;
		if (newQuantity <= 0) {
			this.cartLines.remove(line);
		} else {
			line.setQuantity(newQuantity);
		}
	}

	public void validate() {

	}

	public void updateProduct(String code, int quantity) {
		CartLineinfo line = this.findLineByCode(code);

		if (line != null) {
			if (quantity <= 0) {
				this.cartLines.remove(line);
			} else {
				line.setQuantity(quantity);
			}
		}
	}

	public void removeProduct(Productinfo productInfo) {
		CartLineinfo line = this.findLineByCode(productInfo.getCode());
		if (line != null) {
			this.cartLines.remove(line);
		}
	}

	public boolean isEmpty() {
		return this.cartLines.isEmpty();
	}

	public boolean isValidCustomer() {
		return this.customerInfo != null && this.customerInfo.isValid();
	}

	public int getQuantityTotal() {
		int quantity = 0;
		for (CartLineinfo line : this.cartLines) {
			quantity += line.getQuantity();
		}
		return quantity;
	}

	public double getAmountTotal() {
		double total = 0;
		for (CartLineinfo line : this.cartLines) {
			total += line.getAmount();
		}
		return total;
	}

	public void updateQuantity(CartInfo cartForm) {
		if (cartForm != null) {
			List<CartLineinfo> lines = cartForm.getCartLines();
			for (CartLineinfo line : lines) {
				this.updateProduct(line.getProductInfo().getCode(), line.getQuantity());
			}
		}

	}

}