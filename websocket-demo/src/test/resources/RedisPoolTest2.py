#!/usr/bin/python
# -*- coding: UTF-8 -*-
import redis
from com.duowan.game.service import ServiceRegistry
from java.util import Properties
from org.apache.log4j import Logger
class RedisPoolTest2:
    def __init__(self):
        self.name = 'RedisPoolTest2'
        self.log = Logger.getLogger(self.name)
        self.pool = ServiceRegistry.getService("testRedisPool")
        if not self.pool:
            self.log.info(self.name + ': creating pool...')
            self.pool = redis.ConnectionPool(host='127.0.0.1', port=6379)
            try:
                ServiceRegistry.registerService("testRedisPool", self.pool)
            finally:
                pass
        self.r = redis.Redis(connection_pool=self.pool)
        self.log.info(self.name + ': init...')
    def process(self, millis):
        self.r.set('millis', millis)
        return self.r.get('millis')
