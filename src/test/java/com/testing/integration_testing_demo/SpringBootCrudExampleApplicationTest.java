package com.testing.integration_testing_demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.web.client.RestTemplate;

import com.testing.integration_testing_demo.entity.Product;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringBootCrudExampleApplicationTest {

	@LocalServerPort
	private int port;
	
	private String baseUrl = "http://localhost";
	
	private static RestTemplate restTemplate;
	
	@Autowired
	private TestH2Database testH2Database;
	
	@BeforeAll
	public static void init() {
		restTemplate = new RestTemplate();
	}
	
	@BeforeEach
	public void setup() {
		baseUrl = baseUrl.concat(":").concat(port+"").concat("/products");
	}
	
	@Test
	public void testAddProduct() {
		System.out.println("testAddProduct **");
		Product product = new Product("Mobile", 100, 345600);
		Product response = restTemplate.postForObject(baseUrl, product, Product.class);
		assertEquals("Mobile", response.getName());
		assertEquals(1, testH2Database.findAll().size());
	}
	
	@Test
	@Sql(statements = "INSERT INTO PRODUCT_TBL (id, name, quantity, price) values (4, 'AC', 1, 33500)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM PRODUCT_TBL WHERE name='AC'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void testGetProducts() {
		System.out.println("testGetProducts **");
		List<Product> products = restTemplate.getForObject(baseUrl, List.class);
		assertEquals(1, products.size());
		assertEquals(1, testH2Database.findAll().size());
	}
	
	@Test
	@Sql(statements = "INSERT INTO PRODUCT_TBL (id, name, quantity, price) values (111, 'Bus', 1, 33500)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM PRODUCT_TBL WHERE id=111", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void testFindProductById() {
		System.out.println("testFindProductById **");
		int id_product = 111;
		Product product = restTemplate.getForObject(baseUrl+"/{id}", Product.class, id_product);
		assertNotNull(product);
		assertEquals(111, product.getId());
		assertEquals("Bus", product.getName());
	}
	
	@Test
	@Sql(statements = "INSERT INTO PRODUCT_TBL (id, name, quantity, price) values (101, 'town', 1, 33500)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM PRODUCT_TBL WHERE id=101", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void testUpdateProduct() {
		System.out.println("testUpdateProduct **");
		Product newproduct = new Product("shoes", 120, 22300);
		int id_product = 101;
		restTemplate.put(baseUrl+"/update/{id}", newproduct, id_product);
		Product product = testH2Database.findById(id_product).get();
		assertNotNull(product);
		assertEquals(101, product.getId());
		assertEquals("shoes", product.getName());
	}
	
	@Test
	@Sql(statements = "INSERT INTO PRODUCT_TBL (id, name, quantity, price) values (102, 'town', 1, 33500)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	public void testDeleteProduct() {
		System.out.println("testDeleteProduct **");
		int recordCount = testH2Database.findAll().size();
		System.out.println(recordCount);
		assertEquals(2, recordCount);
		restTemplate.delete(baseUrl+"/delete/{id}", 102);
		assertEquals(1, testH2Database.findAll().size());
	}
}
