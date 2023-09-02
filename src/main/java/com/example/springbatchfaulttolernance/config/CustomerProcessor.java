package com.example.springbatchfaulttolernance.config;

import com.example.springbatchfaulttolernance.entity.Customer;
import org.springframework.batch.item.ItemProcessor;

/*
 This Class is used to process the data after reading from ITEM-READER

 */
public class CustomerProcessor implements ItemProcessor<Customer, Customer> {
    @Override

    public Customer process(Customer customer) {

 /* Process method is custom implemenation according to business logic */


//        int age = Integer.parseInt(customer.getAge());
//        if (age >= 18) {
//            return customer;
//        }
       return customer;
    }
}