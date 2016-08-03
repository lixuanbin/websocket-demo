package com.duowan.game.test;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.Ignore;
import org.junit.Test;

import com.duowan.game.dao.DataSourceRegistry;

/*@RunWith(SpringJUnit4ClassRunner.class)
 @ContextConfiguration(locations = { "classpath:applicationContext.xml" })*/
/**
 * 测试Druid获取连接和关闭连接的速度，相当快
 * 
 * @author lixuanbin
 */
public class DruidDataSourceTest {
	@Test
	@Ignore
	public void testGetConnection() throws SQLException {
		Properties properties = new Properties();
		properties.put("url", "jdbc:mysql://localhost/dataservice");
		properties.put("username", "root");
		properties.put("driverClassName", "com.mysql.jdbc.Driver");
		properties.put("password", "ben");
		properties.put("initialSize", "1");
		properties.put("minIdle", "5");
		properties.put("maxActive", "10");
		String name = "localSource";
		DataSourceRegistry.registerDataSource(name, properties);
		DataSource dataSource = DataSourceRegistry.getDataSource(name);
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			java.sql.Connection connection = dataSource.getConnection();
			connection.close();
		}
		long end = System.currentTimeMillis();
		System.out.println(String.format("time consumed: %d ms.", (end - begin)));
		DataSourceRegistry.destroyCache();
	}
}
