package com.nkxgen.spring.jdbc.Dao;

import java.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.auth.login.AccountNotFoundException;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.nkxgen.spring.jdbc.Bal.CustomerSetter;
import com.nkxgen.spring.jdbc.DaoInterfaces.CustomerDaoInterface;
import com.nkxgen.spring.jdbc.DaoInterfaces.TransactionsInterface;
import com.nkxgen.spring.jdbc.Exception.CustomerNotFoundException;
import com.nkxgen.spring.jdbc.Exception.EMIpayConversionException;
import com.nkxgen.spring.jdbc.Exception.InsufficientBalanceException;
import com.nkxgen.spring.jdbc.Exception.InvalidLoanRepaymentException;
import com.nkxgen.spring.jdbc.Exception.LoanAccountApplicationNotFoundException;
import com.nkxgen.spring.jdbc.Exception.LoanAccountNotFoundException;
import com.nkxgen.spring.jdbc.Exception.LoanTransactionRepayException;
import com.nkxgen.spring.jdbc.Exception.LoanTransactionSaveException;
import com.nkxgen.spring.jdbc.Exception.LoanTransactionWithdrawlException;
import com.nkxgen.spring.jdbc.Exception.LoanWithdrawalException;
import com.nkxgen.spring.jdbc.Exception.TransactionDepositSaveException;
import com.nkxgen.spring.jdbc.Exception.TransactionSaveException;
import com.nkxgen.spring.jdbc.Exception.TransactionWithdrawlSaveException;
import com.nkxgen.spring.jdbc.controller.LoginController;
import com.nkxgen.spring.jdbc.model.Account;
import com.nkxgen.spring.jdbc.model.Customertrail;
import com.nkxgen.spring.jdbc.model.EMIpay;
import com.nkxgen.spring.jdbc.model.LoanAccount;
import com.nkxgen.spring.jdbc.model.LoanApplication;
import com.nkxgen.spring.jdbc.model.LoanTransactions;
import com.nkxgen.spring.jdbc.model.Transaction;
import com.nkxgen.spring.jdbc.model.tempRepayment;
import com.nkxgen.spring.jdbc.model.transactioninfo;

@Repository
@Transactional
public class TransactionsDAO implements TransactionsInterface {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private CustomerSetter s;
	@Autowired
	private TransactionsInterface ti;

	@Autowired
	private CustomerDaoInterface cd;
	
	Logger LOGGER = LoggerFactory.getLogger(LoginController.class);


	@Override
	public void moneyDeposit(transactioninfo trans) throws AccountNotFoundException {
	    // Find the account object with the given account number using the entity manager
	    LOGGER.info("Finding account with account number: {}", trans.getAccountNumber());
	    Account account = entityManager.find(Account.class, (long) trans.getAccountNumber());

	    if (account == null) {
	        String errorMessage = "Account not found with ID: " + trans.getAccountNumber();
	        LOGGER.error(errorMessage);
	        throw new AccountNotFoundException(errorMessage);
	    }

	    // Calculate the new balance by adding the deposit amount to the current balance
	    long balance = (long) (account.getBalance() + trans.getAmount());

	    LOGGER.info("Updating balance for account with account number: {}. Old balance: {}. Deposit amount: {}. New balance: {}",
	            trans.getAccountNumber(), account.getBalance(), trans.getAmount(), balance);

	    account.setBal(balance); // Update the balance of the account with the new balance
	}

	@Override

public void moneyWithdrawl(transactioninfo trans) throws InsufficientBalanceException {
    // Find the account object with the given account number using the entity manager
    LOGGER.info("Finding account with account number: {}", trans.getAccountNumber());
    Account account = entityManager.find(Account.class, (long) trans.getAccountNumber());

    // Check if the account balance is sufficient for the withdrawal
    if (account.getBalance() >= (long) trans.getAmount()) {
        // Calculate the new balance by subtracting the withdrawal amount from the current balance
        long balance = (long) (account.getBalance() - (long) trans.getAmount());

        LOGGER.info("Updating balance for account with account number: {}. Old balance: {}. Withdrawal amount: {}. New balance: {}",
                trans.getAccountNumber(), account.getBalance(), trans.getAmount(), balance);

        account.setBal(balance); // Update the balance of the account with the new balance
    } else {
        String errorMessage = "Insufficient balance for withdrawal";
        LOGGER.error(errorMessage);
        // Throw an InsufficientBalanceException indicating insufficient balance for the withdrawal
        throw new InsufficientBalanceException(errorMessage);
    }
}

	@Override
	public Account getAccountById(int id) throws AccountNotFoundException {
	    // Find the account object with the given ID using the entity manager
	    LOGGER.info("Finding account with ID: {}", id);
	    Account account = entityManager.find(Account.class, (long) id);
	    if (account == null) {
	        String errorMessage = "Account not found with ID: " + id;
	        LOGGER.error(errorMessage);
	        throw new AccountNotFoundException(errorMessage);
	    }
	    LOGGER.info("Retrieved account with ID: {}", id);
	    return account; // Return the found account object
	}

	@Override
	public Transaction transactionSave(transactioninfo tarn) throws TransactionWithdrawlSaveException {
	    try {
	        LOGGER.info("Saving transaction with transaction info: {}", tarn.toString());
	        Transaction t = s.transactionSet(tarn);
	        t.setTran_mode(tarn.getMode());
	        t.setTran_type("Withdrawl"); // Set the transaction type to "Withdrawl"

	        LOGGER.info("Transaction saved successfully");
	        return t; // Return the created Transaction object
	    } catch (Exception e) {
	        String errorMessage = "Failed to save transaction";
	        LOGGER.error(errorMessage, e);
	        throw new TransactionWithdrawlSaveException(errorMessage);
	    }
	}

	@Override
	public void saveTransaction(Transaction transaction) throws TransactionSaveException {
	    // Persist the provided Transaction object by adding it to the entity manager, allowing it to be saved in the data store
	    try {
	        LOGGER.info("Saving transaction: {}", transaction.toString());
	        entityManager.persist(transaction);
	        LOGGER.info("Transaction saved successfully");
	    } catch (Exception e) {
	        String errorMessage = "Failed to save transaction";
	        LOGGER.error(errorMessage, e);
	        throw new TransactionSaveException(errorMessage);
	    }
	}

	@Override
	// save deposit transactions
	public Transaction transactionSave1(transactioninfo tarn) throws TransactionDepositSaveException {
	    // Create a new Transaction object by calling a method 'transactionSet' from another class or service, passing a transactioninfo object
	    try {
	        LOGGER.info("Saving transaction with transaction info: {}", tarn.toString());
	        Transaction t = s.transactionSet(tarn);
	        t.setTran_type("deposit"); // Set the transaction type to "deposit"
	        
	        LOGGER.info("Transaction saved successfully");
	        return t; // Return the created Transaction object
	    } catch (Exception e) {
	        String errorMessage = "Failed to save transaction";
	        LOGGER.error(errorMessage, e);
	        throw new TransactionDepositSaveException(errorMessage);
	    }
	}

	// =================================================================
	@Override
	public LoanAccount getLoanAccountById(long id) throws LoanAccountNotFoundException {
	    // Find the loan account object with the given ID using the entity manager
	    LOGGER.info("Finding loan account with ID: {}", id);
	    LoanAccount account = entityManager.find(LoanAccount.class, id);
	    if (account == null) {
	        String errorMessage = "Loan account not found with ID: " + id;
	        LOGGER.error(errorMessage);
	        throw new LoanAccountNotFoundException(errorMessage);
	    }
	    LOGGER.info("Retrieved loan account with ID: {}", id);
	    return account; // Return the found loan account object
	}

	// =================================================================
	@Override
	public LoanApplication getLoanAccountApplicationById(long id) throws LoanAccountApplicationNotFoundException {
	    // Find the loan application object with the given ID using the entity manager
	    LOGGER.info("Finding loan application with ID: {}", id);
	    LoanApplication account = entityManager.find(LoanApplication.class, (long) id);
	    if (account == null) {
	        String errorMessage = "Loan account not found with ID: " + id;
	        LOGGER.error(errorMessage);
	        throw new LoanAccountApplicationNotFoundException(errorMessage);
	    }
	    LOGGER.info("Retrieved loan application with ID: {}", id);
	    return account; // Return the found loan account object
	}
	
	// =================================================================

	@Override
	public void loanWithdrawl(long id) throws LoanWithdrawalException {
	    // Find the loan account object with the given ID using the entity manager
	    LOGGER.info("Finding loan account with ID: {}", id);
	    LoanAccount account = entityManager.find(LoanAccount.class, id);
	    if (account.getdeductionAmt() == 0) {
	        LOGGER.info("Setting deduction amount for loan account with ID: {}", id);
	        account.setdeductionAmt(account.getLoanAmount()); // Set the deduction amount to the loan amount
	        LOGGER.info("Loan withdrawal successful for loan account with ID: {}", id);
	    } else {
	        String errorMessage = "Loan withdrawal has already been made";
	        LOGGER.error(errorMessage);
	        throw new LoanWithdrawalException(errorMessage);
	    }
	}

	@Override
	public void loanRepayment(tempRepayment trans) throws InvalidLoanRepaymentException {
	    // Find the loan account object with the given loan ID using the entity manager
	    LOGGER.info("Finding loan account with loan ID: {}", trans.getLoanid());
	    LoanAccount account = entityManager.find(LoanAccount.class, (long) trans.getLoanid());

	    int x = (int) Math.ceil(trans.getEMI()); // Calculate the EMI value and round it up to the nearest integer
	    if (trans.getAmount() == trans.getTotal()) { // If the repayment amount is equal to the total amount
	        LOGGER.info("Updating due balance for loan account with loan ID: {}. Old balance: {}. EMI amount: {}. New balance: {}",
	                trans.getLoanid(), account.getdeductionAmt(), x, account.getdeductionAmt() - x);
	        account.setdeductionAmt(account.getdeductionAmt() - x); // Update the due balance by subtracting the EMI amount
	    } else if (trans.getAmount() == trans.getComplete()) { // If the repayment amount is equal to the complete amount
	        LOGGER.info("Setting due balance to 0 for loan account with loan ID: {}", trans.getLoanid());
	        account.setdeductionAmt(0); // Set the due balance to 0
	    } else if (trans.getAmount() == trans.getPastdue()) {
	        double y = (double) trans.getPastdue() / trans.getTotal();
	        double z = y * trans.getEMI();
	        int xy = (int) z;
	        LOGGER.info("Updating due balance for loan account with loan ID: {}. Old balance: {}. Repayment amount: {}. New balance: {}",
	                trans.getLoanid(), account.getdeductionAmt(), xy, account.getdeductionAmt() - xy);
	        account.setdeductionAmt(account.getdeductionAmt() - xy);
	    } else {
	        String errorMessage = "Invalid loan repayment amount";
	        LOGGER.error(errorMessage);
	        throw new InvalidLoanRepaymentException(errorMessage);
	    }
	}
	
	@Override
	public Customertrail getCustomerByLoanID(Long loanId) throws CustomerNotFoundException {
	    LOGGER.info("Retrieving customer trail for loan ID: {}", loanId);
	    Customertrail t = cd.getCustomerById(loanId); // Retrieve the customer trail object with the given loan ID using a method 'getCustomerById' from another class or service
	    if (t == null) {
	        String errorMessage = "Customer trail not found for loan ID: " + loanId;
	        LOGGER.error(errorMessage);
	        throw new CustomerNotFoundException(errorMessage);
	    }
	    LOGGER.info("Retrieved customer trail for loan ID: {}", loanId);
	    return t; // Return the retrieved customer trail object
	}

	@Override
	public EMIpay changeToEMI(LoanAccount account) throws EMIpayConversionException {
	    LOGGER.info("Converting LoanAccount to EMIpay");
	    EMIpay obj = s.changeToEmiObj(account, ti); // Create a new EMIpay object by calling a method 'changeToEmiObj' from another class or service, passing the loan account object
	    if (obj == null) {
	        String errorMessage = "Failed to convert LoanAccount to EMIpay";
	        LOGGER.error(errorMessage);
	        throw new EMIpayConversionException(errorMessage);
	    }
	    LOGGER.info("LoanAccount converted to EMIpay successfully");
	    return obj; // Return the created EMIpay object
	}

	@Override
	public LoanTransactions loanTransactionRepay(tempRepayment lt) throws LoanTransactionRepayException {
	    try {
	        LOGGER.info("Performing loan transaction repayment");
	        LoanTransactions t = s.loantransactionSet(lt); // Create a new LoanTransactions object by calling a method 'loantransactionSet' from another class or service
	        t.setDate(LocalDate.now().toString()); // Set the date of the loan transaction to the current date
	        t.setType("emi pay"); // Set the type of the loan transaction to "emi pay"
	        
	        LOGGER.info("Loan transaction repayment performed successfully");
	        return t; // Return the created LoanTransactions object
	    } catch (Exception e) {
	        String errorMessage = "Failed to perform loan transaction repayment";
	        LOGGER.error(errorMessage, e);
	        throw new LoanTransactionRepayException(errorMessage);
	    }
	}

	@Override
	public void saveLoanTransaction(LoanTransactions lt) throws LoanTransactionSaveException {
	    try {
	        LOGGER.info("Saving LoanTransactions: {}", lt.toString());
	        entityManager.persist(lt); // Persist the provided LoanTransactions object by adding it to the entity manager
	        LOGGER.info("LoanTransactions saved successfully");
	    } catch (Exception e) {
	        String errorMessage = "Failed to save LoanTransactions";
	        LOGGER.error(errorMessage, e);
	        throw new LoanTransactionSaveException(errorMessage);
	    }
	}

	@Override
	public LoanTransactions loanTransactionWithdrawl(tempRepayment temp) throws LoanTransactionWithdrawlException {
	    try {
	        LOGGER.info("Performing loan transaction withdrawal");
	        LoanTransactions t = s.loantransactionSet(temp); // Create a new LoanTransactions object by calling a method 'loantransactionSet' from another class or service
	        t.setDate(LocalDate.now().toString()); // Set the date of the loan transaction to the current date
	        t.setType("loan withdrawl"); // Set the type of the loan transaction to "loan withdrawl"
	        
	        LOGGER.info("Loan transaction withdrawal performed successfully");
	        return t; // Return the created LoanTransactions object
	    } catch (Exception e) {
	        String errorMessage = "Failed to perform loan transaction withdrawal";
	        LOGGER.error(errorMessage, e);
	        throw new LoanTransactionWithdrawlException(errorMessage);
	    }
	}

}