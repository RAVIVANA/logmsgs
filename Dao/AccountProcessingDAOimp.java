package com.nkxgen.spring.jdbc.Dao;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nkxgen.spring.jdbc.DaoInterfaces.AccountProcessingDAO;
import com.nkxgen.spring.jdbc.controller.LoginController;
import com.nkxgen.spring.jdbc.model.Account;
import com.nkxgen.spring.jdbc.model.LoanTransactions;
import com.nkxgen.spring.jdbc.model.Transaction;

// ...

@Repository
@Transactional
public class AccountProcessingDAOimp implements AccountProcessingDAO {

	@PersistenceContext
	public EntityManager em;
	
	Logger LOGGER = LoggerFactory.getLogger(LoginController.class);


	@Transactional // Transactional annotation to indicate the method should be executed within a transaction
    public List<Account> getSavAcc(List<Account> acctype) {
        LOGGER.info("Entering getSavAcc method");

        for (Account i : acctype) {
            long acc = i.getApplicationId(); // Retrieve the ApplicationId from each Account object
            String sql = "CALL smi(:acc)"; // SQL statement to call the stored procedure
            Query query1 = em.createNativeQuery(sql); // Create a native SQL query
            query1.setParameter("acc", acc); // Set the "acc" parameter with the retrieved ApplicationId
            query1.executeUpdate(); // Execute the native SQL query to call the stored procedure
        }

        LocalDate currentDate = LocalDate.now(); // Get the current date
        String dateString1 = currentDate.toString(); // Convert the current date to a string

        for (Account a : acctype) {
            a.setLastUpdate(dateString1); // Set the lastUpdate field of each Account object to the current date string
        }

        LOGGER.info("Exiting getSavAcc method");
        return acctype; // Return the modified acctype list
    }

	@Override
	@Transactional // Transactional annotation to indicate the method should be executed within a transaction
	public void executeProcedure(int acc) {
        LOGGER.info("Executing procedure"); // Log a message indicating that the procedure is being executed
        LOGGER.info("acc: {}", acc); // Log the value of the acc parameter

        StoredProcedureQuery query = em.createStoredProcedureQuery("smi"); // Create a stored procedure query with the name "smi"
        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN); // Register the first parameter of the stored procedure as an input parameter
        query.setParameter(1, acc); // Set the value of the input parameter to the acc value

        query.execute(); // Execute the stored procedure

        LOGGER.info("Procedure execution completed");
    }
	@Override
	  public List<Transaction> AccountTransactionStatementGeneration(int accno) {
        LOGGER.info("Entering AccountTransactionStatementGeneration method");

        LocalDateTime endDate = LocalDateTime.now(); // Get the current date and time
        Timestamp tran_date1 = Timestamp.valueOf(endDate); // Convert the LocalDateTime to a Timestamp
        String tran_date = tran_date1.toString(); // Convert the Timestamp to a string

        String jpql = "SELECT bt FROM Transaction bt WHERE bt.tran_anct_id = :accno"; // Define a JPQL query to retrieve transactions for a specific account number
        TypedQuery<Transaction> query = em.createQuery(jpql, Transaction.class); // Create a typed query with the JPQL query and the Transaction entity class
        query.setParameter("accno", accno); // Set the value of the accno parameter in the JPQL query

        List<Transaction> transactionList = query.getResultList(); // Execute the query and retrieve the list of transactions

        LOGGER.info("Exiting AccountTransactionStatementGeneration method");
        return transactionList;
    }

	@Override

    public List<LoanTransactions> LoanTransactionStatementGeneration(int accno) {
        LOGGER.info("Entering LoanTransactionStatementGeneration method");

        LocalDateTime endDate = LocalDateTime.now(); // Get the current date and time
        Timestamp tran_date1 = Timestamp.valueOf(endDate); // Convert the LocalDateTime to a Timestamp
        String tran_date = tran_date1.toString(); // Convert the Timestamp to a string

        String jpql = "SELECT bt FROM LoanTransactions bt WHERE bt.loanId = :accno"; // Define a JPQL query to retrieve loan transactions for a specific loan ID
        TypedQuery<LoanTransactions> query = em.createQuery(jpql, LoanTransactions.class); // Create a typed query with the JPQL query and the LoanTransactions entity class
        query.setParameter("accno", accno); // Set the value of the accno parameter in the JPQL query

        List<LoanTransactions> loanTransactionList = query.getResultList(); // Execute the query and retrieve the list of loan transactions

        LOGGER.info("Exiting LoanTransactionStatementGeneration method");
        return loanTransactionList;
    }

    @Override
    public List<Account> getthisMonthIntrest() {
        LOGGER.info("Entering getthisMonthIntrest method");

        String jpql = "SELECT la FROM Account la";
        TypedQuery<Account> query = em.createQuery(jpql, Account.class); // Create a typed query

        List<Account> accountList = query.getResultList(); // Execute the query and retrieve the list of accounts

        LOGGER.info("Exiting getthisMonthIntrest method");
        return accountList;
    }

}
