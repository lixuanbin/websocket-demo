package com.duowan.game.dao;

import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.alibaba.druid.pool.DruidDataSource;

public class DataSourceFactory {
	public static final Logger log = Logger.getLogger(DataSourceFactory.class);

	protected static final ConcurrentHashMap<String, DataSource> dataSourceCache = new ConcurrentHashMap<String, DataSource>();

	@SuppressWarnings("resource")
	public static boolean registerDataSource(String name, Properties properties) {
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
}