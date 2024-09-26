package com.testing.integration_testing_demo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.testing.integration_testing_demo.entity.Product;

public interface TestH2Database extends JpaRepository<Product, Integer> {

}
