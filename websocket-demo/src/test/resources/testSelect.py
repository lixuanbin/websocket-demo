from javax.servlet.http import HttpServlet
from com.duowan.game.dao import DaoFactory
from com.duowan.game.dao import PyServletDao
from org.apache.log4j import Logger
from com.ziclix.python.sql import zxJDBC
class testSelect(HttpServlet):
    def __init__(self):
        self.log = PyServletDao.log
        self.name = 'testSelect'
        self.log.info(self.name + ': testSelect init...')
    def getConnection(self, jdbc_url, driverName, user, pwd):
        try:
            dbConn = zxJDBC.connect(jdbc_url, user, pwd, driverName)
        except zxJDBC.DatabaseError, msg:
            print msg
        return dbConn
    def doGet(self, req, res):
        path = DaoFactory.getDao('PyServletDao').findByClassName('hello').getRequestPath()
        res.setContentType("text/plain");
        out = res.getOutputStream()
        print >>out, path
        out.close()