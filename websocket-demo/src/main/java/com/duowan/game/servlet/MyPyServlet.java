package com.duowan.game.servlet;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.duowan.game.dao.PyServletDao;
import com.duowan.game.domain.PyServletDto;

/**
 * This servlet is used to re-serve Jython servlets. It stores bytecode for Jython servlets and
 * re-uses it if the underlying .py file has not changed.
 * <p>
 * e.g. http://localhost:8080/test/hello.py
 * <pre>
 *
 * from javax.servlet.http import HttpServlet
 * class hello(HttpServlet):
 *     def doGet(self, req, res):
 *         res.setContentType("text/html");
 *         out = res.getOutputStream()
 *         print >>out, "<html>"
 *         print >>out, "<head><title>Hello World, How are we?</title></head>"
 *         print >>out, "<body>Hello World, how are we?"
 *         print >>out, "</body>"
 *         print >>out, "</html>"
 *         out.close()
 * </pre>
 *
 * in web.xml for the PyServlet context:
 * <pre>
 * &lt;web-app>
 *     &lt;servlet>
 *         &lt;servlet-name>PyServlet&lt;/servlet-name>
 *         &lt;servlet-class>org.python.util.PyServlet&lt;/servlet-class>
 *         &lt;init-param>
 *             &lt;param-name>python.home&lt;/param-name>
 *             &lt;param-value>/usr/home/jython-2.5&lt;/param-value>
 *         &lt;/init-param>
 *     &lt;/servlet>
 *     &lt;servlet-mapping>
 *         &lt;servlet-name>PyServlet&lt;/servlet-name>
 *         &lt;url-pattern>*.py&lt;/url-pattern>
 *     &lt;/servlet-mapping>
 * &lt;/web-app>
 *
 * </pre>
 */
public class MyPyServlet extends HttpServlet {
	private static final long serialVersionUID = -8736439501739144397L;
	protected static final String INIT_ATTR = "__jython_initialized__";
	protected static final long thirtyMinutesMillis = 30 * 60 * 1000;
	protected static final Logger log = Logger.getLogger(MyPyServlet.class);

	@Autowired
	private PyServletDao dao;
	private PythonInterpreter interp;
	private ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<String, MyPyServlet.CacheEntry>();

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
				config.getServletContext());
		Properties props = new Properties();
		Enumeration<?> e = getInitParameterNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			props.put(name, getInitParameter(name));
		}
		init(props, getServletContext());
		reset();
		log.info("python entry servlet initialized...");
	}

	/**
	 * PyServlet's initialization can be performed as a ServletContextListener
	 * or as a regular servlet, and this is the shared init code. If both
	 * initializations are used in a single context, the system state
	 * initialization code only runs once.
	 */
	protected static void init(Properties props, ServletContext context) {
		String rootPath = getRootPath(context);
		context.setAttribute(INIT_ATTR, true);
		Properties baseProps = PySystemState.getBaseProperties();
		// Context parameters
		Enumeration<?> e = context.getInitParameterNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			props.put(name, context.getInitParameter(name));
		}
		if (props.getProperty("python.home") == null
				&& baseProps.getProperty("python.home") == null) {
			props.put("python.home", rootPath + "WEB-INF" + File.separator + "lib");
		}
		PySystemState.initialize(baseProps, props, new String[0]);
		PySystemState.add_package("javax.servlet");
		PySystemState.add_package("javax.servlet.http");
		PySystemState.add_package("javax.servlet.jsp");
		PySystemState.add_package("javax.servlet.jsp.tagext");
		PySystemState.add_classdir(rootPath + "WEB-INF" + File.separator + "classes");
		PySystemState.add_extdir(rootPath + "WEB-INF" + File.separator + "lib", true);
	}

	protected static PythonInterpreter createInterpreter(ServletContext servletContext) {
		String rootPath = getRootPath(servletContext);
		PySystemState sys = new PySystemState();
		PythonInterpreter interp = new PythonInterpreter(Py.newStringMap(), sys);
		sys.path.append(new PyString(rootPath));

		String modulesDir = rootPath + "WEB-INF" + File.separator + "jython";
		sys.path.append(new PyString(modulesDir));
		return interp;
	}

	protected static String getRootPath(ServletContext context) {
		String rootPath = context.getRealPath("/");
		if (!rootPath.endsWith(File.separator)) {
			rootPath += File.separator;
		}
		return rootPath;
	}

	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException,
			IOException {
		String spath = (String) req.getAttribute("javax.servlet.include.servlet_path");
		if (spath == null) {
			spath = ((HttpServletRequest) req).getServletPath();
			if (spath == null || spath.length() == 0) {
				spath = ((HttpServletRequest) req).getPathInfo();
			}
		}
		getServlet(spath).service(req, res);
	}

	@Override
	public void destroy() {
		super.destroy();
		destroyCache();
	}

	/**
	 * Clears the cache of loaded servlets and makes a new PythonInterpreter to
	 * service further requests.
	 */
	public void reset() {
		destroyCache();
		interp = createInterpreter(getServletContext());
		String os = System.getProperty("os.name");
		interp.exec("import sys");
		if (StringUtils.containsIgnoreCase(os, "Windows")) {
			interp.exec("sys.path.append('D:\\jython2.7.0\\Lib')");
			interp.exec("sys.path.append('D:\\jython2.7.0\\Lib\\site-packages')");
		} else {
			interp.exec("sys.path.append('/data/jython2.7.0/Lib')");
			interp.exec("sys.path.append('/data/jython2.7.0/Lib/site-packages')");
		}
	}

	private HttpServlet getServlet(String path) throws ServletException, IOException {
		CacheEntry entry = cache.get(path);
		if (entry == null || System.currentTimeMillis() - entry.lastTime > thirtyMinutesMillis) {
			synchronized (this) {
				log.info("get servlet step1");
				entry = cache.get(path);
				if (entry == null || System.currentTimeMillis() - entry.lastTime > thirtyMinutesMillis) {
					log.info("get servlet step2");
					return loadServlet(path);
				}
			}
		}
		entry.requestCount.incrementAndGet();
		return entry.servlet;
	}

	/**
	 * Load py servlet from db
	 * 
	 * @param path
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	private HttpServlet loadServlet(String path) throws ServletException, IOException {
		PyServletDto dto = dao.findByRequestPath(path);
		if (dto == null) {
			throw new ServletException("No python servlet found for " + path);
		}
		HttpServlet servlet = createInstance(interp, dto.getClassName(), dto.getScript(),
				HttpServlet.class);
		try {
			servlet.init(getServletConfig());
		} catch (PyException e) {
			throw new ServletException(e);
		}
		AtomicLong lastCount = cache.get(path) != null ? cache.get(path).requestCount : new AtomicLong(0);
		lastCount.incrementAndGet();
		cache.put(path, new CacheEntry(dto, servlet, System.currentTimeMillis(), lastCount));
		return servlet;
	}

	protected static <T> T createInstance(PythonInterpreter interp, String className,
			String script, Class<T> type) throws ServletException {
		try {
			interp.exec(script);
			PyObject cls = interp.get(className);
			if (cls == null) {
				throw new ServletException("No callable (class or function) named " + className
						+ " in " + script);
			}
			PyObject pyServlet = cls.__call__();
			Object o = pyServlet.__tojava__(type);
			if (o == Py.NoConversion) {
				throw new ServletException("The value from " + className + " must extend "
						+ type.getSimpleName());
			}
			@SuppressWarnings("unchecked")
			T asT = (T) o;
			return asT;
		} catch (PyException e) {
			throw new ServletException(e);
		}
	}

	private void destroyCache() {
		for (CacheEntry entry : cache.values()) {
			entry.servlet.destroy();
		}
		cache.clear();
	}

	private static class CacheEntry {
		public PyServletDto dto;
		public HttpServlet servlet;
		public long lastTime;
		public AtomicLong requestCount; // TODO should use redis instead

		CacheEntry(PyServletDto dto, HttpServlet servlet, long lastAccessTime, AtomicLong requestCount) {
			this.servlet = servlet;
			this.dto = dto;
			this.lastTime = lastAccessTime;
			this.requestCount = requestCount;
		}
	}

}