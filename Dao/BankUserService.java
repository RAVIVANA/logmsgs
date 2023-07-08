package com.nkxgen.spring.jdbc.Dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nkxgen.spring.jdbc.DaoInterfaces.BankUserInterface;
import com.nkxgen.spring.jdbc.Exception.EmailNotFoundException;
import com.nkxgen.spring.jdbc.controller.LoginController;
import com.nkxgen.spring.jdbc.model.BankUser;

@Service
public class BankUserService implements BankUserInterface {

	@PersistenceContext
	private EntityManager entityManager;
	Logger LOGGER = LoggerFactory.getLogger(LoginController.class);


	@Transactional
	public BankUser saveBankUser(BankUser bankUser) {
        LOGGER.info("Saving bank user: {}", bankUser);

        // Persist the BankUser object in the database
        entityManager.persist(bankUser);

        // Flush the changes to the database
        entityManager.flush();

        return bankUser;
    }

	@Transactional
	public void updatePassword(String newPassword, long userID) {
        LOGGER.info("Updating password for user ID: {}", userID);

        String updateQuery = "UPDATE BankUser u SET u.busr_pwd = :busr_pwd WHERE u.busr_id = :userID";

        entityManager.createQuery(updateQuery)
                .setParameter("busr_pwd", newPassword)
                .setParameter("userID", userID)
                .executeUpdate();
    }


    public BankUser getBankUserById(long busr_id) {
        LOGGER.info("Retrieving bank user by ID: {}", busr_id);

        // Retrieve a BankUser object from the database based on the provided ID
        BankUser bankUser = entityManager.find(BankUser.class, busr_id);

        if (bankUser == null) {
            LOGGER.warn("Bank user not found for ID: {}", busr_id);
        }

        return bankUser;
    }

    public boolean getBankUserByEmail(String email, long userID) {
        LOGGER.info("Checking bank user by email: {}, userID: {}", email, userID);

        TypedQuery<BankUser> query = entityManager.createQuery(
                "SELECT u FROM BankUser u WHERE u.busr_email = :email and u.busr_id = :userID", BankUser.class);
        query.setParameter("email", email);
        query.setParameter("userID", userID);
        List<BankUser> results = query.getResultList();

        if (results.isEmpty()) {
            LOGGER.warn("Email not found for userID: {}, email: {}", userID, email);
            throw new EmailNotFoundException("UserID or Email Not Found in our database");
        }

        return true;
    }

    public List<BankUser> getAllBankUsers() {
        LOGGER.info("Retrieving all bank users");

        // Retrieve all BankUser objects from the database, ordered by busr_id in ascending order
        List<BankUser> bankUsers = entityManager.createQuery("SELECT u FROM BankUser u ORDER BY u.busr_id ASC", BankUser.class)
                .getResultList();

        LOGGER.info("Retrieved {} bank users", bankUsers.size());
        return bankUsers;
    }

    @Transactional
    public BankUser saveUser(BankUser bankUser) {
        LOGGER.info("Saving bank user: {}", bankUser);

        // Merge the BankUser object in the database, which will either update an existing record or create a new one
        return entityManager.merge(bankUser);
    }

    @Transactional
    public List<BankUser> getBankUsersByDesignation(String designation) {
        LOGGER.info("Retrieving bank users by designation: {}", designation);

        // Retrieve a list of BankUser objects from the database based on the provided designation
        List<BankUser> bankUsers = entityManager.createQuery("SELECT bu FROM BankUser bu WHERE bu.busr_desg = :designation", BankUser.class)
                .setParameter("designation", designation)
                .getResultList();

        LOGGER.info("Retrieved {} bank users by designation: {}", bankUsers.size(), designation);
        return bankUsers;
    }

    @Transactional
    public List<BankUser> getBankUsersByDesignation(BankUser bankUser) {
        LOGGER.info("Retrieving bank users by designation from bank user object: {}", bankUser);

        // Retrieve a list of BankUser objects from the database based on the designation of the provided BankUser object
        List<BankUser> bankUsers = entityManager.createQuery("SELECT bu FROM BankUser bu WHERE bu.busr_desg = :designation", BankUser.class)
                .setParameter("designation", bankUser.getBusr_desg())
                .getResultList();

        LOGGER.info("Retrieved {} bank users by designation from bank user object: {}", bankUsers.size(), bankUser);
        return bankUsers;
    }
}