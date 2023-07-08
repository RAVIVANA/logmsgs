package com.nkxgen.spring.jdbc.Dao;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nkxgen.spring.jdbc.DaoInterfaces.AccountApplicationDaoInterface;
import com.nkxgen.spring.jdbc.controller.LoginController;
import com.nkxgen.spring.jdbc.model.Account;
import com.nkxgen.spring.jdbc.model.AccountApplication;
import com.nkxgen.spring.jdbc.model.Accountdocument;

@Repository
@Transactional
public class AccountApplicationDAO implements AccountApplicationDaoInterface {
	AccountApplicationDAO() {

	}
	Logger LOGGER = LoggerFactory.getLogger(LoginController.class);


	@PersistenceContext
	private EntityManager entityManager;

	@Override 
	public void save(AccountApplication accountApplication) {
	        entityManager.persist(accountApplication);
	        LOGGER.info("AccountApplication saved: {}", accountApplication.getAcapId());
	    }
    
	 @Override
	 public List<AccountApplication> getAccountsappByType(String value) {
	        LOGGER.info("Entering getAccountsappByType method. Value: {}", value);

	        // Create a JPQL query to select AccountApplication objects based on the given type value
	        String jpql = "SELECT la FROM AccountApplication la WHERE la.acap_acty_id = :value";
	        TypedQuery<AccountApplication> query = entityManager.createQuery(jpql, AccountApplication.class);

	        // Set the value parameter in the query
	        query.setParameter("value", value);

	        // Execute the query and retrieve the list of AccountApplication objects
	        List<AccountApplication> list1 = query.getResultList();

	        Iterator<AccountApplication> iterator = list1.iterator();

	        while (iterator.hasNext()) {
	            AccountApplication application = iterator.next();
	            if (application.getStatus().equals("approved")) {
	                iterator.remove();
	            }
	        }

	        LOGGER.info("Exiting getAccountsappByType method. Result count: {}", list1.size());
	        return list1;
	    }
	 
	 @Override
	 public List<Account> getAccountsByType(String acnt_acty_id) {
	        LOGGER.info("Entering getAccountsByType method. Account type ID: {}", acnt_acty_id);

	        // Create a JPQL query to select Account objects based on the given account type ID
	        String jpql = "SELECT la FROM Account la WHERE la.accountTypeId = :acnt_acty_id";
	        TypedQuery<Account> query = entityManager.createQuery(jpql, Account.class);

	        // Set the acnt_acty_id parameter in the query
	        query.setParameter("acnt_acty_id", acnt_acty_id);

	        // Execute the query and retrieve the list of Account objects
	        List<Account> accountList = query.getResultList();

	        LOGGER.info("Exiting getAccountsByType method. Result count: {}", accountList.size());
	        return accountList;
	    }

	    @Override
	    public List<Account> getAccountssByType(String acnt_acty_id) {
	        LOGGER.info("Entering getAccountssByType method. Account type ID: {}", acnt_acty_id);

	        // Create a JPQL query to select Account objects based on the given account type ID
	        String jpql = "SELECT la FROM Account la WHERE la.accountTypeId = :acnt_acty_id";
	        TypedQuery<Account> query = entityManager.createQuery(jpql, Account.class);

	        // Set the acnt_acty_id parameter in the query
	        query.setParameter("acnt_acty_id", acnt_acty_id);

	        // Execute the query and retrieve the list of Account objects
	        List<Account> accountList = query.getResultList();

	        LOGGER.info("Exiting getAccountssByType method. Result count: {}", accountList.size());
	        return accountList;
	    }

	    @Override
	    public void saveAccount(Account account) {
	        LOGGER.info("Saving account: {}", account);

	        // Merge the account object with the entity manager to ensure it's in a managed state
	        Account mergedAccount = entityManager.merge(account);

	        // Persist the merged account object to the database
	        entityManager.persist(mergedAccount);

	        LOGGER.info("Account saved successfully");
	    }

	    @Override
	    public void saveAccountdocument(Accountdocument accountdocument) {
	        LOGGER.info("Saving account document: {}", accountdocument);

	        // Merge the account document object with the entity manager to ensure it's in a managed state
	        Accountdocument mergedAccount = entityManager.merge(accountdocument);

	        // Persist the merged account document object to the database
	        entityManager.persist(mergedAccount);

	        LOGGER.info("Account document saved successfully");
	    }

	    @Override
	    public List<Account> getall() {
	        LOGGER.info("Entering getall method");

	        // Define the JPQL query to select all accounts
	        String query = "SELECT l FROM Account l";

	        // Create a TypedQuery using the query string and specifying the Account class as the result type
	        TypedQuery<Account> query1 = entityManager.createQuery(query, Account.class);

	        // Execute the query and return the list of Account objects
	        List<Account> accountList = query1.getResultList();

	        LOGGER.info("Exiting getall method. Result count: {}", accountList.size());
	        return accountList;
	    }

	    @Override
        public Account mergeAccount(Account account) {
	        LOGGER.info("Merging account: {}", account);
	        return entityManager.merge(account);
	    }

	    @Override
	    public AccountApplication getAccountApplicationById(Long applicationId) {
	        LOGGER.info("Getting AccountApplication by ID: {}", applicationId);

	        AccountApplication accountApplication = entityManager.find(AccountApplication.class, applicationId);

	        if (accountApplication != null) {
	            LOGGER.info("Retrieved AccountApplication with status: {}", accountApplication.getStatus());
	        } else {
	            LOGGER.info("AccountApplication not found for ID: {}", applicationId);
	        }

	        return accountApplication;
	    }

	    public void savetheAccountapp(AccountApplication accountApplication) {
	        LOGGER.info("Saving AccountApplication: {}", accountApplication);
	        entityManager.merge(accountApplication); // Persist the AccountApplication object to the database
	    }

	    @Override
	    public Account getAccountById(Long num) {
	        LOGGER.info("Getting Account by ID: {}", num);
	        Account account = entityManager.find(Account.class, num);

	        if (account != null) {
	            LOGGER.info("Retrieved Account with ID: {}", num);
	        } else {
	            LOGGER.info("Account not found for ID: {}", num);
	        }

	        return account;
	    }

	    @Override
	    public AccountApplication getAccountsappById(long value) {
	        LOGGER.info("Getting AccountApplication by ID: {}", value);

	        // Create a JPQL query to select AccountApplication objects based on the given type value

	        // Execute the query and retrieve the list of AccountApplication objects
	        AccountApplication accountApplication = entityManager.find(AccountApplication.class, value);

	        if (accountApplication != null) {
	            LOGGER.info("Retrieved AccountApplication with ID: {}", value);
	        } else {
	            LOGGER.info("AccountApplication not found for ID: {}", value);
	        }

	        return accountApplication;
	    }
}
