package com.duowan.game.test;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.python.core.PyLong;
import org.python.core.PyObject;

/**
 * 借用Druid的DataSource测试简单插入，性能与Spring Dao没有太大差距
 * 
 * @author lixuanbin
 */
public class PyConnectionTest {
	@Test
	@Ignore
	public void testInsert() throws IOException {
		org.python.util.PythonInterpreter interpreter = new org.python.util.PythonInterpreter();
		interpreter.exec("import sys");
		interpreter.exec("sys.path.append('D:\\jython2.7.0\\Lib')");
		interpreter.exec("sys.path.append('D:\\jython2.7.0\\Lib\\site-packages')");
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("PoolConTest.py");
		String pyScript = IOUtils.toString(inputStream, "utf-8");
		interpreter.exec(pyScript);
		final PyObject blahClass = interpreter.get("PoolConTest");
		PyObject blahInstance = blahClass.__call__();
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			long millis = System.currentTimeMillis();
			blahInstance.invoke("process", new PyLong(millis)).__tojava__(String.class).toString();
		}
		long end = System.currentTimeMillis();
		System.out.println(String.format("time consumed: %d ms.", (end - begin)));
	}
}
