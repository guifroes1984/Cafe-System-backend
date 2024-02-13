package br.com.guifroes1984.cafe.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import br.com.guifroes1984.cafe.wrapper.UserWrapper;

public interface UserService {

	ResponseEntity<String> signUp(Map<String, String> requestMap);

	ResponseEntity<String> login(Map<String, String> requestMap);

	ResponseEntity<List<UserWrapper>> getAllUser();

	ResponseEntity<String> update(Map<String, String> requestMap);

}
