#!/usr/bin/python
# -*- coding: UTF-8 -*-
import requests
from com.ziclix.python.sql import zxJDBC
class FundB:
    def __init__(self):
        print 'FundB init...'
    def getConnection(self, jdbc_url, driverName, user, pwd):
        try:
            dbConn = zxJDBC.connect(jdbc_url, user, pwd, driverName)
        except zxJDBC.DatabaseError, msg:
            print msg
        return dbConn
    def process(self, millis):
        fundBPath = 'https://api.github.com/user'
        r=requests.get(fundBPath)
        url, user, pwd, driver = "jdbc:mysql://localhost/dataservice", 'root' ,'ben', "com.mysql.jdbc.Driver"
        conn = self.getConnection(url, driver, user, pwd)
        c = conn.cursor()
        c.execute("insert into dataservice.py_script_heartbeat(beat,demo) values (%d,'%s')" % (millis, r.text))
        conn.commit()
        c.close()
        conn.close()
        return 'success'