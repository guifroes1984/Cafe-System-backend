package br.com.guifroes1984.cafe.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.guifroes1984.cafe.POJO.Bill;

public interface BillDao extends JpaRepository<Bill, Integer> {

}
