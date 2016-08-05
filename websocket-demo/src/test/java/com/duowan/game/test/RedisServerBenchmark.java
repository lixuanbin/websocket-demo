package com.duowan.game.test;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisServerBenchmark {
	@Test
	public void test() throws InterruptedException {
		JedisPool pool = new JedisPool(new JedisPoolConfig(), "172.27.22.26");
		for(int a = 0; a < 5; a++) {
			long begin = System.currentTimeMillis();
			for (int i = 0; i < 100000; i++) {
				Jedis jedis = null;
				String data = "{%22commandName%22:%22jobMonitor.runJob%22,%22params%22:{%22jobId%22:%228342a7d6-051a-41da-9fe5-6fd89eb3474e%22,%22dataFrom%22:20160730,%22dataTo%22:20160730,%22interval%22:0}}";
				try {
					jedis = pool.getResource();
					jedis.rpush("anylist", data);
				} finally {
					if (jedis != null) {
						jedis.close();
					}
				}
			}
			long end = System.currentTimeMillis();
			System.out.println(String.format("time consumed: %d ms", (end - begin)));
			TimeUnit.SECONDS.sleep(5);
		}
		pool.close();
	}
}
