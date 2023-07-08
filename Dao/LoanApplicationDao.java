package com.nkxgen.spring.jdbc.Dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nkxgen.spring.jdbc.Bal.CustomerSetter;
import com.nkxgen.spring.jdbc.DaoInterfaces.LoanApplicationDaoInterface;
import com.nkxgen.spring.jdbc.InputModels.LoanApplicationInput;
import com.nkxgen.spring.jdbc.controller.LoginController;
import com.nkxgen.spring.jdbc.model.Customertrail;
import com.nkxgen.spring.jdbc.model.LoanAccount;
import com.nkxgen.spring.jdbc.model.LoanApplication;

@Repository
@Transactional
public class LoanApplicationDao implements LoanApplicationDaoInterface {
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	CustomerSetter s;

	public void setEntity(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	Logger LOGGER = LoggerFactory.getLogger(LoginController.class);


	@Override
	public List<LoanApplication> getLoanApplicationByValue(String value) {
	    LOGGER.info("Retrieving loan applications by value: {}", value);

	    // Define a JPQL query to retrieve loan application objects based on the loan type ID and status
	    String jpql = "SELECT la FROM LoanApplication la WHERE la.loanTypeId = :value and la.status=:val";
	    TypedQuery<LoanApplication> query = entityManager.createQuery(jpql, LoanApplication.class);
	    
	    // Set the value of the 'value' parameter in the query to the provided 'value' value
	    query.setParameter("value", value);
	    
	    // Set the value of the 'val' parameter in the query to "waiting"
	    query.setParameter("val", "waiting");
	    
	    // Execute the query and return a list of loan application objects matching the loan type ID and status
	    return query.getResultList();
	}


	@Override
	public void saveLoanApplication(LoanApplication loanApplication) {
	    // Persist the 'loanApplication' object by adding it to the entity manager
	    LOGGER.info("Saving loan application: {}", loanApplication);
	    entityManager.persist(loanApplication);
	}

	@Override
	public void updateLoanApplication(LoanApplicationInput loanApplication) {
	    // Find the loan application object with the given ID using the entity manager
	    LOGGER.info("Updating loan application with ID: {}", loanApplication.getId());
	    LoanApplication la = entityManager.find(LoanApplication.class, (long) loanApplication.getId());

	    // Update the properties of the loan application object using the provided loan application input
	    la.LoanApplication(loanApplication);

	    // Merge the updated loan application object with the entity manager to update it in the data store
	    entityManager.merge(la);
	}


	@Override
	public List<LoanApplication> getLoanApplicationsByStatus(String status) {
	LOGGER.info("Retrieving loan applications by status: {}", status);

	// Define a JPQL query to retrieve loan application objects based on the status
	String jpql = "SELECT la FROM LoanApplication la WHERE la.status = :status";
	TypedQuery<LoanApplication> query = entityManager.createQuery(jpql, LoanApplication.class);

	// Set the value of the 'status' parameter in the query to the provided 'status' value
	query.setParameter("status", status);

	// Execute the query and return a list of loan application objects matching the status
	return query.getResultList();
	}

	@Override
	public List<LoanAccount> getLoanAccountsByLoanType(String loanType) {
	LOGGER.info("Retrieving loan accounts by loan type: {}", loanType);

	
	// Define a JPQL query to retrieve loan account objects based on the loan type
	String queryStr = "SELECT la FROM LoanAccount la WHERE la.loanType = :loanType";
	TypedQuery<LoanAccount> query = entityManager.createQuery(queryStr, LoanAccount.class);

	// Set the value of the 'loanType' parameter in the query to the provided 'loanType' value
	query.setParameter("loanType", loanType);

	// Execute the query and return a list of loan account objects matching the loan type
	return query.getResultList();
								// type
	}

	@Override
	public void deleteApplication(int loanId) {
	    LoanApplication loanApplication = entityManager.find(LoanApplication.class, loanId); // Find the loan
	                                                                                         // application object
	                                                                                         // with the given
	                                                                                         // 'loanId' using the
	                                                                                         // entity manager

	    if (loanApplication != null) { // If a loan application object is found
	        LOGGER.info("Deleting loan application with loanId: {}", loanId); // Log the deletion operation
	        entityManager.remove(loanApplication); // Remove the loan application object from the entity manager
	        LOGGER.info("Loan application deleted successfully."); // Log the successful deletion
	    } else {
	        LOGGER.info("Loan application with loanId {} not found.", loanId); // Log the case when loan application is not found
	    }
	}

	@Override
	public void approveApplication(int loanId, long custId) {
	    LoanApplication loanApplication = entityManager.find(LoanApplication.class, (long) loanId);
	    // Find the loan
	    // application object
	    // with the given
	    // 'loanId' using the
	    // entity manager

	    Customertrail customer = entityManager.find(Customertrail.class, custId);
	    // Find the customer object with the
	    // given 'custId' using the entity
	    // manager

	    LoanAccount l = new LoanAccount(); // Create a new LoanAccount object

	    l.setValuesFromLoanAccount(loanApplication); // Set the values of the LoanAccount object using the loan
	    // application object

	    entityManager.persist(l); // Persist the LoanAccount object by adding it to the entity manager

	    LOGGER.info("Loan application with loanId {} approved for customerId {}", loanId, custId);
	}

	public List<LoanAccount> getAllLoans() {
		String jpql = "SELECT l FROM LoanAccount l"; // Define a JPQL query to retrieve all loan account objects
		TypedQuery<LoanAccount> query = entityManager.createQuery(jpql, LoanAccount.class); // Create a typed query
																							// using the JPQL query and
																							// specifying the result
																							// type as 'LoanAccount'
		return query.getResultList(); // Execute the query and return a list of all loan account objects
	}

	@Override
	public LoanApplication getLoanApplicationByid(long accountType) {

		LoanApplication LoanApplication = entityManager.find(LoanApplication.class, accountType);
		return LoanApplication;

	}

	@Override
	public void saveTheApprovedLoanApplication(LoanApplication loanapp) {
		entityManager.merge(loanapp);

	}

	@Override
	public LoanApplication getLoanApplicationById(long id) {
	    LOGGER.info("Retrieving loan application with id: {}", id);
	    LoanApplication loanApplication = entityManager.find(LoanApplication.class, id);
	    LOGGER.info("Retrieved loan application with id: {}", id);
	    return loanApplication;
	}

	@Override
	public LoanAccount getLoanAccountById(int accountnumber) {
	    LOGGER.info("Retrieving loan account with account number: {}", accountnumber);
	    LoanAccount loanAccount = entityManager.find(LoanAccount.class, (long) accountnumber);
	    LOGGER.info("Retrieved loan account with account number: {}", accountnumber);
	    return loanAccount;
	}

}
