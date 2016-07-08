package com.duowan.game.service;

import org.springframework.stereotype.Service;

import com.duowan.game.util.NonsenseGenerator;

@Service("demoService")
public class DemoService {
	private NonsenseGenerator generator = NonsenseGenerator.getInstance();

	public String chat(String user, String message) {
		String serverMessage = "hello " + user + ", echo from server: " + message
				+ ", message from server: " + generator.makeText(3);
		return serverMessage;
	}

	public String pushNonsense(String user) {
		return user + ", random sentences for you: " + generator.makeText(3);
	}
}
