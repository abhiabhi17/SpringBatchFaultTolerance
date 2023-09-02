package com.example.springbatchfaulttolernance.repository;


import com.example.springbatchfaulttolernance.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Integer> {
}
