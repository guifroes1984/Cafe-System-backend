package br.com.guifroes1984.cafe.dao;


import br.com.guifroes1984.cafe.POJO.User;
import br.com.guifroes1984.cafe.wrapper.UserWrapper;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

public interface UserDao extends JpaRepository<User, Integer> {

	User findByEmailId(@Param("email") String email);
	
	List<UserWrapper> getAllUser();

	List<String> getAllAdmin();
	
	@Transactional
	@Modifying
	Integer updateStatus(@Param("status") String status, @Param("id") Integer id);

	
}
