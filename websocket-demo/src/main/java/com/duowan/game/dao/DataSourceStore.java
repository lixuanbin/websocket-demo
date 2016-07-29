package com.duowan.game.dao;

import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * Provides database connectivity for python scripts, usage: <br/>
 * 1. Try DataSourceStore.getDataSource(name);<br/>
 * 2. If null returned, register your own data source and call get again.
 * 
 * @author lixuanbin
 */
@Service
public class DataSourceStore {
	public static final Logger log = Logger.getLogger(DataSourceStore.class);
	protected static final ConcurrentHashMap<String, DataSource> dataSourceCache = new ConcurrentHashMap<String, DataSource>();

	@PostConstruct
	public void initCache() {
		// Init pre-defined datasources blah blah blah...
	}

	@SuppressWarnings("resource")
	public static synchronized boolean registerDataSource(String name, Properties properties) {
		if (dataSourceCache.get(name) != null) {
			throw new RuntimeException(name + " already exists!");
		}
		DruidDataSource dataSource = new DruidDataSource(false);
		dataSource.configFromPropety(properties);
		try {
			dataSource.init();
			dataSourceCache.put(name, dataSource);
			return true;
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}

	public static DataSource getDataSource(String name) {
		if (dataSourceCache.get(name) == null) {
			throw new RuntimeException("No datasource named " + name + " registered.");
		}
		return dataSourceCache.get(name);
	}

	public static void destroyCache() {
		for (DataSource dataSource : dataSourceCache.values()) {
			try {
				((DruidDataSource) dataSource).close();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	@PreDestroy
	public void destroy() {
		// Destroy cache as spring context close.
		destroyCache();
	}
}
