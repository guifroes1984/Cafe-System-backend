package br.com.guifroes1984.cafe.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.guifroes1984.cafe.POJO.Category;

public interface CategoryDao extends JpaRepository<Category, Integer> {
	
	List<Category> getAllCategory();

}
