/**
 * 
 */
package com.duowan.game.service;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * General object cache for reusable stateless singletons written in Java or Python.
 * 
 * @author lixuanbin
 */
@Service
public class ServiceRegistry {
	public static final Logger log = Logger.getLogger(ServiceRegistry.class);
	protected static final ConcurrentHashMap<String, Object> servcieCache = new ConcurrentHashMap<String, Object>();

	@PostConstruct
	public void init() {

	}

	public static synchronized void initCache() {
		// Init pre-defined services from config files or db.
	}

	public static synchronized boolean registerService(String name, Object service) {
		if (servcieCache.get(name) != null) {
			throw new RuntimeException(name + " already exists!");
		}
		servcieCache.put(name, service);
		return true;
	}

	public static synchronized boolean removeService(String name) {
		boolean result = true;
		if (servcieCache.get(name) == null) {
			throw new RuntimeException(name + " does not exist!");
		}
		servcieCache.remove(name);
		return result;
	}

	/**
	 * Remember to check null.
	 * 
	 * @param name
	 * @return
	 */
	public static Object getService(String name) {
		return servcieCache.get(name);
	}

	public static synchronized void destroyCache() {
		servcieCache.clear();
	}

	@PreDestroy
	public void destroy() {
		destroyCache();
	}
}
