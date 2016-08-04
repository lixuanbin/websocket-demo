package com.duowan.game.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolTest {
	@Test
	public void testSingleThread() {
		// average get and release connection time: 0.02 ms
		// average rpush time: 0.056 ms, tps: 17k
		JedisPool pool = null;
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(500);
		config.setMaxIdle(5);
		config.setTestOnBorrow(true);
		pool = new JedisPool(config, "127.0.0.1", 6379);
		for (int i = 0; i < 10; i++) {
			long begin = System.currentTimeMillis();
			for (int k = 0; k < 10000; k++) {
				Jedis jedis = pool.getResource();
				try {
					jedis.rpush("fy_oss.login",
							"{%22commandName%22:%22jobMonitor.runJob%22,%22params%22:{%22jobId%22:%228342a7d6-051a-41da-9fe5-6fd89eb3474e%22,%22dataFrom%22:20160730,%22dataTo%22:20160730,%22interval%22:0}}");
				} finally {
					if (jedis != null) {
						jedis.close();
					}
				}
			}
			long end = System.currentTimeMillis();
			System.out.println(String.format("time consumed: %d ms", (end - begin)));
		}
		pool.close();
	}

	@Test
	public void testMultiThread() throws InterruptedException {
		// average rpush time: 0.034 ms, tps: 29k
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(500);
		config.setMaxIdle(5);
		config.setTestOnBorrow(true);
		final JedisPool pool = new JedisPool(config, "127.0.0.1", 6379);
		for (int i = 0; i < 10; i++) {
			long begin = System.currentTimeMillis();
			final CountDownLatch latch = new CountDownLatch(10000);
			ExecutorService executor = Executors.newFixedThreadPool(100);
			for (int k = 0; k < 10000; k++) {
				executor.execute(new Runnable() {
					public void run() {
						Jedis jedis = null;
						try {
							jedis = pool.getResource();
							jedis.rpush("fy_oss.login",
									"{%22commandName%22:%22jobMonitor.runJob%22,%22params%22:{%22jobId%22:%228342a7d6-051a-41da-9fe5-6fd89eb3474e%22,%22dataFrom%22:20160730,%22dataTo%22:20160730,%22interval%22:0}}");
						} finally {
							if (jedis != null) {
								jedis.close();
							}
							latch.countDown();
						}
					}
				});
			}
			latch.await();
			long end = System.currentTimeMillis();
			System.out.println(String.format("time consumed: %d ms", (end - begin)));
		}
		pool.close();
	}
}
