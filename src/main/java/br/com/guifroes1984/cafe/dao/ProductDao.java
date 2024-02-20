package br.com.guifroes1984.cafe.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.guifroes1984.cafe.POJO.Product;
import br.com.guifroes1984.cafe.wrapper.ProductWrapper;

public interface ProductDao extends JpaRepository<Product, Integer> {

	List<ProductWrapper> getAllProduct();

}
