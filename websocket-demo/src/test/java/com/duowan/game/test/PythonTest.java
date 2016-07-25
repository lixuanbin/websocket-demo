package com.duowan.game.test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;

import org.apache.commons.io.IOUtils;
import org.codehaus.groovy.control.CompilationFailedException;
import org.junit.Test;
import org.python.core.PyLong;
import org.python.core.PyObject;

public class PythonTest {
	@Test
	public <T> void testHello() throws CompilationFailedException, IOException,
			AttributeNotFoundException, InstanceNotFoundException, MBeanException,
			ReflectionException, MalformedObjectNameException, InterruptedException,
			ExecutionException {
		MBeanServer connection = ManagementFactory.getPlatformMBeanServer();
		Set<ObjectInstance> set = connection.queryMBeans(new ObjectName("java.lang:type=Memory"),
				null);
		ObjectInstance oi = set.iterator().next();
		Object attrValue = connection.getAttribute(oi.getObjectName(), "HeapMemoryUsage");
		if (!(attrValue instanceof CompositeData)) {
			System.out.println("attribute value is instanceof [" + attrValue.getClass().getName()
					+ ", exitting -- must be CompositeData.");
			return;
		}
		System.out.println("heap used: " + ((CompositeData) attrValue).get("used").toString());

		attrValue = connection.getAttribute(oi.getObjectName(), "NonHeapMemoryUsage");
		if (!(attrValue instanceof CompositeData)) {
			System.out.println("attribute value is instanceof [" + attrValue.getClass().getName()
					+ ", exitting -- must be CompositeData.");
			return;
		}
		System.out.println("non-heap used: " + ((CompositeData) attrValue).get("used").toString());
		org.python.util.PythonInterpreter interpreter = new org.python.util.PythonInterpreter();
		interpreter.exec("import sys");
		interpreter.exec("sys.path.append('D:\\jython2.7.0\\Lib')");
		interpreter.exec("sys.path.append('D:\\jython2.7.0\\Lib\\site-packages')");
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("db-test.py");
		String pyScript = IOUtils.toString(inputStream, "utf-8");
		interpreter.exec(pyScript);
		final PyObject blahClass = interpreter.get("FundB");
		ExecutorService exec = Executors.newCachedThreadPool();
		List<Future<String>> list = new ArrayList<Future<String>>();
		for (int i = 0; i < 1; i++) {
			list.add(exec.submit(new Callable<String>() {
				public String call() throws Exception {
					String millis = String.valueOf(System.currentTimeMillis());
					PyObject blahInstance = blahClass.__call__();
					String result = blahInstance.invoke("process", new PyLong(millis))
							.__tojava__(String.class).toString();
					// System.out.println(result);
					return result;
				}
			}));
		}
		exec.shutdown();
		exec.awaitTermination(30, TimeUnit.SECONDS);
		for (Future<String> future : list) {
			System.out.println(future.get());
		}
		interpreter.close();

		connection = ManagementFactory.getPlatformMBeanServer();
		set = connection.queryMBeans(new ObjectName("java.lang:type=Memory"), null);
		oi = set.iterator().next();
		attrValue = connection.getAttribute(oi.getObjectName(), "HeapMemoryUsage");
		if (!(attrValue instanceof CompositeData)) {
			System.out.println("attribute value is instanceof [" + attrValue.getClass().getName()
					+ ", exitting -- must be CompositeData.");
			return;
		}
		System.out.println("heap used: " + ((CompositeData) attrValue).get("used").toString());

		attrValue = connection.getAttribute(oi.getObjectName(), "NonHeapMemoryUsage");
		if (!(attrValue instanceof CompositeData)) {
			System.out.println("attribute value is instanceof [" + attrValue.getClass().getName()
					+ ", exitting -- must be CompositeData.");
			return;
		}
		System.out.println("non-heap used: " + ((CompositeData) attrValue).get("used").toString());
	}
}
