package com.duowan.game.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class MyServlet extends HttpServlet {
	public static final Logger log = Logger.getLogger(MyServlet.class);
	private static final long serialVersionUID = -4849141657757659855L;
	private JedisPool pool;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		pool = new JedisPool(new JedisPoolConfig(), "127.0.0.1");
	}

	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException,
			IOException {
		String data = req.getParameter("data");
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.rpush("MyUploadServlet", data);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		PrintWriter writer = res.getWriter();
		writer.print("{'message': 'success', 'status': 200, 'data': None}");
		writer.flush();
		writer.close();
	}

	@Override
	public void destroy() {
		super.destroy();
		try {
			pool.destroy();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
