package br.com.guifroes1984.cafe.dao;


import br.com.guifroes1984.cafe.POJO.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UserDao extends JpaRepository<User, Integer> {

	User findByEmailId(@Param("email") String email);
	
}
