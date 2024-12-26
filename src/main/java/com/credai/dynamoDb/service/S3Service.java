package com.credai.dynamoDb.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.credai.dynamoDb.entity.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class S3Service {
	
	private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    private final AmazonS3 amazonS3;
    private final DynamoDBMapper dynamoDBMapper;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.folder-name}")
    private String folderName;

    public S3Service(AmazonS3 amazonS3, DynamoDBMapper dynamoDBMapper) {
        this.amazonS3 = amazonS3;
        this.dynamoDBMapper = dynamoDBMapper;
    }
    
    private static final String PATH_SEPARATOR = "/";

    private String generateFileName(Customer customer) {
        String dataToHash = customer.getCustId() + "_" + customer.getPhone(); 
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return "customer_" + hexString.toString() + ".json";
        } catch (NoSuchAlgorithmException e) {
        	logger.error("Error generating file name: {}", e.getMessage(), e);
            throw new RuntimeException("Error generating file name", e);
        }
    }

    public void saveCustomerToS3(String custId) {
    	logger.info("Saving customer with custId: {}", custId);
        Customer customer = dynamoDBMapper.load(Customer.class, custId);

        if (customer == null) {
        	logger.warn("Customer with custId: {} not found in the DynamoDB. Upload to S3Bucket failed", custId);
        	throw new RuntimeException("Customer with custId: " + custId + " not found in DynamoDB.Upload to s3bucket failed");
            
        }

        String fileName = generateFileName(customer);
        String key = folderName + PATH_SEPARATOR + fileName;//String key = folderName + "/" + fileName;

        if (amazonS3.doesObjectExist(bucketName, key)) {
        	logger.info("Customer data with custId: {} already exists in S3. Skipping upload", custId);
            throw new RuntimeException("Customer data with custId: "+custId+ " already exists in S3Bucket. Skipping upload");
    
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(customer);

            InputStream inputStream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(jsonString.length());
            metadata.setContentType(ContentType.APPLICATION_JSON.getMimeType());  //metadata.setContentType("application/json");

            amazonS3.putObject(bucketName, key, inputStream, metadata);

            logger.info("Successfully uploaded customer data to S3Bucket at: {}", key);
        } catch (Exception e) {
        	logger.error("Error uploading customer data to S3Bucket: {}", e.getMessage(), e);
        	throw new RuntimeException("Error uploading customer data to S3Bucket", e);
        }
    }
}
