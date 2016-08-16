package com.duowan.game.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.groovy.control.CompilationFailedException;
import org.junit.Test;
import org.python.core.PyLong;
import org.python.core.PyObject;
import org.python.core.PyString;

public class RunPythonScriptTest {
	public static final Logger log = Logger.getLogger(RunPythonScriptTest.class);

	@Test
	public <T> void testHello() throws CompilationFailedException, IOException,
			AttributeNotFoundException, InstanceNotFoundException, MBeanException,
			ReflectionException, MalformedObjectNameException, InterruptedException,
			ExecutionException {
		org.python.util.PythonInterpreter interpreter = new org.python.util.PythonInterpreter();
		interpreter.exec("import sys");
		interpreter.exec("sys.path.append('D:\\jython2.7.0\\Lib')");
		interpreter.exec("sys.path.append('D:\\jython2.7.0\\Lib\\site-packages')");
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("FeiHuoSyncData.py");
		String pyScript = IOUtils.toString(inputStream, "utf-8");
		interpreter.exec(pyScript);
		final PyObject blahClass = interpreter.get("FeiHuoSyncData");
		final PyObject blahInstance = blahClass.__call__();
		int times = 1;
		final CountDownLatch latch = new CountDownLatch(times);
		ExecutorService exec = Executors.newCachedThreadPool();
		List<Future<String>> list = new ArrayList<Future<String>>();
		for (int i = 0; i < times; i++) {
			list.add(exec.submit(new Callable<String>() {
				public String call() throws Exception {
					latch.countDown();
					String millis = String.valueOf(System.currentTimeMillis());
					String result = blahInstance.invoke("process", new PyLong(millis))
							.__tojava__(String.class).toString();
					System.out.println(result);
					return result;
				}
			}));
		}
		exec.shutdown();
		latch.await();
		for (Future<String> future : list) {
			if (!StringUtils.equalsIgnoreCase("success", future.get())) {
				System.out.println(future.get());
			}
		}
		interpreter.close();
	}
}
