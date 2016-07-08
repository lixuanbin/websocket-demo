package com.duowan.game.websocket;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.Logger;

import com.duowan.game.service.DemoService;
import com.duowan.game.util.SpringContextUtil;

@ServerEndpoint(value = "/echo.ws", configurator = GetHttpSessionConfigurator.class)
public class DemoSocket {
	private static Logger log = Logger.getLogger(DemoSocket.class);
	// Spring 4 can inject dependencies for @ServerEndpoint, TIY.
	private static DemoService service;
	private static ConcurrentHashMap<String, Session> onlineUsers = new ConcurrentHashMap<String, Session>();
	private static ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
	static {
		scheduler.scheduleWithFixedDelay(new ServerChatTask(), 60, 300, TimeUnit.SECONDS);
	}

	public DemoSocket() {
		log.info("DemoSocket created at: " + new Date());
	}

	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {
		HttpSession httpSession = (HttpSession) config.getUserProperties().get(
				HttpSession.class.getName());
		String userId = (String) httpSession.getAttribute("user");
		onlineUsers.putIfAbsent(userId, session);
		session.getUserProperties().put("user", userId);
		log.info(userId + " connected...");
		if (service == null) {
			synchronized (this) {
				if (service == null) {
					service = (DemoService) SpringContextUtil.getBean("demoService");
				}
			}
		}
	}

	@OnClose
	public void onClose(Session session) {
		String userId = (String) session.getUserProperties().get("user");
		onlineUsers.remove(userId);
		log.info(userId + " disconnected...");
	}

	@OnMessage
	public void onMessage(Session session, String message) {
		String userId = (String) session.getUserProperties().get("user");
		try {
			String res = service.chat(userId, message);
			session.getBasicRemote().sendText(res);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		log.error(throwable.getMessage(), throwable);
	}

	static class ServerChatTask implements Runnable {
		public void run() {
			for (Map.Entry<String, Session> entry : onlineUsers.entrySet()) {
				if (entry.getValue().isOpen()) {
					String user = entry.getKey();
					log.info("sending random message for: " + user);
					try {
						entry.getValue().getBasicRemote().sendText(service.pushNonsense(user));
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		}
	}
}