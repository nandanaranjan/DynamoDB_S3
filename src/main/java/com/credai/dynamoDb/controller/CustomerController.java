package com.credai.dynamoDb.controller;

import java.util.Map;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.credai.dynamoDb.entity.Customer;
import com.credai.dynamoDb.service.CustomerService;
import com.credai.dynamoDb.service.S3Service;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/customer")
public class CustomerController {

	private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

	@Autowired
	private CustomerService customerService;

	@Autowired
	private S3Service s3Service;

	@PostMapping("/create")
	public ResponseEntity<String> createCustomer(@Valid @RequestBody Customer customer) {
		try {
			logger.info("Received request to create customer: {}", customer);
			customerService.createCustomer(customer);
			logger.info("Customer created successfully: {}", customer.getCustId());
			return ResponseEntity.ok("Customer created successfully");
		} catch (IllegalArgumentException e) {
			logger.error("Error creating customer: Invalid argument: {}", e.getMessage());
			return ResponseEntity.badRequest().body("Error creating customer: " + e.getMessage());
		} catch (Exception e) {
			logger.error("Error creating customer: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
					.body("Error creating customer: " + e.getMessage());
		}
	}

	@PostMapping("/upload")
	public ResponseEntity<String> saveToS3(@RequestBody Map<String, Object> jsonData) {
		try {
			if (jsonData == null || !jsonData.containsKey("custId")) {
				logger.warn("Invalid request data: 'custId' is required");
				return ResponseEntity.badRequest().body("Invalid data:'custId' is required");
			}

			String custId = jsonData.get("custId").toString();
			logger.info("Received request to save customer data to S3 for custId: {}", custId);
			s3Service.saveCustomerToS3(custId);
			logger.info("File successfully saved to S3 for custId: {}", custId);

			return ResponseEntity.ok("File saved to S3 Successfully");
		} catch (IllegalArgumentException e) {
			logger.error("Error - During S3 upload: {}", e.getMessage());
			return ResponseEntity.badRequest().body("Error: " + e.getMessage());
		} catch (Exception e) {
			logger.error("Error saving file to S3: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
					.body("Error saving file to S3: " + e.getMessage());
		}
	}
}
