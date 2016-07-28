package com.duowan.game.dao;

import java.util.concurrent.ConcurrentHashMap;

public class DaoFactory {
	protected static final ConcurrentHashMap<String, Object> daoCache = new ConcurrentHashMap<String, Object>();

	public static Object getDao(String key) {
		return daoCache.get(key);
	}
}
