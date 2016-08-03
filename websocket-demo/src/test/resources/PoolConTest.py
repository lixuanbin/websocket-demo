#!/usr/bin/python
# -*- coding: UTF-8 -*-
from com.duowan.game.dao import DataSourceRegistry
from java.util import Properties
from org.apache.log4j import Logger
from com.ziclix.python.sql import PyConnection
class PoolConTest:
    def __init__(self):
        self.name = 'PoolConTest'
        self.log = Logger.getLogger(self.name)
        ds = DataSourceRegistry.getDataSource('localSource')
        if not ds:
            props = Properties()
            props.put("url", "jdbc:mysql://localhost/dataservice")
            props.put("username", "root")
            props.put("driverClassName", "com.mysql.jdbc.Driver")
            props.put("password", "ben")
            props.put("initialSize", "1")
            props.put("minIdle", "5")
            props.put("maxActive", "10")
            try:
                DataSourceRegistry.registerDataSource('localSource', props)
            finally:
                ds = DataSourceRegistry.getDataSource('localSource')
        self.ds = ds
        self.log.info(self.name + ': init...')
    def process(self, millis):
        conn = PyConnection(self.ds.getConnection())
        c = conn.cursor()
        try:
            c.execute("insert into dataservice.py_script_heartbeat(beat,demo) values (%d,'%s')" % (millis, 'nonse'))
            conn.commit()
        finally:
            if c: c.close()
            if conn: conn.close()
        #self.log.info(self.name + ': success')
        return 'success'
