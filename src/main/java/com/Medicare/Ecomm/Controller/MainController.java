package com.Medicare.Ecomm.Controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Medicare.Ecomm.Dao.OrderDao;
import com.Medicare.Ecomm.Dao.ProductDao;
import com.Medicare.Ecomm.Entity.Product;
import com.Medicare.Ecomm.Form.CustomerForm;
import com.Medicare.Ecomm.Model.CartInfo;
import com.Medicare.Ecomm.Model.Customerinfo;
import com.Medicare.Ecomm.Model.Productinfo;
import com.Medicare.Ecomm.Pagination.PaginationResult;
import com.Medicare.Ecomm.Utils.Utils;
import com.Medicare.Ecomm.Validator.CustomerFormValidator;

@Controller
@Transactional
public class MainController {
	@Autowired
	private OrderDao orderDAO;

	@Autowired
	private ProductDao productDAO;

	@Autowired(required = true)
	private CustomerFormValidator customerFormValidator;

	@InitBinder
	public void myInitBinder(WebDataBinder dataBinder) {
		Object target = dataBinder.getTarget();
		if (target == null) {
			return;
		}
		System.out.println("Target=" + target);

		// Case update quantity in cart
		// (@ModelAttribute("cartForm") @Validated CartInfo cartForm)
		if (target.getClass() == CartInfo.class) {

		}

		// Case save customer information.
		// (@ModelAttribute @Validated CustomerInfo customerForm)
		else if (target.getClass() == CustomerForm.class) {
			dataBinder.setValidator(customerFormValidator);
		}

	}

	@RequestMapping("/403")
	public String accessDenied() {
		return "/403";
	}

	@RequestMapping("/")
	public String home() {
		return "index";
	}

	// Product List
	@GetMapping({ "/productList" })
	public String listProductHandler(Model model, //
			@RequestParam(value = "name", defaultValue = "") String likeName,
			@RequestParam(value = "page", defaultValue = "1") int page) {
		final int maxResult = 5;
		final int maxNavigationPage = 10;

		PaginationResult<Productinfo> result = productDAO.queryProducts(page, //
				maxResult, maxNavigationPage, likeName);

		model.addAttribute("paginationProducts", result);
		return "productList";
	}

	@GetMapping({ "/buyProduct" })
	public String listProductHandler(HttpServletRequest request, Model model, //
			@RequestParam(value = "code", defaultValue = "") String code) {

		Product product = null;
		if (code != null && code.length() > 0) {
			product = productDAO.findProduct(code);
		}
		if (product != null) {

			//
			CartInfo cartInfo = Utils.getCartInSession(request);

			Productinfo productInfo = new Productinfo(product);

			cartInfo.addProduct(productInfo, 1);
		}

		return "redirect:/shoppingCart";
	}

	@PostMapping({ "/shoppingCartRemoveProduct" })
	public String removeProductHandler(HttpServletRequest request, Model model, //
			@RequestParam(value = "code", defaultValue = "") String code) {
		Product product = null;
		if (code != null && code.length() > 0) {
			product = productDAO.findProduct(code);
		}
		if (product != null) {

			CartInfo cartInfo = Utils.getCartInSession(request);

			Productinfo productInfo = new Productinfo(product);

			cartInfo.removeProduct(productInfo);

		}

		return "redirect:/shoppingCart";
	}

	// POST: Update quantity for product in cart
	@PostMapping(value = { "/shoppingCart" })
	public String shoppingCartUpdateQty(HttpServletRequest request, //
			Model model, //
			@ModelAttribute("cartForm") CartInfo cartForm) {

		CartInfo cartInfo = Utils.getCartInSession(request);
		cartInfo.updateQuantity(cartForm);

		return "redirect:/shoppingCart";
	}

	// GET: Show cart.
	@GetMapping(value = { "/shoppingCart" })
	public String shoppingCartHandler(HttpServletRequest request, Model model) {
		CartInfo myCart = Utils.getCartInSession(request);

		model.addAttribute("cartForm", myCart);
		return "shoppingCart";
	}

	// GET: Enter customer information.
	@GetMapping(value = { "/shoppingCartCustomer" })
	public String shoppingCartCustomerForm(HttpServletRequest request, Model model) {

		CartInfo cartInfo = Utils.getCartInSession(request);

		if (cartInfo.isEmpty()) {

			return "redirect:/shoppingCart";
		}
		Customerinfo customerInfo = cartInfo.getCustomerInfo();

		CustomerForm customerForm = new CustomerForm(customerInfo);

		model.addAttribute("customerForm", customerForm);

		return "shoppingCartCustomer";
	}

	// POST: Save customer information.
	@PostMapping(value = { "/shoppingCartCustomer" })
	public String shoppingCartCustomerSave(HttpServletRequest request, //
			Model model, //
			@ModelAttribute("customerForm") @Validated CustomerForm customerForm, //
			BindingResult result, //
			final RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			customerForm.setValid(false);
			// Forward to reenter customer info.
			return "shoppingCartCustomer";
		}

		customerForm.setValid(true);
		CartInfo cartInfo = Utils.getCartInSession(request);
		Customerinfo customerInfo = new Customerinfo(customerForm);
		cartInfo.setCustomerInfo(customerInfo);

		return "redirect:/shoppingCartConfirmation";
	}

	// GET: Show information to confirm.
	@GetMapping(value = { "/shoppingCartConfirmation" })
	public String shoppingCartConfirmationReview(HttpServletRequest request, Model model) {
		CartInfo cartInfo = Utils.getCartInSession(request);

		if (cartInfo == null || cartInfo.isEmpty()) {

			return "redirect:/shoppingCart";
		} else if (!cartInfo.isValidCustomer()) {

			return "redirect:/shoppingCartCustomer";
		}
		model.addAttribute("myCart", cartInfo);

		return "shoppingCartConfirmation";
	}

	// POST: Submit Cart (Save)
	@PostMapping(value = { "/shoppingCartConfirmation" })

	public String shoppingCartConfirmationSave(HttpServletRequest request, Model model) {
		CartInfo cartInfo = Utils.getCartInSession(request);

		if (cartInfo.isEmpty()) {

			return "redirect:/shoppingCart";
		} else if (!cartInfo.isValidCustomer()) {

			return "redirect:/shoppingCartCustomer";
		}
		try {
			orderDAO.saveOrder(cartInfo);
		} catch (Exception e) {

			return "shoppingCartConfirmation";
		}

		// Remove Cart from Session.
		Utils.removeCartInSession(request);

		// Store last cart.
		Utils.storeLastOrderedCartInSession(request, cartInfo);

		return "redirect:/shoppingCartFinalize";
	}

	@GetMapping(value = { "/shoppingCartFinalize" })
	public String shoppingCartFinalize(HttpServletRequest request, Model model) {

		CartInfo lastOrderedCart = Utils.getLastOrderedCartInSession(request);

		if (lastOrderedCart == null) {
			return "redirect:/shoppingCart";
		}
		model.addAttribute("lastOrderedCart", lastOrderedCart);
		return "shoppingCartFinalize";
	}

	@GetMapping(value = { "/productImage" })
	public void productImage(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam("code") String code) throws IOException {
		Product product = null;
		if (code != null) {
			product = this.productDAO.findProduct(code);
		}
		if (product != null && product.getImage() != null) {
			response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
			response.getOutputStream().write(product.getImage());
		}
		response.getOutputStream().close();
	}
}
