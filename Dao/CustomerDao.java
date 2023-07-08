package com.nkxgen.spring.jdbc.Dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.nkxgen.spring.jdbc.Bal.CustomerSetter;
import com.nkxgen.spring.jdbc.DaoInterfaces.CustomerDaoInterface;
import com.nkxgen.spring.jdbc.controller.LoginController;
import com.nkxgen.spring.jdbc.model.Customer;
import com.nkxgen.spring.jdbc.model.CustomerSub;
import com.nkxgen.spring.jdbc.model.Customertrail;

@Repository
@Transactional
public class CustomerDao implements CustomerDaoInterface {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private CustomerSetter s;
	Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

	@Override
	public void saveCustomer(Customertrail customer) {
        LOGGER.info("Saving customer: {}", customer);

        entityManager.merge(customer);
    }

    // Get Customer By ID
    @Override
    public Customertrail getCustomerById(Long id) {
        LOGGER.info("Retrieving customer by ID: {}", id);

        Customertrail customer = entityManager.find(Customertrail.class, id);
        return customer;
    }

    // Save Customer to Database
    @Override
    public void saveCustomertoDb(Customer customer) {
        LOGGER.info("Saving customer to database: {}", customer);

        entityManager.persist(customer);
    }

    // Get All Customers
    @Override
    public List<Customertrail> getAllCustomers() {
        LOGGER.info("Retrieving all customers");

        String jpql = "SELECT la FROM Customertrail la";
        TypedQuery<Customertrail> query = entityManager.createQuery(jpql, Customertrail.class);

        return query.getResultList();
    }

    // Update Customer Data By ID
    @Override
    public void updateCustomerDataById(Customertrail updatedCustomer) {
        LOGGER.info("Updating customer data for ID: {}", updatedCustomer.getId());

        entityManager.merge(updatedCustomer);
    }

    // Delete Customer By ID
    @Override
    public void deleteCusById(Customertrail cus) {
    	// Assuming Customertrail class has an 'id' property
    	LOGGER.info("Deleting customer by ID: {}", cus.getId());

    	long customerId = cus.getId(); // Extract the 'id' property from the 'cus' object and store it in 'customerId' variable

    	Customertrail customer = entityManager.find(Customertrail.class, customerId); // Find the customer object with the given 'customerId' using the entity manager

    	if (customer != null) { // If a customer object is found
    	    entityManager.remove(customer); // Remove the customer object from the entity manager
    	    LOGGER.info("Customer with ID {} has been deleted successfully.", customerId);
    	} else { // If no customer object is found
    	    LOGGER.warn("Customer with ID {} does not exist.", customerId);
    	}
    }

    // Get Real Customer By ID
    @Override
    public Customertrail getRealCustomerById(Long customerId) {
    	LOGGER.info("Retrieving real customer by ID: {}", customerId);


        Customertrail customer = entityManager.find(Customertrail.class, customerId); // Find the customer object with the given 'customerId' using the entity manager

        return customer; // Return the found customer object, or null if no customer is found
    }

    // Change Customer Data
    @Override
    public void changethese(Customertrail customer2, CustomerSub customerSub) {
    	LOGGER.info("Modifying customer: {}", customer2);


        Customertrail customer = s.changing(customer2, customerSub); // Call a method 'changing' from a service or utility class 's' to modify the 'customer2' object and assign the modified customer to 'customer' variable

        entityManager.merge(customer); // Merge the modified 'customer' object with the entity manager to update it in the data store
    }

}