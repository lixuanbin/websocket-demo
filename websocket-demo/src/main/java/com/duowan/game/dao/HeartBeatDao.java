package com.duowan.game.dao;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class HeartBeatDao {
	public static final Logger log = Logger.getLogger(PyServletDao.class);

	@PostConstruct
	public void init() {
		DaoFactory.daoCache.put("HeartBeatDao", this);
	}

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public int save(long millis, String text) {
		String sql = String.format(
				"insert into dataservice.py_script_heartbeat(beat,demo) values (%d,'%s')", millis,
				"nonse");
		return jdbcTemplate.update(sql);
	}
}
