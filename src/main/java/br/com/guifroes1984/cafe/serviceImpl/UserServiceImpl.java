package br.com.guifroes1984.cafe.serviceImpl;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.guifroes1984.cafe.JWT.CustomerUsersDetailsService;
import br.com.guifroes1984.cafe.JWT.JwtUtil;
import br.com.guifroes1984.cafe.POJO.User;
import br.com.guifroes1984.cafe.contents.CafeConstants;
import br.com.guifroes1984.cafe.dao.UserDao;
import br.com.guifroes1984.cafe.service.UserService;
import br.com.guifroes1984.cafe.utils.CafeUtils;
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

}
