package com.nkxgen.spring.jdbc.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.nkxgen.spring.jdbc.Bal.Accounts;
import com.nkxgen.spring.jdbc.Bal.ViewInterface;
import com.nkxgen.spring.jdbc.DaoInterfaces.AccountTypeInterface;
import com.nkxgen.spring.jdbc.DaoInterfaces.LoanTypesInterface;
import com.nkxgen.spring.jdbc.InputModels.AccountTypeInput;
import com.nkxgen.spring.jdbc.InputModels.LoanTypeInput;
import com.nkxgen.spring.jdbc.ViewModels.AccountTypeView;
import com.nkxgen.spring.jdbc.ViewModels.LoanViewModel;
import com.nkxgen.spring.jdbc.model.LoansTypes;
import com.nkxgen.spring.jdbc.model.accountTypes;
import com.nkxgen.spring.jdbc.model.cashChest;

@Controller
public class masterController {

	@Autowired
	private AccountTypeInterface account;
	@Autowired
	ViewInterface v;
	@Autowired
	private Accounts ac1;
	@Autowired
	private LoanTypesInterface loan;
	
	Logger LOGGER = LoggerFactory.getLogger(LoginController.class);


	@RequestMapping(value = "/accountDataSave", method = RequestMethod.POST)
	public String accountApplicationSave(AccountTypeInput accountTypes, Model model) {
	    LOGGER.info("Handling POST request for /accountDataSave");
	    accountTypes at = new accountTypes();
	    at.setInputModelValues(accountTypes);
	    account.saveAccountTypes(at);
	    return "account-master-entry";
	}

	@RequestMapping(value = "/getAccountTypes", method = RequestMethod.GET)
	public String getAccounts(Model model) {
	    LOGGER.info("Handling GET request for /getAccountTypes");
	    List<AccountTypeView> list = v.set11();
	    for (AccountTypeView ll : list) {
	        LOGGER.info("Account Type: {}", ll.getAccountType());
	    }
	    model.addAttribute("accountTypes", list);
	    return "get-accounts";
	}

	@RequestMapping(value = "/getSelectedAccountDetails", method = RequestMethod.GET)
	public String getSelectedAccountDetails(@RequestParam("accountType") int accountType, Model model) {
	    LOGGER.info("Handling GET request for /getSelectedAccountDetails");
	    AccountTypeView accountT = v.getSelectedAccountDetails(accountType);
	    LOGGER.info("Account Type: {}", accountT.getAccountType());
	    LOGGER.info("Description Form: {}", accountT.getDescriptionForm());
	    model.addAttribute("accounts", accountT);
	    return "account-details";
	}

	@RequestMapping(value = "/loanDataSave", method = RequestMethod.POST)
	public String loanInfoSaveToDb(LoanTypeInput loans, Model model) {
	    LOGGER.info("Handling POST request for /loanDataSave");
	    LoansTypes lt = new LoansTypes();
	    lt.setInputModelValues(loans);
	    LOGGER.info("Hello");
	    loan.save(lt);
	    return "loan-master-entry";
	}

	@RequestMapping(value = "/getLoanTypes", method = RequestMethod.GET)
	public String getLoans(Model model) {
	    LOGGER.info("Handling GET request for /getLoanTypes");
	    List<LoanViewModel> list = v.getAllLoans();
	    for (LoanViewModel ll : list) {
	        LOGGER.info("Loan Type: {}", ll.getLoanType());
	    }
	    model.addAttribute("loans", list);
	    return "get-loans";
	}

	@RequestMapping(value = "/getSelectedLoanDetails", method = RequestMethod.GET)
	public String getSelectedLoanDetails(@RequestParam("loanType") int loanType, Model model) {
	    LOGGER.info("Handling GET request for /getSelectedLoanDetails");
	    LOGGER.info("The value is: {}", loanType);
	    LoanViewModel loank = v.getSelectedLoanDetails(loanType);
	    LOGGER.info("The loan ID is: {}", loank.getLoanId());
	    model.addAttribute("loans", loank);
	    return "loan-details";
	}

	@RequestMapping("/masterLoan")
	public String LoanMasterEntry(Model model) {
	    LOGGER.info("Handling request for /masterLoan");
	    return "loan_master_entry";
	}

	@RequestMapping("/masterAccount")
	public String masterAccount(Model model) {
	    LOGGER.info("Handling request for /masterAccount");
	    return "account-master-entry";
	}

	@RequestMapping(value = "/cashChest", method = RequestMethod.GET)
	public String money(Model model) {
	    LOGGER.info("Handling GET request for /cashChest");
	    cashChest cashchest = account.getallamount();
	    ac1.setcashChest(cashchest);
	    model.addAttribute("cashChest", cashchest);
	    return "cash-chest";
	}

	@RequestMapping(value = "profitLoss", method = RequestMethod.GET)
	public String Profitloss(Model model) {
	    LOGGER.info("Handling GET request for /profitLoss");
	    cashChest cashchest = account.getallamount();
	    ac1.setcashChest(cashchest);
	    model.addAttribute("cashChest", cashchest);
	    return "profitLoss";
	}
}