package com.duowan.game.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Servlet implementation class RedisServlet
 */
@WebServlet("/RedisServlet.oo")
public class RedisServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private JedisPool pool = null;

	@Override
	public void init() throws ServletException {
		super.init();
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(500);
		config.setMaxIdle(5);
		config.setTestOnBorrow(true);
		pool = new JedisPool(config, "127.0.0.1", 6379);
	}

	@Override
	public void destroy() {
		super.destroy();
		try {
			pool.close();
		} finally {
		}
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RedisServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// ab -n 5000 -c 10 'http://localhost:8080/websocket-demo/RedisServlet.oo?data={%22commandName%22:%22jobMonitor.runJob%22,%22params%22:{%22jobId%22:%228342a7d6-051a-41da-9fe5-6fd89eb3474e%22,%22dataFrom%22:20160730,%22dataTo%22:20160730,%22interval%22:0}}'
		// 7k+ tps, mac, tomcat 8, jdk 8.
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			String data = request.getParameter("data");
			jedis.rpush("fy_oss.login", data);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		response.getWriter().write("{'status': 200, 'message': 'success', 'data': 'None'}");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
