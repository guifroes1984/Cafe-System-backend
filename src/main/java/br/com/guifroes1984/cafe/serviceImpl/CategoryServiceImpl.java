package br.com.guifroes1984.cafe.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;import com.google.common.base.Strings;

import br.com.guifroes1984.cafe.JWT.JwtFilter;
import br.com.guifroes1984.cafe.POJO.Category;
import br.com.guifroes1984.cafe.contents.CafeConstants;
import br.com.guifroes1984.cafe.dao.CategoryDao;
import br.com.guifroes1984.cafe.service.CategoryService;
import br.com.guifroes1984.cafe.utils.CafeUtils;

@Service
public class CategoryServiceImpl implements CategoryService {
	
	@Autowired
	CategoryDao categoryDao;
	
	@Autowired
	JwtFilter jwtFilter;

	@Override
	public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
		try {
			if (jwtFilter.isAdmin()) {
				if (validateCategoryMap(requestMap, false)) {
					categoryDao.save(getCategoryFromMap(requestMap, false));
					return CafeUtils.getResponseEntity("Categoria adicionada com sucesso", HttpStatus.OK);
				}
			} else {
				return CafeUtils.getResponseEntity(CafeConstants.ACESSO_NAO_AUTORIZADO, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.ALGO_DEU_ERRADO, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {
		if (requestMap.containsKey("name")) {
			if (requestMap.containsKey("id") && validateId) {
				return true;
			} else if (!validateId) {
				return true;
			}
		}
		return false;
	}
	
	private Category getCategoryFromMap(Map<String, String> requestMap, Boolean isAdd) {
		Category category = new Category();
		if (isAdd) {
			category.setId(Integer.parseInt(requestMap.get("id")));
		}
		category.setName(requestMap.get("name"));
		return category;
	}

	@Override
	public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
		try {
			if (!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")) {
				return new ResponseEntity<List<Category>>(categoryDao.getAllCategory(), HttpStatus.OK);
			}
			return new ResponseEntity<>(categoryDao.findAll(), HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<List<Category>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	 @Override
	    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
	        try {
	            if(jwtFilter.isAdmin()) {
	                if(validateCategoryMap(requestMap, true)) {
	                    Optional<?> optional = categoryDao.findById(Integer.parseInt(requestMap.get("id")));
	                    if(!optional.isEmpty()) {
	                        categoryDao.save(getCategoryFromMap(requestMap, true));
	                        return CafeUtils.getResponseEntity("Atualização de categoria com sucesso", HttpStatus.OK);
	                    } else {
	                        return CafeUtils.getResponseEntity("ID da categoria não existe", HttpStatus.OK);
	                    }
	                }
	                return CafeUtils.getResponseEntity(CafeConstants.DADOS_INVALIDOS, HttpStatus.BAD_REQUEST);
	            } else {
	                return CafeUtils.getResponseEntity(CafeConstants.ACESSO_NAO_AUTORIZADO, HttpStatus.UNAUTHORIZED);
	            }
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	        return CafeUtils.getResponseEntity(CafeConstants.DADOS_INVALIDOS, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
