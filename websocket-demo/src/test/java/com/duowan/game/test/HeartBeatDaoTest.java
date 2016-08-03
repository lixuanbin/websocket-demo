package com.duowan.game.test;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.duowan.game.dao.HeartBeatDao;

/**
 * 测试单表插入，作为Python版本的对照
 * 
 * @author lixuanbin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class HeartBeatDaoTest {
	@Autowired
	private HeartBeatDao dao;
	
	@Test
	@Ignore
	public void testSave() {
		long begin = System.currentTimeMillis();
		for(int i = 0; i < 10000; i++) {
			dao.save(System.currentTimeMillis(), "nonce");
		} 
		long end = System.currentTimeMillis();
		System.out.println(String.format("time consumed: %d ms.", (end - begin)));
	}
}
