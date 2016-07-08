package com.duowan.game.domain;

import java.io.Serializable;

public class AjaxResponse implements Serializable {
	private static final long serialVersionUID = -3919333267766552982L;

	private int statusCode;
	private String message;
	private Object payLoad;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getPayLoad() {
		return payLoad;
	}

	public void setPayLoad(Object payLoad) {
		this.payLoad = payLoad;
	}
}
