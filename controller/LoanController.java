package com.nkxgen.spring.jdbc.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.nkxgen.spring.jdbc.Bal.ViewInterface;
import com.nkxgen.spring.jdbc.Dao.PermissionsDAO;
import com.nkxgen.spring.jdbc.DaoInterfaces.CustomerDaoInterface;
import com.nkxgen.spring.jdbc.DaoInterfaces.LoanApplicationDaoInterface;
import com.nkxgen.spring.jdbc.Exception.AccountNotFound;
import com.nkxgen.spring.jdbc.Exception.ApplicationNotFound;
import com.nkxgen.spring.jdbc.InputModels.LoanApplicationInput;
import com.nkxgen.spring.jdbc.ViewModels.LoanAccountViewModel;
import com.nkxgen.spring.jdbc.ViewModels.LoanApplicationViewModel;
import com.nkxgen.spring.jdbc.ViewModels.LoanViewModel;
import com.nkxgen.spring.jdbc.events.LoanAppApprovalEvent;
import com.nkxgen.spring.jdbc.events.LoanAppRequestEvent;
import com.nkxgen.spring.jdbc.model.LoanApplication;
import com.nkxgen.spring.jdbc.model.Permission;

@Controller
public class LoanController {
    
	Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	LoanApplicationDaoInterface ll;
	@Autowired
	CustomerDaoInterface cd;
	@Autowired
	ViewInterface v;
	@Autowired
	private PermissionsDAO permissionsDAO;
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@RequestMapping(value = "/loanNewApplicationForm", method = RequestMethod.GET)
	public String loanNewApplicationForm(Model model) {
	    LOGGER.info("Handling GET request for /loanNewApplicationForm");
	    List<LoanViewModel> list = v.getAllLoans();
	    LOGGER.debug("Retrieving list of all loans");
	    model.addAttribute("loan", list);
	    LOGGER.info("Adding list of loans to model attribute 'loan'");
	    return "loan-new-application-form";
	}

	@RequestMapping(value = "/editForm", method = RequestMethod.GET)
	public String editForm(Model model) {
	    LOGGER.info("Handling GET request for /editForm");
	    return "edit-form";
	}

	@RequestMapping(value = "/redirected", method = RequestMethod.GET)
	public String redirectForm(Model model) {
	    try {
	        LOGGER.info("Handling GET request for /redirected");
	        List<LoanApplicationViewModel> list = v.getLoanApplicationsByStatus("redirecting");
	        LOGGER.debug("Retrieving list of loan applications with status 'redirecting'");
	        model.addAttribute("loanApplications", list);
	        LOGGER.info("Adding list of loan applications to model attribute 'loanApplications'");
	        return "redirected-applications";
	    } catch (Exception e) {
	        LOGGER.error("An error occurred while processing the request", e);
	        throw new DataIntegrityViolationException("Foreign key violation occurred.", e);
	    }
	}

	@RequestMapping(value = "/newLoanApplication", method = RequestMethod.POST)
	public String newLoanApplication(@Validated LoanApplicationInput l, Model model, HttpServletRequest request) {
	    try {
	        LOGGER.info("Handling POST request for /newLoanApplication");
	        LoanApplication loan = new LoanApplication();
	        loan.LoanApplication(l);
	        ll.saveLoanApplication(loan);
	        HttpSession session = request.getSession();
	        String username = (String) session.getAttribute("username");
	        applicationEventPublisher.publishEvent(new LoanAppRequestEvent("New Loan Application Filled", username));
	        return "loan-new-application-form";
	    } catch (Exception e) {
	        LOGGER.error("An error occurred while processing the new loan application", e);
	        model.addAttribute("error", "An error occurred while processing the new loan application.");
	        return "error-view";
	    }
	}

	@RequestMapping(value = "/updateApplication", method = RequestMethod.POST)
	public String updateLoanApplication(@Validated LoanApplicationInput loanApplication, HttpServletRequest request, Model model) {
	    try {
	        LOGGER.info("Handling POST request for /updateApplication");
	        ll.updateLoanApplication(loanApplication);
	        HttpSession session = request.getSession();
	        String username = (String) session.getAttribute("username");
	        applicationEventPublisher.publishEvent(new LoanAppApprovalEvent("Loan Application Updated", username));
	        return "loan-approval";
	    } catch (Exception e) {
	        LOGGER.error("An error occurred while updating the loan application", e);
	        model.addAttribute("error", "An error occurred while updating the loan application.");
	        return "error-view";
	    }
	}

	@RequestMapping(value = "/update_application", method = RequestMethod.POST)
	public String updateLoanApplication1(@Validated LoanApplicationInput loanApplication, HttpServletRequest request, Model model) {
	    try {
	        LOGGER.info("Handling POST request for /update_application");
	        ll.updateLoanApplication(loanApplication);
	        HttpSession session = request.getSession();
	        String username = (String) session.getAttribute("username");
	        applicationEventPublisher.publishEvent(new LoanAppApprovalEvent("Loan Application Updated", username));
	        return "loan-approval";
	    } catch (Exception e) {
	        LOGGER.error("An error occurred while updating the loan application", e);
	        model.addAttribute("error", "An error occurred while updating the loan application.");
	        return "error-view";
	    }
	}

	@RequestMapping(value = "/getApplications", method = RequestMethod.POST)
	public String GetLoanApplication(@RequestParam("Typevalue") String accountType, Model model, HttpServletRequest request, HttpServletResponse response) {
	    LOGGER.info("Handling POST request for /getApplications");
	    List<LoanApplicationViewModel> list = v.getLoanApplicationByValue(accountType);
	    HttpSession session = request.getSession();
	    List<LoanApplicationViewModel> list1 = new ArrayList<>();
	    String username = (String) session.getAttribute("username");
	    Permission p = permissionsDAO.getPermissions(Long.parseLong(username));
	    if (p.isApplication()) {
	        for (LoanApplicationViewModel l : list) {
	            if (l.getProcessedBy() == p.getUserId()) {
	                list1.add(l);
	            }
	        }
	        model.addAttribute("loanApplications", list1);
	        return "loan-approval";
	    } else {
	        return "not-permitted";
	    }
	}

	@RequestMapping(value = "/account", method = RequestMethod.POST)
	public String GetLoanAccounts(@RequestParam("Typevalue") String accountType, Model model, HttpServletRequest request, HttpServletResponse response) {
	    LOGGER.info("Handling POST request for /account");
	    List<LoanAccountViewModel> list = v.getLoanAccountsByLoanType(accountType);
	    HttpSession session = request.getSession();
	    String username = (String) session.getAttribute("username");
	    Permission p = permissionsDAO.getPermissions(Long.parseLong(username));
	    if (p.isLoans()) {
	        model.addAttribute("loanAccounts", list);
	        return "loan-account-details";
	    } else {
	        return "not-permitted";
	    }
	}

	@RequestMapping(value = "/deleteLoan", method = RequestMethod.POST)
	public String deleteLoanApplication(@RequestParam("loanId") int loanId, Model model) {
	    try {
	        LOGGER.info("Handling POST request for /deleteLoan");
	        ll.deleteApplication(loanId);
	        return "loan-approval";
	    } catch (Exception e) {
	        LOGGER.error("An error occurred while deleting the loan application", e);
	        model.addAttribute("error", "An error occurred while deleting the loan application.");
	        return "error-view";
	    }
	}

	@RequestMapping(value = "/approveLoan", method = RequestMethod.POST)
	public String approveLoanApplication(@RequestParam("loanId") int loanId, @RequestParam("customerId") Long customerId, Model model, HttpServletRequest request) {
	    try {
	        LOGGER.info("Handling POST request for /approveLoan");
	        ll.approveApplication(loanId, customerId);
	        LOGGER.debug("Approved loan application with loanId: {}", loanId);
	        LoanApplication loanapp = ll.getLoanApplicationByid(loanId);
	        LOGGER.debug("Acquired loan id: {}", loanapp.getId());
	        loanapp.setProcessedStatus("Approved");
	        loanapp.setStatus("Approved");
	        ll.saveTheApprovedLoanApplication(loanapp);
	        HttpSession session = request.getSession();
	        String username = (String) session.getAttribute("username");
	        applicationEventPublisher.publishEvent(new LoanAppApprovalEvent("Loan Application Approved", username));
	        return "loan-approval";
	    } catch (Exception e) {
	        LOGGER.error("An error occurred while approving the loan application", e);
	        model.addAttribute("error", "An error occurred while approving the loan application.");
	        return "error-view";
	    }
	}

	@RequestMapping(value = "/getLoanApplicationsById", method = RequestMethod.POST)
	public String getLoanApplicationById(@RequestParam("Data") int accountType, Model model) {
	    try {
	        LOGGER.info("Handling POST request for /getLoanApplicationsById");
	        LoanApplicationViewModel list = v.getLoanApplicationById(accountType);
	        List<LoanApplicationViewModel> list1 = new ArrayList<>();
	        list1.add(list);
	        model.addAttribute("loanApplications", list1);
	        return "loan-approval";
	    } catch (ApplicationNotFound e) {
	        LOGGER.error("Loan application not found for id: {}", accountType);
	        return "AccountNotFound";
	    }
	}

	@RequestMapping(value = "/getLoanAccountById", method = RequestMethod.POST)
	public String getAccountById(@RequestParam("Data") int accountType, Model model) {
	    try {
	        LOGGER.info("Handling POST request for /getLoanAccountById");
	        LoanAccountViewModel l = v.getLoanAccountById(accountType);
	        List<LoanAccountViewModel> list = new ArrayList<>();
	        list.add(l);
	        model.addAttribute("loanAccounts", list);
	        return "loan-account-details";
	    } catch (AccountNotFound e) {
	        LOGGER.error("Loan account not found for id: {}", accountType);
	        return "AccountNotFound";
	    }
	}
}