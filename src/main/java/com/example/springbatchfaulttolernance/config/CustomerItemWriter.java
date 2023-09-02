package com.example.springbatchfaulttolernance.config;


import com.example.springbatchfaulttolernance.entity.Customer;
import com.example.springbatchfaulttolernance.repository.CustomerRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/*
 This Class is used to write to the repository database from csv file

 */
@Component
public class CustomerItemWriter implements ItemWriter<Customer> {

    @Autowired
    private CustomerRepository repository;

    @Override
    public void write(List<? extends Customer> list) throws Exception {
        System.out.println("Writer Thread "+Thread.currentThread().getName());
        repository.saveAll(list);
    }
}
