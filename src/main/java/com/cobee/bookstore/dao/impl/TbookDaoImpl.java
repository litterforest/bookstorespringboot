package com.cobee.bookstore.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cobee.bookstore.dao.ITbookDao;
import com.cobee.bookstore.entity.Tbook;

@Repository
public class TbookDaoImpl implements ITbookDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public Tbook get(String isbn){
		return jdbcTemplate.queryForObject("select * from tbook where isbn = ?", new Object[]{ isbn }, new BeanPropertyRowMapper<Tbook>(Tbook.class));
	}

	public List<Tbook> listAll() {
		
		return jdbcTemplate.query("select * from tbook", new BeanPropertyRowMapper<Tbook>(Tbook.class));
		
	}
	
}
