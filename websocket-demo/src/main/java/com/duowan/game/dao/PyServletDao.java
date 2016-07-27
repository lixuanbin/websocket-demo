package com.duowan.game.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.duowan.game.domain.PyServletDto;

@Repository
public class PyServletDao {
	protected static final Logger log = Logger.getLogger(PyServletDao.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	ResultSetExtractor<PyServletDto> extractor = new ResultSetExtractor<PyServletDto>() {
		public PyServletDto extractData(ResultSet rs) throws SQLException, DataAccessException {
			if (rs.next()) {
				PyServletDto dto = new PyServletDto();
				dto.setId(rs.getLong("id"));
				dto.setClassName(rs.getString("className"));
				dto.setRequestPath(rs.getString("requestPath"));
				dto.setScript(rs.getString("script"));
				dto.setLastModifiedTime(new Date(rs.getTimestamp("last_modified_time").getTime()));
				dto.setStatus(rs.getInt("status"));
				return dto;
			}
			return null;
		}
	};

	public PyServletDto findByClassName(String className) {
		String sql = String.format("select * from dataservice.tb_py_servlet where className='%s'",
				className);
		log.info("findByClassName sql: " + sql);
		return jdbcTemplate.query(sql, extractor);
	}
	
	public PyServletDto findByRequestPath(String requestPath) {
		String sql = String.format("select * from dataservice.tb_py_servlet where requestPath='%s'",
				requestPath);
		log.info("findByRequestPath sql: " + sql);
		return jdbcTemplate.query(sql, extractor);
	}

	public List<PyServletDto> findAll() {
		List<PyServletDto> list = null;
		String sql = "select * from dataservice.tb_py_servlet";
		list = jdbcTemplate.query(sql, new RowMapper<PyServletDto>() {
			public PyServletDto mapRow(ResultSet rs, int i) throws SQLException {
				PyServletDto dto = new PyServletDto();
				dto.setId(rs.getLong("id"));
				dto.setClassName(rs.getString("className"));
				dto.setRequestPath(rs.getString("requestPath"));
				dto.setScript(rs.getString("script"));
				dto.setLastModifiedTime(new Date(rs.getTimestamp("last_modified_time").getTime()));
				dto.setStatus(rs.getInt("status"));
				return dto;
			}
		});
		return list;
	}

	public int save(final PyServletDto dto) {
		if (dto == null || StringUtils.isBlank(dto.getClassName())
				|| StringUtils.isBlank(dto.getScript())) {
			throw new RuntimeException("PyServlet className or script attribute cannot be empty!");
		}
		String sql = "insert into dataservice.tb_py_servlet(className,requestPath,script) values (?,?,?)";
		return jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, dto.getClassName());
				ps.setString(2, dto.getRequestPath());
				ps.setString(3, dto.getScript());
			}
		});
	}

	public int deleteByClassName(String className) {
		String sql = "delete from dataservice.tb_py_servlet where className=?";
		return jdbcTemplate.update(sql, className);
	}

	public int deleteById(int id) {
		String sql = String.format("delete from dataservice.tb_py_servlet where id=%d", id);
		return jdbcTemplate.update(sql);
	}

	public int updateScriptByClassName(String className, String script) {
		String sql = "update dataservice.tb_py_servlet set script=? where className=?";
		return jdbcTemplate.update(sql, script, className);
	}

	public int toggleStatusByClassName(String className) {
		String sql = "update dataservice.tb_py_servlet set status=CASE WHEN status=-1 THEN 0 WHEN status=0 THEN -1 end where className=?";
		return jdbcTemplate.update(sql, className);
	}
}
