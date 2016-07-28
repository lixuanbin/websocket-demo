create schema if not exists dataservice;
create table if not exists dataservice.tb_py_servlet (
	id bigint primary key auto_increment,
    className varchar(100) not null unique comment 'python class name',
    requestPath varchar(100) not null unique comment 'http request path, eg. hello.py',
    script text not null comment 'executable python servlet script',
    last_modified_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment 'last modified timestamp',
    status int default 0 comment '-1 for disable, 0 for runnable'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT ignore INTO dataservice.tb_py_servlet(className, requestPath, script, last_modified_time, status) VALUES ('hello','hello.py','from javax.servlet.http import HttpServlet\nclass hello(HttpServlet):\n    def doGet(self, req, res):\n        res.setContentType("text/html");\n        out = res.getOutputStream()\n        print >>out, "<html>"\n        print >>out, "<head><title>Hello World, How are we?</title></head>"\n        print >>out, "<body>Hello World, how are we?"\n        print >>out, "</body>"\n        print >>out, "</html>"\n        out.close()','2016-07-27 03:32:56',0);
