package com.credai.dynamoDb.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

@Configuration
@EnableDynamoDBRepositories(basePackages = "com.credai.dynamoDb.repository")
public class DynamoDbConfig {

	private static final Logger logger = LoggerFactory.getLogger(DynamoDbConfig.class);

	@Value("${amazon.dynamodb.endpoint}")
	String endpoint;
	@Value("${amazon.aws.accesskey}")
	String accesskey;
	@Value("${amazon.aws.secretkey}")
	String secretkey;
	@Value("${amazon.aws.region}")
	String region;

	@Bean
	public AmazonDynamoDB amazonDynamoDB() {
		try {
			logger.info("Initializing Amazon DynamoDb with endpoint: {}, region: {}", endpoint, region);
			return AmazonDynamoDBClientBuilder
					.standard()
					.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
					.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accesskey, secretkey)))
					.build();
		} catch (Exception e) {
			logger.error("Error occurred while initializing Amazon DynamoDb", e);
			throw new RuntimeException("Failed to initialize Amazon DynamoDb", e);
		}
	}

}
