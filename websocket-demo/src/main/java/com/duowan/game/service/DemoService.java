package com.duowan.game.service;

import org.springframework.stereotype.Service;

import com.duowan.game.util.NonsenseGenerator;
import com.jcabi.aspects.Loggable;

@Service("demoService")
public class DemoService {
	private NonsenseGenerator generator = NonsenseGenerator.getInstance();

	@Loggable(value = Loggable.INFO, name = "org.apache.log4j.Logger")
	public String chat(String user, String message) {
		String serverMessage = "hello " + user + ", echo from server: " + message
				+ ", message from server: " + generator.makeText(3);
		return serverMessage;
	}

	@Loggable(value = Loggable.INFO, name = "org.apache.log4j.Logger")
	public String pushNonsense(String user) {
		return user + ", random sentences for you: " + generator.makeText(3);
	}
}
