package com.Medicare.Ecomm.Dao;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.Medicare.Ecomm.Entity.Order;
import com.Medicare.Ecomm.Entity.OrderDetails;
import com.Medicare.Ecomm.Entity.Product;
import com.Medicare.Ecomm.Model.CartInfo;
import com.Medicare.Ecomm.Model.CartLineinfo;
import com.Medicare.Ecomm.Model.Customerinfo;
import com.Medicare.Ecomm.Model.OrderDetailinfo;
import com.Medicare.Ecomm.Model.Orderinfo;
import com.Medicare.Ecomm.Pagination.PaginationResult;

@Transactional
@Repository
public class OrderDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private ProductDao productDAO;

	private int getMaxOrderNum() {
		String sql = "Select max(o.orderNum) from " + Order.class.getName() + " o ";
		Session session = this.sessionFactory.getCurrentSession();
		Query<Integer> query = session.createQuery(sql, Integer.class);
		Integer value = (Integer) query.getSingleResult();
		if (value == null) {
			return 0;
		}
		return value;
	}

	@Transactional(rollbackFor = Exception.class)
	public void saveOrder(CartInfo cartInfo) {
		Session session = this.sessionFactory.getCurrentSession();

		int orderNum = this.getMaxOrderNum() + 1;
		Order order = new Order();

		order.setId(UUID.randomUUID().toString());
		order.setOrderNum(orderNum);
		order.setOrderDate(new Date());
		order.setAmount(cartInfo.getAmountTotal());

		Customerinfo customerInfo = cartInfo.getCustomerInfo();
		order.setCustomerName(customerInfo.getName());
		order.setCustomerEmail(customerInfo.getEmail());
		order.setCustomerPhone(customerInfo.getPhone());
		order.setCustomerAddress(customerInfo.getAddress());

		session.persist(order);

		List<CartLineinfo> lines = cartInfo.getCartLines();

		for (CartLineinfo line : lines) {
			OrderDetails detail = new OrderDetails();
			detail.setId(UUID.randomUUID().toString());
			detail.setOrder(order);
			detail.setAmount(line.getAmount());
			detail.setPrice(line.getProductInfo().getPrice());
			detail.setQuanity(line.getQuantity());

			String code = line.getProductInfo().getCode();
			Product product = this.productDAO.findProduct(code);
			detail.setProduct(product);

			session.persist(detail);
		}

		// Order Number!
		cartInfo.setOrderNum(orderNum);
		// Flush
		session.flush();
	}

	// @page = 1, 2, ...
	public PaginationResult<Orderinfo> listOrderInfo(int page, int maxResult, int maxNavigationPage) {
		String sql = "Select new " + Orderinfo.class.getName()//
				+ "(ord.id, ord.orderDate, ord.orderNum, ord.amount, "
				+ " ord.customerName, ord.customerAddress, ord.customerEmail, ord.customerPhone) " + " from "
				+ Order.class.getName() + " ord "//
				+ " order by ord.orderNum desc";

		Session session = this.sessionFactory.getCurrentSession();
		Query<Orderinfo> query = session.createQuery(sql, Orderinfo.class);
		return new PaginationResult<Orderinfo>(query, page, maxResult, maxNavigationPage);
	}

	public Order findOrder(String orderId) {
		Session session = this.sessionFactory.getCurrentSession();
		return session.find(Order.class, orderId);
	}

	public Orderinfo getOrderInfo(String orderId) {
		Order order = this.findOrder(orderId);
		if (order == null) {
			return null;
		}
		return new Orderinfo(order.getId(), order.getOrderDate(), //
				order.getOrderNum(), order.getAmount(), order.getCustomerName(), //
				order.getCustomerAddress(), order.getCustomerEmail(), order.getCustomerPhone());
	}

	public List<OrderDetailinfo> listOrderDetailInfos(String orderId) {
		String sql = "Select new " + OrderDetailinfo.class.getName() //
				+ "(d.id, d.product.code, d.product.name , d.quanity,d.price,d.amount) "//
				+ " from " + OrderDetails.class.getName() + " d "//
				+ " where d.order.id = :orderId ";

		Session session = this.sessionFactory.getCurrentSession();
		Query<OrderDetailinfo> query = session.createQuery(sql, OrderDetailinfo.class);
		query.setParameter("orderId", orderId);

		return query.getResultList();
	}

}