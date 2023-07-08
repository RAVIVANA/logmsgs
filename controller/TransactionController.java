package com.nkxgen.spring.jdbc.controller;

import javax.security.auth.login.AccountNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.nkxgen.spring.jdbc.Bal.CustomerSetter;
import com.nkxgen.spring.jdbc.Dao.PermissionsDAO;
import com.nkxgen.spring.jdbc.DaoInterfaces.TransactionsInterface;
import com.nkxgen.spring.jdbc.Exception.InsufficientBalanceException;
import com.nkxgen.spring.jdbc.Exception.InvalidLoanRepaymentException;
import com.nkxgen.spring.jdbc.Exception.LoanAccountNotFoundException;
import com.nkxgen.spring.jdbc.Exception.TransactionSaveException;
import com.nkxgen.spring.jdbc.Exception.TransactionWithdrawlSaveException;
import com.nkxgen.spring.jdbc.events.TransactionEvent;
import com.nkxgen.spring.jdbc.model.Account;
import com.nkxgen.spring.jdbc.model.Customertrail;
import com.nkxgen.spring.jdbc.model.EMIpay;
import com.nkxgen.spring.jdbc.model.LoanAccount;
import com.nkxgen.spring.jdbc.model.LoanTransactions;
import com.nkxgen.spring.jdbc.model.Permission;
import com.nkxgen.spring.jdbc.model.Transaction;
import com.nkxgen.spring.jdbc.model.tempRepayment;
import com.nkxgen.spring.jdbc.model.transactioninfo;;

@Controller
public class TransactionController {
	Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	TransactionsInterface ti;

	@Autowired
	ApplicationEventPublisher applicationEventPublisher;
	@Autowired
	private PermissionsDAO permissionsDAO;
	@Autowired
	private CustomerSetter s;

	// Mapping for money deposit form
	@RequestMapping(value = "/moneyDeposit", method = RequestMethod.GET)
	public String moneyDepositeForm(Model model, HttpServletRequest request, HttpServletResponse response) {
	    HttpSession session = request.getSession();

	    // Get the username attribute from the session
	    String username = (String) session.getAttribute("username");
	    LOGGER.info("Handling GET request for /moneyDeposit by user: {}", username);

	    Permission p = permissionsDAO.getPermissions(Long.parseLong(username));
	    if (p.isTransactions()) {
	        LOGGER.info("User {} has permission for transactions. Returning money-deposit view.", username);
	        return "money-deposit";
	    } else {
	        LOGGER.info("User {} does not have permission for transactions. Returning not-permitted view.", username);
	        return "not-permitted";
	    }
	}

	// Mapping for loan repayment form
	@RequestMapping(value = "/loanRepay", method = RequestMethod.GET)
	public String loanRepaymentForm(Model model, HttpServletRequest request, HttpServletResponse response) {
	    HttpSession session = request.getSession();

	    // Get the username attribute from the session
	    String username = (String) session.getAttribute("username");
	    LOGGER.info("Handling GET request for /loanRepay by user: {}", username);

	    Permission p = permissionsDAO.getPermissions(Long.parseLong(username));
	    if (p.isTransactions()) {
	        LOGGER.info("User {} has permission for transactions. Returning loan-repayment view.", username);
	        return "loan-repayment";
	    } else {
	        LOGGER.info("User {} does not have permission for transactions. Returning not-permitted view.", username);
	        return "not-permitted";
	    }
	}


	// Mapping for money withdrawal form
	@RequestMapping(value = "/withdrawl", method = RequestMethod.GET)
	public String moneyWithdrawlForm(Model model, HttpServletRequest request, HttpServletResponse response) {
	    HttpSession session = request.getSession();

	    // Get the username attribute from the session
	    String username = (String) session.getAttribute("username");
	    LOGGER.info("Handling GET request for /withdrawl by user: {}", username);

	    Permission p = permissionsDAO.getPermissions(Long.parseLong(username));
	    if (p.isTransactions()) {
	        LOGGER.info("User {} has permission for transactions. Returning money-withdrawl-form view.", username);
	        return "money-withdrawl-form";
	    } else {
	        LOGGER.info("User {} does not have permission for transactions. Returning not-permitted view.", username);
	        return "not-permitted";
	    }
	}


	// Mapping for loan withdrawal form
	@RequestMapping(value = "/lowid", method = RequestMethod.GET)
	public String loWithdrawlForm(Model model, HttpServletRequest request, HttpServletResponse response) {
	    HttpSession session = request.getSession();

	    // Get the username attribute from the session
	    String username = (String) session.getAttribute("username");
	    LOGGER.info("Handling GET request for /lowid by user: {}", username);

	    Permission p = permissionsDAO.getPermissions(Long.parseLong(username));
	    if (p.isTransactions()) {
	        LOGGER.info("User {} has permission for transactions. Returning loan-withdrawl-form view.", username);
	        return "loan-withdrawl-form";
	    } else {
	        LOGGER.info("User {} does not have permission for transactions. Returning not-permitted view.", username);
	        return "not-permitted";
	    }
	}

	// =================================================================================
	// money-deposit.html
	// Get account details for money deposit
	@RequestMapping(value = "/getAccountDetails", method = RequestMethod.POST)
	public String getAccountDetails(@RequestParam("accountNumber") int Acnt_id, Model model) {
	    try {
	        LOGGER.info("Getting account details for account number: {}", Acnt_id);
	        Account account = ti.getAccountById(Acnt_id); // Get the account details for the provided account number
	        model.addAttribute("account", account); // Add the account object to the model
	        LOGGER.info("Account details retrieved successfully for account number: {}", Acnt_id);
	        return "sub-money-deposit"; // Return the name of the view to be rendered
	    } catch (AccountNotFoundException e) {
	        // Handle the exception
	        LOGGER.error("Failed to retrieve account details: {}", e.getMessage());
	        model.addAttribute("error", "Failed to retrieve account details: " + e.getMessage());
	        return "error-page"; // Return the error page view
	    }
	}

	@RequestMapping(value = "/moneyDepositUrl")
	public ResponseEntity<String> getDepositMoney(@Validated transactioninfo tarn, Model model,
	        HttpServletRequest request) {
	    try {
	        LOGGER.info("Processing money deposit for transaction: {}", tarn);
	        ti.moneyDeposit(tarn); // Perform money deposit operation using the 'tarn' object
	        Transaction t = ti.transactionSave1(tarn); // Save the transaction details using the 'tarn' object
	        HttpSession session = request.getSession(); // Get the current session
	        String username = (String) session.getAttribute("username"); // Get the username from the session
	        // Publish a transaction event
	        LOGGER.info("Publishing transaction event for money deposited by user: {}", username);
	        applicationEventPublisher.publishEvent(new TransactionEvent("Money Deposited ", username));
	        ti.saveTransaction(t); // Save the transaction to the database

	        LOGGER.info("Money deposited successfully for transaction: {}", tarn);
	        return ResponseEntity.ok("Deposit successful"); // Return a response with a success message
	    } catch (Exception e) {
	        // Handle the exception
	        LOGGER.error("Failed to deposit money: {}", e.getMessage());
	        return ResponseEntity.status(500).body("Failed to deposit money: " + e.getMessage());
	    }
	}


	// =================================================================================
	// money-withdrawl-form.html
	// Get account details for money withdrawal
	@RequestMapping(value = "/getAccountDetailsMoneyWithdrawl", method = RequestMethod.POST)
	public String getAccountDetailsForMoneyWithdrawl(@RequestParam("accountNumber") int Acnt_id, Model model) {
	    try {
	        LOGGER.info("Getting account details for account number: {}", Acnt_id);
	        Account account = ti.getAccountById(Acnt_id); // Get the account details for the provided account number
	        model.addAttribute("account", account); // Add the account object to the model
	        LOGGER.info("Account details retrieved successfully for account number: {}", Acnt_id);
	        return "sub-money-withdrawl"; // Return the name of the view to be rendered
	    } catch (AccountNotFoundException e) {
	        // Handle the exception
	        LOGGER.error("Failed to retrieve account details: {}", e.getMessage());
	        model.addAttribute("error", "Failed to retrieve account details: " + e.getMessage());
	        return "error-page"; // Return the error page view
	    }
	}

	// sub-money-withdrawl.html
	// Process money withdrawal
	@RequestMapping(value = "/moneyWithDrawlUrl")
	public ResponseEntity<String> getMoneyWithdrawlAmount(@Validated transactioninfo tarn, Model model,
	        HttpServletRequest request) throws TransactionWithdrawlSaveException, TransactionSaveException {
	    try {
	        LOGGER.info("Performing money withdrawal based on transaction info: {}", tarn);
	        ti.moneyWithdrawl(tarn); // Perform money withdrawal based on the provided transaction info
	        Transaction t = ti.transactionSave(tarn); // Save the transaction
	        HttpSession session = request.getSession();
	        String username = (String) session.getAttribute("username"); // Retrieve the username from the session
	        // Publish a transaction event
	        LOGGER.info("Publishing transaction event for money withdrawal");
	        applicationEventPublisher.publishEvent(new TransactionEvent("Money Withdrawed ", username));
	        ti.saveTransaction(t); // Save the transaction to the database
	        LOGGER.info("Money withdrawal completed successfully");
	        // Return a response entity with "Money withdrawal Successfully" message
	        return ResponseEntity.ok("Money withdrawal Successfully");
	    } catch (InsufficientBalanceException e) {
	        // Handle the exception
	        LOGGER.error("Failed to withdraw money: {}", e.getMessage());
	        return ResponseEntity.status(500).body("Failed to withdraw money: " + e.getMessage());
	    }
	}


	// =====================================================================================================================
	// loan-withdrawl-form.html
	// Get loan account details for loan withdrawal
	@RequestMapping(value = "/getLoanDetails", method = RequestMethod.POST)
	public String getLoandetails(@RequestParam("accountNumber") long loan_id, Model model) {
	    try {
	        LOGGER.info("Retrieving loan details for loan ID: {}", loan_id);
	        long acnt_id = loan_id; // Assign the loan_id to the acnt_id variable
	        LoanAccount account = ti.getLoanAccountById(acnt_id); // Retrieve the LoanAccount object by its ID
	        // Retrieve the Customertrail object associated with the loan account
	        Customertrail customer = ti.getCustomerByLoanID(account.getCustomerId().getId());
	        LOGGER.info("Deduction amount for loan account {}: {}", loan_id, account.getdeductionAmt());
	        if (account.getdeductionAmt() == 0) { // Check if the deductionAmt property of the account is 0
	            model.addAttribute("account", account); // Add the account object to the model attribute
	            // Add the customer object to the model attribute with the name "customerss"
	            model.addAttribute("customerss", customer);
	            // Return the view name "sub_loan_withdrawl" to display the loan withdrawal form
	            return "sub-loan-withdrawl";
	        } else {
	            throw new Exception("Loan withdrawal is not allowed for this account");
	        }
	    } catch (Exception e) {
	        // Handle the exception
	        LOGGER.error("Failed to retrieve loan details: {}", e.getMessage());
	        model.addAttribute("error", "Failed to retrieve loan details: " + e.getMessage());
	        return "error-page"; // Return the error page view
	    }
	}


	// sub-loan-withdrawl.html
	// Process loan withdrawal
	@RequestMapping(value = "/loanWithdrawlUrl", method = RequestMethod.POST)
	public ResponseEntity<String> getLoanmoneyWithdrawlAmount(@Validated transactioninfo tarn, Model model,
	        HttpServletRequest request) {
	    try {
	        ti.loanWithdrawl(tarn.getAccountNumber()); // Perform the loan withdrawal operation based on the account number
	        tempRepayment temp = s.setthistarn(tarn); // Set temporary repayment information using the tarn object
	        // Create a LoanTransactions object based on the temprepayment
	        LoanTransactions t = ti.loanTransactionWithdrawl(temp);
	        HttpSession session = request.getSession(); // Get the HttpSession object from the request
	        String username = (String) session.getAttribute("username"); // Get the username from the session attribute
	        LOGGER.info("Withdrawing loan for user: {}", username);
	        // Publish a TransactionEvent for loan withdrawal
	        applicationEventPublisher.publishEvent(new TransactionEvent("Loan Withdrawed ", username));
	        ti.saveLoanTransaction(t); // Save the LoanTransactions object
	        LOGGER.info("Loan withdrawal successful");
	        return ResponseEntity.ok("Loan withdrawal Successfully"); // Return a ResponseEntity with a success message
	    } catch (Exception e) {
	        // Handle the exception
	        LOGGER.error("Failed to withdraw loan: {}", e.getMessage());
	        return ResponseEntity.status(500).body("Failed to withdraw loan: " + e.getMessage());
	    }
	}

	// =====================================================================================================================
	// loan_repayment
	// Get loan account details for loan repayment
	@RequestMapping(value = "/getLoanRepaytDetails", method = RequestMethod.POST)
	public String getloanrepaytdetails(@RequestParam("accountNumber") long loan_id, Model model) {
	    try {
	        LoanAccount account = ti.getLoanAccountById(loan_id); // Retrieve the LoanAccount object based on the loan_id

	        if (account.getdeductionAmt() != 0) { // Check if the deduction amount is not zero
	            EMIpay emiobj = ti.changeToEMI(account); // Convert the account to EMIpay object
	            model.addAttribute("account", emiobj); // Add the emiobj to the model attribute
	            LOGGER.info("Loan repayment details retrieved successfully");
	            return "sub-loan-repayment.html"; // Return the view for loan repayment
	        } else {
	            throw new Exception("Loan repayment is not allowed for this account");
	        }
	    } catch (LoanAccountNotFoundException e) {
	        // Handle the exception
	        LOGGER.error("Failed to retrieve loan details: {}", e.getMessage());
	        model.addAttribute("error", "Failed to retrieve loan details: " + e.getMessage());
	        return "error-page"; // Return the error page view
	    } catch (Exception e) {
	        // Handle the exception
	        LOGGER.error("Failed to retrieve loan details: {}", e.getMessage());
	        model.addAttribute("error", "Failed to retrieve loan details: " + e.getMessage());
	        return "error-page"; // Return the error page view
	    }
	}

	// sub_loan_repayment
	// Process loan repayment
	@RequestMapping(value = "/loanRepaymentUrl")
	public ResponseEntity<String> getloanrepaymenAmount(@Validated tempRepayment tarn, Model model,
	        HttpServletRequest request) {
	    try {
	        ti.loanRepayment(tarn); // Perform the loan repayment based on the tempRepayment object
	        // Create a LoanTransactions object based on the tempRepayment object
	        LoanTransactions t = ti.loanTransactionRepay(tarn);
	        ti.saveLoanTransaction(t); // Save the LoanTransactions object

	        HttpSession session = request.getSession();
	        String username = (String) session.getAttribute("username");
	        LOGGER.info("Loan repaid successfully for user: {}", username);
	        // Publish a Loan repaid event
	        applicationEventPublisher.publishEvent(new TransactionEvent("Loan repayed ", username));
	        // Return a response entity indicating successful loan repayment
	        return ResponseEntity.ok("Loan Repayed Successfully ");
	    } catch (InvalidLoanRepaymentException e) {
	        // Handle the exception
	        LOGGER.error("Failed to repay loan: {}", e.getMessage());
	        return ResponseEntity.status(500).body("Failed to repay loan: " + e.getMessage());
	    } catch (Exception e) {
	        // Handle the exception
	        LOGGER.error("Failed to repay loan: {}", e.getMessage());
	        return ResponseEntity.status(500).body("Failed to repay loan: " + e.getMessage());
	    }
	}


}