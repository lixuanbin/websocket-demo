<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>Web Socket Demo by lixuanbin.github.io</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/bootstrap.css" />
</head>
<body>
	<br>
	<div style="text-align:center">
		<h2>
			This is A Web Socket Demo<br> <br>
		</h2>
		<form action="chat.do">
			<input type="text" id='user' name='user' />
			<input type="submit" value='ok'/>
		</form>

	</div>
</body>
<script src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script src="<%=request.getContextPath()%>/js/bootstrap.js"></script>
</html>