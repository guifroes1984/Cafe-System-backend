package br.com.guifroes1984.cafe.restImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.guifroes1984.cafe.POJO.Category;
import br.com.guifroes1984.cafe.contents.CafeConstants;
import br.com.guifroes1984.cafe.rest.CategoryRest;
import br.com.guifroes1984.cafe.service.CategoryService;
import br.com.guifroes1984.cafe.utils.CafeUtils;

@RestController
public class CategoryRestImpl implements CategoryRest {
	
	@Autowired
	CategoryService categoryService;

	@Override
	public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
		try {
			return categoryService.addNewCategory(requestMap);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.ALGO_DEU_ERRADO, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
		try {
			return categoryService.getAllCategory(filterValue);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
		try {
			return categoryService.updateCategory(requestMap);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.ALGO_DEU_ERRADO, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
