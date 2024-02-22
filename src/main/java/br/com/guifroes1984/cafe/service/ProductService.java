package br.com.guifroes1984.cafe.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import br.com.guifroes1984.cafe.wrapper.ProductWrapper;

public interface ProductService {

	ResponseEntity<String> addNewProduct(Map<String, String> requestMap);

	ResponseEntity<List<ProductWrapper>> getAllProduct();

	ResponseEntity<String> updateProduct(Map<String, String> requestMap);

	ResponseEntity<String> deleteProduct(Integer id);

	ResponseEntity<String> updateStatud(Map<String, String> requestMap);

	ResponseEntity<List<ProductWrapper>> getByCategory(Integer id);

	ResponseEntity<List<ProductWrapper>> getProductById(Integer id);

}
