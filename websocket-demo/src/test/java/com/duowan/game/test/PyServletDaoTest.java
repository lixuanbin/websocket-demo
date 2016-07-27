package com.duowan.game.test;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.duowan.game.dao.PyServletDao;
import com.duowan.game.domain.PyServletDto;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:websocket-demo-servlet.xml" })
public class PyServletDaoTest {
	@Autowired
	private PyServletDao dao;

	@Test
	public void testFindByClassName() {
		String className = "hello";
		PyServletDto dto = dao.findByClassName(className);
		System.out.println(dto);
	}

	@Test
	@Ignore
	public void testSave() {
		PyServletDto dto = new PyServletDto();
		dto.setClassName("add_to_page");
		dto.setScript("#######################################################################\n"
				+ "# add_numbers.py\n"
				+ "#\n"
				+ "# Calculates the sum for two numbers and returns it.\n"
				+ "#######################################################################\n"
				+ "import javax\n"
				+ "\n"
				+ "class add_numbers(javax.servlet.http.HttpServlet):\n"
				+ "    def doGet(self, request, response):\n"
				+ "        self.doPost(request, response)\n"
				+ "\n"
				+ "    def doPost(self, request, response):\n"
				+ "        x = request.getParameter(\"x\")\n"
				+ "        y = request.getParameter(\"y\")\n"
				+ "        if not x or not y:\n"
				+ "            sum = \"<font color='red'>You must place numbers in each value box</font>\"\n"
				+ "        else:\n"
				+ "            try:\n"
				+ "                sum = int(x) + int(y)\n"
				+ "            except ValueError, e:\n"
				+ "                sum = \"<font color='red'>You must place numbers only in each value box</font>\"\n"
				+ "        request.setAttribute(\"sum\", sum)\n"
				+ "        dispatcher = request.getRequestDispatcher(\"testJython.jsp\")\n"
				+ "        dispatcher.forward(request, response)\n");
		dao.save(dto);
	}
	
	@Test
	@Ignore
	public void testToggleStatus() {
		String className = "add_to_page";
		PyServletDto dto = dao.findByClassName(className);
		System.out.println(dto.getStatus());
		dao.toggleStatusByClassName(className);
		dto = dao.findByClassName(className);
		System.out.println(dto.getStatus());
	}
	
	@Test
	public void testFindAll() {
		List<PyServletDto> list = dao.findAll();
		System.out.println(list);
	}
}
