package com.duowan.game.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DemoController {
	@RequestMapping("/chat")
	public ModelAndView welcome(@RequestParam(required = false) String user, HttpSession session) {
		String message = "<br><div style='text-align:center;'>"
				+ "<h3>********** Hello, this page will demonstrate how to use web socket **********</div><br><br>";
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", message);
		String name = StringUtils.isNotBlank(user) ? user : RandomStringUtils.randomAlphanumeric(8);
		session.setAttribute("user", name);
		return new ModelAndView("chat", model);
	}
}
