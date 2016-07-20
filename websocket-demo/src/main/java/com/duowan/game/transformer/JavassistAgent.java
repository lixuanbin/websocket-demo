package com.duowan.game.transformer;

import java.lang.instrument.Instrumentation;

public class JavassistAgent {
	public static void premain(String agentArgs, Instrumentation inst) {
		System.out.println("Starting the agent");
		inst.addTransformer(new ImportantLogClassTransformer());
	}
}