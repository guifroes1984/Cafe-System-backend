package br.com.guifroes1984.cafe.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

import br.com.guifroes1984.cafe.JWT.CustomerUsersDetailsService;
import br.com.guifroes1984.cafe.JWT.JwtFilter;
import br.com.guifroes1984.cafe.JWT.JwtUtil;
import br.com.guifroes1984.cafe.POJO.User;
import br.com.guifroes1984.cafe.contents.CafeConstants;
import br.com.guifroes1984.cafe.dao.UserDao;
import br.com.guifroes1984.cafe.service.UserService;
import br.com.guifroes1984.cafe.utils.CafeUtils;
import br.com.guifroes1984.cafe.utils.EmailUtils;
import br.com.guifroes1984.cafe.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserDao userDao;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	CustomerUsersDetailsService customerUsersDetailsService;
	
	@Autowired
	JwtUtil jwtUtil;
	
	@Autowired
	JwtFilter jwtFilter;
	
	@Autowired
	EmailUtils emailUtils;

	@Override
	public ResponseEntity<String> signUp(Map<String, String> requestMap) {
		log.info("Dentro signup {}", requestMap);
		try {
			if (validateSignUpMap(requestMap)) {
				User user = userDao.findByEmailId(requestMap.get("email"));
				if (Objects.isNull(user)) {
					userDao.save(getUserFromMap(requestMap));
					return CafeUtils.getResponseEntity("Registrado com sucesso.", HttpStatus.OK);
				} else {
					return CafeUtils.getResponseEntity("O e-mail já existe.", HttpStatus.BAD_REQUEST);
				}
			} else {
				return CafeUtils.getResponseEntity(CafeConstants.DADOS_INVALIDOS, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.ALGO_DEU_ERRADO, HttpStatus.BAD_REQUEST);
	}

	private boolean validateSignUpMap(Map<String, String> requestMap) {
		if (requestMap.containsKey("name") && requestMap.containsKey("contactNumber") && requestMap.containsKey("email")
				&& requestMap.containsKey("password")) {
			return true;
		}
		return false;
	}

	private User getUserFromMap(Map<String, String> requestMap) {
		User user = new User();
		user.setName(requestMap.get("name"));
		user.setContactNumber(requestMap.get("contactNumber"));
		user.setEmail(requestMap.get("email"));
		user.setPassword(requestMap.get("password"));
		user.setStatus("false");
		user.setRole("user");
		return user;
	}

	 @Override
	    public ResponseEntity<String> login(Map<String, String> requestMap) {
	        log.info("Login interno");
	        try {
	            Authentication auth = authenticationManager.authenticate(
	                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
	            );
	            if(auth.isAuthenticated()) {
	                if(customerUsersDetailsService.getUserDetails().getStatus().equalsIgnoreCase("true")) {
	                    return new ResponseEntity<String>("{\"token\":\"" +
	                            jwtUtil.generateToken(customerUsersDetailsService.getUserDetails().getEmail(),
	                                    customerUsersDetailsService.getUserDetails().getRole()) + "\"}",
	                    HttpStatus.OK);
	                }
	                else {
	                    return new ResponseEntity<String>("{\"message\":\"" + "Aguarde a aprovação do administrador." + "\"}",
	                            HttpStatus.BAD_REQUEST);
	                }
	            }
	        } catch (Exception ex) {
	            log.error("{}", ex);
	        }
	        return new ResponseEntity<String>("{\"message\":\"" + "Credenciais Incorretas." + "\"}",
	                HttpStatus.BAD_REQUEST);
	    }

	@Override
	public ResponseEntity<List<UserWrapper>> getAllUser() {
		try {
			if (jwtFilter.isAdmin()) {
				return new ResponseEntity<>(userDao.getAllUser(), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> update(Map<String, String> requestMap) {
		try {
			if (jwtFilter.isAdmin()) {
				Optional<User> optional = userDao.findById(Integer.parseInt(requestMap.get("id")));
				if (!optional.isEmpty()) {
					userDao.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
					sendMailToAllAdmin(requestMap.get("status"), optional.get().getEmail(), userDao.getAllAdmin());
					return CafeUtils.getResponseEntity("Atualização de status do usuário com sucesso", HttpStatus.OK);
				} else {
					CafeUtils.getResponseEntity("ID do usuário não existe", HttpStatus.OK);
				}
			} else {
				return CafeUtils.getResponseEntity(CafeConstants.ACESSO_NAO_AUTORIZADO, HttpStatus.UNAUTHORIZED);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.ALGO_DEU_ERRADO, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
		allAdmin.remove(jwtFilter.getCurrentUser());
		if (status != null && status.equalsIgnoreCase("true")) {
			emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Conta aprovada", "DO UTILIZADOR:- " + user + " \n é aprovado por \nADMIN:-" + jwtFilter.getCurrentUser(), allAdmin);
		} else {
			emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Conta desativada", "DO UTILIZADOR:- " + user + " \n é desativado por \nADMIN:-" + jwtFilter.getCurrentUser(), allAdmin);
		}
	}

	@Override
	public ResponseEntity<String> checkToken() {
		return CafeUtils.getResponseEntity("true", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
		try {
			User userObj = userDao.findByEmail(jwtFilter.getCurrentUser());
			if (!userObj.equals(null)) {
				if (userObj.getPassword().equals(requestMap.get("oldPassword"))) {
					userObj.setPassword(requestMap.get("newPassword"));
					userDao.save(userObj);
					return CafeUtils.getResponseEntity("Atualização de senha com sucesso", HttpStatus.OK);
				}
				return CafeUtils.getResponseEntity("Senha antiga incorreta", HttpStatus.BAD_REQUEST);
			}
			return CafeUtils.getResponseEntity(CafeConstants.ALGO_DEU_ERRADO, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.ALGO_DEU_ERRADO, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
		try {
			User user = userDao.findByEmail(requestMap.get("email"));
			if (!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail()))
				emailUtils.forgotMail(user.getEmail(), "Credenciais por sistema de gerenciamento de café", user.getPassword());
			return CafeUtils.getResponseEntity("Verifique seu e-mail para credenciais", HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.ALGO_DEU_ERRADO, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
