package com.Medicare.Ecomm.Model;

public class CartLineinfo {
	  private Productinfo productInfo;
	    private int quantity;
	 
	    public CartLineinfo() {
	        this.quantity = 0;
	    }
	 
	    public Productinfo getProductInfo() {
	        return productInfo;
	    }
	 
	    public void setProductInfo(Productinfo productInfo) {
	        this.productInfo = productInfo;
	    }
	 
	    public int getQuantity() {
	        return quantity;
	    }
	 
	    public void setQuantity(int quantity) {
	        this.quantity = quantity;
	    }
	 
	    public double getAmount() {
	        return this.productInfo.getPrice() * this.quantity;
	    }
	    
}
