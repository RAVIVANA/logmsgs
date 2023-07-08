package com.nkxgen.spring.jdbc.Dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.nkxgen.spring.jdbc.DaoInterfaces.LoanTypesInterface;
import com.nkxgen.spring.jdbc.controller.LoginController;
import com.nkxgen.spring.jdbc.model.LoansTypes;

@Repository
@Transactional
public class LoanTypesDAO implements LoanTypesInterface {

	@PersistenceContext
	private EntityManager entityManager;
	Logger LOGGER = LoggerFactory.getLogger(LoginController.class);


public List<LoansTypes> getAllLoans() {
    LOGGER.info("Retrieving all loan types.");
    String jpql = "SELECT l FROM LoansTypes l"; // Define a JPQL query to retrieve all loan types
    TypedQuery<LoansTypes> query = entityManager.createQuery(jpql, LoansTypes.class); // Create a typed query using
    // the JPQL query and
    // specifying the result
    // type as 'LoansTypes'
    List<LoansTypes> loanTypes = query.getResultList(); // Execute the query and retrieve a list of all loan types
    LOGGER.info("Retrieved {} loan types.", loanTypes.size());
    return loanTypes;
}

public List<LoansTypes> getAllLoanDetails() {
    LOGGER.info("Retrieving all loan details.");
    String jpql = "SELECT l FROM LoansTypes l"; // Define a JPQL query to retrieve all loan types
    TypedQuery<LoansTypes> query = entityManager.createQuery(jpql, LoansTypes.class); // Create a typed query using
    // the JPQL query and
    // specifying the result
    // type as 'LoansTypes'
    List<LoansTypes> loanDetails = query.getResultList(); // Execute the query and retrieve a list of all loan details
    LOGGER.info("Retrieved {} loan details.", loanDetails.size());
    return loanDetails;
}

public LoansTypes getSelectedLoanDetails(int loanType) {
    LOGGER.info("Retrieving loan details for loanType: {}", loanType);
    LoansTypes loan = entityManager.find(LoansTypes.class, loanType); // Find the loan type object with the given loanType using the entity manager
    LOGGER.info("Retrieved loan details for loanType: {}. LoanType: {}, DescriptionForm: {}", loanType, loan.getLoanType(), loan.getDescriptionForm());
    return loan; // Return the found loan type object
}

public void save(LoansTypes loansTypes) {
    LOGGER.info("Saving loan type: {}", loansTypes.getLoanType());
    if (loansTypes.getLoanType() != null && loansTypes.getDescriptionForm() != null) {
        entityManager.merge(loansTypes); // Merge the account type entity with the persistence context
    } else {
        // Handle the case where either accountType or descriptionForm is null
        // You can throw an exception, log an error, or perform any appropriate action
        LOGGER.error("Invalid account type data. Account type or description form is null.");
    }
    LOGGER.info("Loan type saved successfully.");
}

}
