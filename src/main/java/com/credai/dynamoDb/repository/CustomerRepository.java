package com.credai.dynamoDb.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.credai.dynamoDb.entity.Customer;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, String>{ 

}
