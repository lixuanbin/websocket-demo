package com.duowan.game.test;

import static org.junit.Assert.assertEquals;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.File;
import java.io.IOException;

import org.codehaus.groovy.control.CompilationFailedException;
import org.junit.Test;

public class GroovyTest {
	@Test
	public void testHello() throws CompilationFailedException, IOException {
		GroovyShell shell = new GroovyShell();
		final File file = new File("C:/Users/Administrator/Documents/GitHub/groovy_demo/hello.groovy");
		Script script = shell.parse(file);
		Object result = script.run();
		assertEquals(result, "hello world");
	}
}
