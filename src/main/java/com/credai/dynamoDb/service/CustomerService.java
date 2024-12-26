package com.credai.dynamoDb.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.credai.dynamoDb.entity.Customer;
import com.credai.dynamoDb.repository.CustomerRepository;

@Service
public class CustomerService {
	
	private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);  

	@Autowired
	private CustomerRepository customerRepository;

	public Customer createCustomer(Customer customer) {
		try {
            logger.info("Attempting to create customer with custId: {}", customer.getCustId());
            Customer savedCustomer = customerRepository.save(customer); 
            logger.info("Customer created successfully with ID: {}", savedCustomer.getCustId());
            return savedCustomer;
        } catch (DataIntegrityViolationException e) {
            logger.error("Data Integrity Violation while saving customer: {}", e.getMessage());
            throw new IllegalArgumentException("Customer already exists or invalid data", e);  
        } catch (Exception e) {
            logger.error("Error creating customer: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create customer", e); 
        }
    }

}
