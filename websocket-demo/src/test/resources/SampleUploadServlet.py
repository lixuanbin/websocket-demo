#!/usr/bin/python
# -*- coding: UTF-8 -*-
import redis
import json
from javax.servlet.http import HttpServlet
from com.duowan.game.service import ServiceRegistry
from java.util import Properties
from com.duowan.game.servlet import MyPyServlet
from org.apache.log4j import Logger
class SampleUploadServlet(HttpServlet):
    def __init__(self):
        self.log = MyPyServlet.log
        self.name = 'SampleUploadServlet'
        pool = ServiceRegistry.getService("testRedisPool")
        if not pool:
            self.log.info(self.name + ': creating pool...')
            pool = redis.ConnectionPool(host='127.0.0.1', port=6379)
            try:
                ServiceRegistry.registerService("testRedisPool", pool)
            finally:
                pass
        self.r = redis.Redis(connection_pool=pool)
        self.log.info(self.name + ': init...')

    def doGet(self, req, res):
        req_json = req.getParameter("data")
        self.r.rpush('SampleUploadServlet', req_json)
        res_json = {'status': 200, 'message': 'success', 'data': None}
        res.setContentType("application/json")
        out = res.getOutputStream()
        print >> out, repr(res_json)
        out.close()
