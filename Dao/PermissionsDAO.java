package com.nkxgen.spring.jdbc.Dao;

import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.nkxgen.spring.jdbc.ViewModels.GetPermissions;
import com.nkxgen.spring.jdbc.controller.LoginController;
import com.nkxgen.spring.jdbc.model.BankUser;
import com.nkxgen.spring.jdbc.model.Permission;
import com.nkxgen.spring.jdbc.model.User;

@Repository
public class PermissionsDAO {

	@PersistenceContext
	private EntityManager entityManager;

	Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

	@Transactional
	public void updatePermissions(Permission permissions) {
	    System.out.println(permissions.isAccounts());
	    System.out.println(permissions.isAuditLog());
	    
	    LOGGER.info("Updating permissions: accounts={}, auditLog={}", permissions.isAccounts(), permissions.isAuditLog());

	    entityManager.merge(permissions);
	}

	@Transactional
	public void allUpdatePermissions(Permission permissions) {
	    ArrayList<Permission> ar = (ArrayList<Permission>) entityManager
	            .createQuery("select ps from Permission ps where ps.role=:x").setParameter("x", permissions.getRole())
	            .getResultList();
	    
	    System.out.println(ar);
	    System.out.println(permissions.toString());
	    
	    LOGGER.info("Updating permissions for role: {}", permissions.getRole());
	    LOGGER.info("Found {} permissions for role: {}", ar.size(), permissions.getRole());

	    for (Permission x : ar) {
	        System.out.println(x.toString());
	        x.setAccountProcessing(permissions.isAccountProcessing());
	        x.setAccounts(permissions.isAccounts());
	        x.setApplication(permissions.isApplication());
	        x.setAuditLog(permissions.isAuditLog());
	        x.setCustomers(permissions.isCustomers());
	        x.setDashboard(permissions.isDashboard());
	        x.setLoans(permissions.isLoans());
	        x.setTransactions(permissions.isTransactions());
	        x.setUsers(permissions.isUsers());
	    }
	}

	public Permission getPermissions(Long id) {
	    String sql = "SELECT * FROM permissions WHERE user_id = :userId";
	    Permission permissions = (Permission) entityManager.createNativeQuery(sql, Permission.class)
	            .setParameter("userId", id).getSingleResult();

	    LOGGER.info("Retrieving permissions for userId: {}", id);
	    return permissions;
	}

	public BankUser getUserById(int id) {
	    return entityManager.find(BankUser.class, id);
	}

}