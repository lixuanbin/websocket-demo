package com.duowan.game.test;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.python.core.PyLong;
import org.python.core.PyObject;

import redis.clients.jedis.Jedis;

/**
 * 测试Python原生Redis连接的性能，发现比hiredis和Jedis慢了差不多5倍。。。
 * 
 * @author lixuanbin
 */
public class RedisPoolTest {
	@Test
	public void testGetSet() throws IOException {
		Jedis jedis = new Jedis("127.0.0.1", 6379);
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			long millis = System.currentTimeMillis();
			jedis.set("millis", String.valueOf(millis));
			jedis.get("millis");
		}
		long end = System.currentTimeMillis();
		System.out.println(String.format("2 time consumed: %d ms.", (end - begin)));
		// tps 8k
		
		org.python.util.PythonInterpreter interpreter = new org.python.util.PythonInterpreter();
		interpreter.exec("import sys");
		interpreter.exec("sys.path.append('D:\\jython2.7.0\\Lib')");
		interpreter.exec("sys.path.append('D:\\jython2.7.0\\Lib\\site-packages')");
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(
				"RedisPoolTest.py");
		String pyScript = IOUtils.toString(inputStream, "utf-8");
		interpreter.exec(pyScript);
		final PyObject blahClass = interpreter.get("RedisPoolTest");
		PyObject blahInstance = blahClass.__call__();
		begin = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			long millis = System.currentTimeMillis();
			blahInstance.invoke("process", new PyLong(millis)).__tojava__(String.class).toString();
		}
		end = System.currentTimeMillis();
		System.out.println(String.format("time consumed: %d ms.", (end - begin)));

		inputStream = getClass().getClassLoader().getResourceAsStream("RedisPoolTest2.py");
		pyScript = IOUtils.toString(inputStream, "utf-8");
		interpreter.exec(pyScript);
		final PyObject blahClass2 = interpreter.get("RedisPoolTest2");
		PyObject blahInstance2 = blahClass2.__call__();
		begin = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			long millis = System.currentTimeMillis();
			blahInstance2.invoke("process", new PyLong(millis)).__tojava__(String.class).toString();
		}
		end = System.currentTimeMillis();
		System.out.println(String.format("2 time consumed: %d ms.", (end - begin)));
		// tps 2k
	}
	
}
