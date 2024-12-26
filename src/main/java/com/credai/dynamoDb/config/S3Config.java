package com.credai.dynamoDb.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Config {
	
	private static final Logger logger = LoggerFactory.getLogger(S3Config.class);
	
    @Value("${amazon.aws.accesskey}")
    private String accessKey;

    @Value("${amazon.aws.secretkey}")
    private String secretKey;

    @Value("${amazon.aws.region}")
    private String region;

    @Bean
    public AmazonS3 amazonS3() {
    	try {
            logger.info("Initializing AmazonS3");
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder
        		.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    	}catch(Exception e) {
            logger.error("Error occurred while initializing AmazonS3: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize AmazonS3 ", e); 
        }
  }
    @Bean(name = "amazonDynamoDBConfig1")
    public AmazonDynamoDB amazonDynamoDB() {
    	 try {
             logger.info("Initializing Amazon DynamoDB ");
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonDynamoDBClientBuilder
        		.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    	 } catch (Exception e) {
             logger.error("Error occurred while initializing Amazon DynamoDB: {}", e.getMessage());
             throw new RuntimeException("Failed to initialize Amazon DynamoDB", e); 
         }
    }

    @Bean
    public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB) {
    	 try {
             logger.info("Initializing DynamoDBMapper");
        return new DynamoDBMapper(amazonDynamoDB);
    	 }catch (Exception e) {
             logger.error("Error occurred while initializing DynamoDBMapper: {}", e.getMessage());
             throw new RuntimeException("Failed to initialize DynamoDBMapper", e); 
         }
    }
}