package com.Medicare.Ecomm.Controller;

import java.util.List;

import org.apache.tomcat.util.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Medicare.Ecomm.Dao.OrderDao;
import com.Medicare.Ecomm.Dao.ProductDao;
import com.Medicare.Ecomm.Entity.Product;
import com.Medicare.Ecomm.Form.ProductForm;
import com.Medicare.Ecomm.Model.OrderDetailinfo;
import com.Medicare.Ecomm.Model.Orderinfo;
import com.Medicare.Ecomm.Pagination.PaginationResult;
import com.Medicare.Ecomm.Validator.ProductFormValidator;

@Controller
@Transactional
public class AdminController {
	@Autowired
	private OrderDao orderDAO;

	@Autowired
	private ProductDao productDAO;

	@Autowired
	private ProductFormValidator productFormValidator;

	@InitBinder
	public void myInitBinder(WebDataBinder dataBinder) {
		Object target = dataBinder.getTarget();
		if (target == null) {
			return;
		}
		System.out.println("Target=" + target);

		if (target.getClass() == ProductForm.class) {
			dataBinder.setValidator(productFormValidator);
		}
	}

	// GET: Show Login Page
	@GetMapping(value = { "/admin/login" })
	public String login(Model model) {

		return "login";
	}

	@GetMapping(value = { "/admin/accountInfo" })
	public String accountInfo(Model model) {

		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		System.out.println(userDetails.getPassword());
		System.out.println(userDetails.getUsername());
		System.out.println(userDetails.isEnabled());

		model.addAttribute("userDetails", userDetails);
		return "accountInfo";
	}

	@GetMapping(value = { "/admin/orderList" })
	public String orderList(Model model, //
			@RequestParam(value = "page", defaultValue = "1") String pageStr) {
		int page = 1;
		try {
			page = Integer.parseInt(pageStr);
		} catch (Exception e) {
		}
		final int MAX_RESULT = 5;
		final int MAX_NAVIGATION_PAGE = 10;

		PaginationResult<Orderinfo> paginationResult //
				= orderDAO.listOrderInfo(page, MAX_RESULT, MAX_NAVIGATION_PAGE);

		model.addAttribute("paginationResult", paginationResult);
		return "orderList";
	}

	// GET: Show product.
	@GetMapping(value = { "/admin/product" })
	public String product(Model model, @RequestParam(value = "code", defaultValue = "") String code) {
		ProductForm productForm = null;

		if (code != null && code.length() > 0) {
			Product product = productDAO.findProduct(code);
			if (product != null) {
				productForm = new ProductForm(product);
			}
		}
		if (productForm == null) {
			productForm = new ProductForm();
			productForm.setNewProduct(true);
		}
		model.addAttribute("productForm", productForm);
		return "product";
	}

	// POST: Save product
	@PostMapping(value = { "/admin/product" })
	public String productSave(Model model, //
			@ModelAttribute("productForm") @Validated ProductForm productForm, //
			BindingResult result, //
			final RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			return "product";
		}
		try {
			productDAO.save(productForm);
		} catch (Exception e) {
			Throwable rootCause = ExceptionUtils.unwrapInvocationTargetException(e);
			String message = rootCause.getMessage();
			model.addAttribute("errorMessage", message);
			// Show product form.
			return "product";
		}

		return "redirect:/productList";
	}

	@GetMapping(value = { "/admin/order" })
	public String orderView(Model model, @RequestParam("orderId") String orderId) {
		Orderinfo orderInfo = null;
		if (orderId != null) {
			orderInfo = this.orderDAO.getOrderInfo(orderId);
		}
		if (orderInfo == null) {
			return "redirect:/admin/orderList";
		}
		List<OrderDetailinfo> details = this.orderDAO.listOrderDetailInfos(orderId);
		orderInfo.setDetails(details);

		model.addAttribute("orderInfo", orderInfo);

		return "order";
	}
}
