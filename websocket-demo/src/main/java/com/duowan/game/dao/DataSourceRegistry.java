package com.duowan.game.dao;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;

/**
 * Provides database connectivity for python scripts, usage: <br/>
 * 1. Try DataSourceStore.getDataSource(name);<br/>
 * 2. If null returned, register your own data source and call get again.<br/>
 * 
 * @see com.alibaba.druid.pool.DruidDataSource
 * @author lixuanbin
 */
@Service
public class DataSourceRegistry {
	public static final Logger log = Logger.getLogger(DataSourceRegistry.class);
	protected static final ConcurrentHashMap<String, DataSource> dataSourceCache = new ConcurrentHashMap<String, DataSource>();

	@PostConstruct
	public void init() {

	}

	public static synchronized void initCache() {
		// Init pre-defined datasources from config files or db.
	}

	public static synchronized boolean registerDataSource(String name, Properties properties) {
		if (dataSourceCache.get(name) != null) {
			throw new RuntimeException(name + " already exists!");
		}
		DruidDataSource dataSource;
		try {
			dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
			dataSourceCache.put(name, dataSource);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}

	public static synchronized boolean removeDataSource(String name) {
		boolean result = true;
		if (dataSourceCache.get(name) == null) {
			throw new RuntimeException(name + " does not exist!");
		}
		DruidDataSource dataSource = (DruidDataSource) dataSourceCache.remove(name);
		dataSource.close();
		return result;
	}

	/**
	 * Remember to check null.
	 * 
	 * @param name
	 * @return
	 */
	public static DataSource getDataSource(String name) {
		return dataSourceCache.get(name);
	}

	public static synchronized void destroyCache() {
		for (DataSource dataSource : dataSourceCache.values()) {
			try {
				((DruidDataSource) dataSource).close();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		dataSourceCache.clear();
	}

	@PreDestroy
	public void destroy() {
		destroyCache();
	}
}
