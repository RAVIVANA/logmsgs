package com.nkxgen.spring.jdbc.Dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.nkxgen.spring.jdbc.Bal.CustomerSetter;
import com.nkxgen.spring.jdbc.DaoInterfaces.AccountTypeInterface;
import com.nkxgen.spring.jdbc.DaoInterfaces.LoanApplicationDaoInterface;
import com.nkxgen.spring.jdbc.controller.LoginController;
import com.nkxgen.spring.jdbc.model.LoanAccount;
import com.nkxgen.spring.jdbc.model.accountTypes;
import com.nkxgen.spring.jdbc.model.cashChest;

@Repository
@Transactional
public class AccountTypesDAO implements AccountTypeInterface {
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private CustomerSetter s;

	@Autowired
	LoanApplicationDaoInterface ll;
	
	Logger LOGGER = LoggerFactory.getLogger(LoginController.class);


	// private long laonrepayamount = 0;
	@Override
	 public List<accountTypes> getAllAccounts() {
	        LOGGER.info("Entering getAllAccounts method");

	        String jpql = "SELECT l FROM accountTypes l"; // Define a JPQL query to retrieve all account types
	        TypedQuery<accountTypes> query = entityManager.createQuery(jpql, accountTypes.class); // Create a typed query with the JPQL query and the accountTypes entity class

	        List<accountTypes> accountTypesList = query.getResultList(); // Execute the query and retrieve the list of account types

	        LOGGER.info("Exiting getAllAccounts method");
	        return accountTypesList;
	    }
   
		@Override
	    public List<accountTypes> getAllAccountDetails() {
	        LOGGER.info("Entering getAllAccountDetails method");

	        String jpql = "SELECT l FROM accountTypes l"; // Define a JPQL query to retrieve all account types
	        TypedQuery<accountTypes> query = entityManager.createQuery(jpql, accountTypes.class); // Create a typed query with the JPQL query and the accountTypes entity class

	        List<accountTypes> accountTypesList = query.getResultList(); // Execute the query and retrieve the list of account types

	        LOGGER.info("Exiting getAllAccountDetails method");
	        return accountTypesList;
	    }

		@Override
	    public accountTypes getSelectedAccountDetails(int accountType) {
	        LOGGER.info("Entering getSelectedAccountDetails method");

	        accountTypes account = entityManager.find(accountTypes.class, accountType); // Find the account type by its ID

	        LOGGER.info("im in the dao of account types");
	        LOGGER.info("Account type: {}", account.getAccountType()); // Log the account type

	        LOGGER.info("Exiting getSelectedAccountDetails method");
	        return account; // Return the account type
	    }

		@Override
		public void saveAccountTypes(accountTypes accountType) {
	        LOGGER.info("Entering saveAccountTypes method");

	        if (accountType.getAccountType() != null && accountType.getDescriptionForm() != null) {
	            entityManager.merge(accountType); // Merge the account type entity with the persistence context
	        } else {
	            // Handle the case where either accountType or descriptionForm is null
	            // You can throw an exception, log an error, or perform any appropriate action
	            LOGGER.error("Invalid account type data. Account type or description form is null.");
	        }

	        LOGGER.info("Exiting saveAccountTypes method");
	    }

	    @Override
	    public cashChest getallamount() {
	        LOGGER.info("Entering getallamount method");

	        // Retrieve the total balance from all accounts
	        String query = "SELECT SUM(a.balance) FROM Account a";
	        Query q = entityManager.createQuery(query);
	        Long totalBalance = (Long) q.getSingleResult();
	        LOGGER.info("Total amount: {}", totalBalance);

	        // Calculate the total loan amount minus deductions
	        String jpql = "SELECT SUM(t.loanAmount - t.deductionAmt) FROM LoanAccount t";
	        Long result = entityManager.createQuery(jpql, Long.class).getSingleResult();
	        LOGGER.info("Total result amount: {}", result);

	        // Calculate the total amount started
	        long started = totalBalance + result;
	        LOGGER.info("Started amount: {}", started);

	        // Retrieve all loans
	        List<LoanAccount> loan = ll.getAllLoans();

	        // Calculate the total difference in loan payments
	        long totalDifference = 0L;
	        for (LoanAccount account : loan) {
	            // Calculate the monthly payment value
	            long value1 = (account.getLoanAmount() / (account.getLoanDuration() * 12));
	            LOGGER.info("Value1: {}", value1);

	            // Calculate the number of payments made
	            long value2 = (account.getdeductionAmt() / value1);
	            LOGGER.info("Value2: {}", value2);

	            int numberofpayed = (int) ((account.getLoanDuration() * 12) - value2);
	            LOGGER.info("Number of payments made: {}", numberofpayed);

	            // Calculate the final value of the loan
	            long finalvalue = (long) ((numberofpayed)
	                    * (s.calinterest(account.getLoanAmount(), account.getdeductionAmt(), account.getLoanDuration(),
	                            account.getInterestRate(), account.getLoanType())));
	            LOGGER.info("Final value of the loan: {}", finalvalue);

	            // Update the total difference
	            totalDifference += finalvalue;
	            LOGGER.info("Total amount of this loan: {}", totalDifference);
	        }
	        LOGGER.info("Final amount: {}", totalDifference);

	        // Calculate the main amount by summing the total balance, result, and total difference
	        Long mainamount = totalBalance + result + totalDifference;
	        LOGGER.info("Main amount: {}", mainamount);

	        // Create a cashChest object with the calculated values
	        cashChest c = s.setcashchest(mainamount, totalDifference, started);

	        LOGGER.info("Exiting getallamount method");
	        return c;
	    }
	







	// @Override
	// public void setTheLoanRepay(tempRepayment tarn, LoanAccount Loan) {
	// laonrepayamount += (tarn.getAmount() - (long) (Loan.getLoanAmount() / Loan.getLoanDuration()));
	// }

}