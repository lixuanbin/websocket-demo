#!/usr/bin/python
# -*- coding: UTF-8 -*-
import json
from redis.clients.jedis import *
from javax.servlet.http import HttpServlet
from com.duowan.game.service import ServiceRegistry
from java.util import Properties
from com.duowan.game.servlet import MyPyServlet
from org.apache.log4j import Logger
class SampleUploadServlet2(HttpServlet):
    def __init__(self):
        self.log = MyPyServlet.log
        self.name = 'SampleUploadServlet2'
        self.pool = ServiceRegistry.getService("testJedisPool")
        if not self.pool:
            self.log.info(self.name + ': creating pool...')
            self.pool = JedisPool(JedisPoolConfig(), "127.0.0.1")
            try:
                ServiceRegistry.registerService("testJedisPool", self.pool)
            finally:
                pass
        self.log.info(self.name + ': init...')

    def doGet(self, req, res):
        req_json = req.getParameter("data")
        j = self.pool.getResource()
        try:
            j.rpush('SampleUploadServlet2', req_json)
        finally:
            if j:
                j.close()
        res_json = {'status': 200, 'message': 'success', 'data': None}
        res.setContentType("application/json")
        out = res.getOutputStream()
        print >> out, repr(res_json)
        out.close()
