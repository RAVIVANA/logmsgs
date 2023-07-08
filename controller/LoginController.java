package com.nkxgen.spring.jdbc.controller;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.nkxgen.spring.jdbc.DaoInterfaces.BankUserInterface;
import com.nkxgen.spring.jdbc.Exception.EmailNotFoundException;
import com.nkxgen.spring.jdbc.events.LoginEvent;
import com.nkxgen.spring.jdbc.events.LogoutEvent;
import com.nkxgen.spring.jdbc.service.ChartService;
import com.nkxgen.spring.jdbc.validation.MailSender;;

@Controller
public class LoginController {

	private String otp;
	private String mail;
	private long userID;
	private MailSender mailSender;
	private HttpSession httpSession;
	private ChartService chartService;
	private BankUserInterface bankUserService;
	private ApplicationEventPublisher applicationEventPublisher;

	public long getUserID() {
		return userID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	@Autowired
	public LoginController(ApplicationEventPublisher applicationEventPublisher, HttpSession httpSession,
			MailSender mailSender, BankUserInterface bankUserService, ChartService chartService) {
		this.applicationEventPublisher = applicationEventPublisher;
		this.httpSession = httpSession;
		this.mailSender = mailSender;
		this.bankUserService = bankUserService;
		this.chartService = chartService;
	}

	@RequestMapping(value = "/graphs", method = RequestMethod.GET)
	public String graphs(Locale locale, Model model) {

		List<Integer> accountData = chartService.getAccountData();
		List<Integer> loanData = chartService.getLoanData();

		List<String> accountLabels = chartService.getAccountLabels(); // Retrieve account label names
		List<String> loanLabels = chartService.getLoanLabels(); // Retrieve loan label names

		System.out.println("accountData" + accountData);
		System.out.println("loanData" + loanData);
		System.out.println("accountLabels" + accountLabels);
		System.out.println("loanLabels" + loanLabels);

		// Pass the data to the HTML view using the model
		model.addAttribute("accountData", accountData);
		model.addAttribute("loanData", loanData);

		// Add the label names to the model
		model.addAttribute("accountLabels", accountLabels);
		model.addAttribute("loanLabels", loanLabels);

		System.out.println("Graphs Method called");

		return "graphs";
	}

	// =====================================================================================================
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String login(Locale locale, Model model) {

		return "login-page";
	}

	@RequestMapping(value = "/updatedPassword", method = RequestMethod.POST)
	public String updatedPassword(@RequestParam("password") String newPassword, HttpSession session) {

		bankUserService.updatePassword(newPassword, getUserID());
		session.setAttribute("errorMessage", "Successfully Updated Password");

		return "redirect:/";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String loginPost(Locale locale, Model model) {

		return "redirect:/";
	}

	@RequestMapping(value = "/Test", method = RequestMethod.POST)
	public String homePage() {

		return "redirect:/home";
	}

	@RequestMapping(value = "/enterOTP", method = RequestMethod.GET)
	public String enterOTP(Locale locale, Model model) {

		setOtp(mailSender.send(getMail()));
		return "enter-otp";
	}

	@RequestMapping(value = "/logOut", method = RequestMethod.GET)
	public String login2(HttpServletRequest request, HttpServletResponse response) {
		// Get the session object from the request
		HttpSession session = request.getSession();

		// Get the username attribute from the session
		String username = (String) session.getAttribute("username");

		// Publish a LogoutEvent with the appropriate message and username
		applicationEventPublisher.publishEvent(new LogoutEvent("Logged Out", username));

		// Invalidate the session to remove all session attributes
		httpSession.invalidate();

		// Set Cache-Control headers to prevent caching of the page
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");

		// Change the return to the view name "LoginPage" to render the page
		return "redirect:/";
	}

	@RequestMapping(value = "/enterOtp", method = RequestMethod.POST)
	public String enterOtp(@RequestParam("email") String email, @RequestParam("userID") String userid,
			HttpSession session) throws EmailNotFoundException {
		try {

			setUserID(Long.parseLong(userid));

			if (bankUserService.getBankUserByEmail(email, userID)) {

				setMail(email);
				// Call the "send" method on the "mailSender" object to send the OTP to the specified email

				// Change the return to the view name "EnterOtp" to render the page
				return "redirect:/enterOTP";
			}
		} catch (EmailNotFoundException e) {

			String message = e.getMessage();
			session.setAttribute("message", message);
			return "redirect:/enterEmail";
		}
		return "redirect:/";

	}

	@RequestMapping(value = "/enterEmail")
	public String sendOtp() {
		return "enter-email";
	}

	@RequestMapping(value = "/confirmPass", method = RequestMethod.POST)
	public String confirmpass(@RequestParam("otp") String otp1, HttpSession session) {

		// Check if the entered OTP matches the sent OTP
		if (otp1.equals(getOtp()))
			return "confirm-pass"; // If OTP is correct, return the view name "confirmPass"
		else {
			session.setAttribute("errorMessage", "Invalid OTP"); // Add an error message to the session
			return "redirect:/"; // If OTP is incorrect, return the view name "EnterOtp"
		}
	}

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String main_page(Model model, HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession();
		String username = (String) session.getAttribute("username");

		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");

		if (username == null) {
			// User is not logged in, redirect to a login page
			return "redirect:/";
		}

		// Add the username as an attribute to the model
		model.addAttribute("username", username);

		// Publish a LoginEvent with the username
		applicationEventPublisher.publishEvent(new LoginEvent("Logged In", username));

		return "bank-home-page"; // Return the view name "BankHomePage" to render the page
	}

}