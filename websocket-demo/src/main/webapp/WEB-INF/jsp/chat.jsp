<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Web Socket Demo by lixuanbin.github.io</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/bootstrap.css" />
</head>
<body>${message}
    <div align="center">
    	<h3>Hello, <%=request.getSession(true).getAttribute("user")%>!</h3>
    	<input type="hidden" id='user' name='user' value='<%=request.getSession(true).getAttribute("user")%>'/>
        <textarea id='msg' rows="4" cols="50"></textarea>
        <input type="button" onclick="sendMessage();" value="发送" />
        <br/><br/>
        <div class="jumbotron" id="responseContent">
			<h3>Message From Server: </h3>
		</div>
        <br/><a href="/index.jsp">Back To Index</a>
    </div>
</body>
<script src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script src="<%=request.getContextPath()%>/js/bootstrap.js"></script>
<script>
var ws = null;
function startServer() {
    var url = "ws://<%=request.getServerName() + ":" + request.getServerPort()%><%=request.getContextPath()%>/echo.ws";
    console.log(url);
    try {
    	if ('WebSocket' in window) {
            ws = new WebSocket(url);
        } else if ('MozWebSocket' in window) {
            ws = new MozWebSocket(url);
        } else {
            alert('WebSocket is not supported by this browser.');
            return;
        }
        ws.onopen = function() {
            console.log(Date() + " connect success!");
        };
        ws.onmessage = function(event) {
            console.log(Date() + " receive message:" + event.data);
            $("#responseContent").append("<p>" + event.data + "</p><br/>");
        };
        ws.onclose = function() {
            alert(Date() + " close connect..");
        };    	
    } catch (exception) {
    	console.log(exception);
    }
}

function sendMessage() {
    var txtMsg = $("#msg").val();
    if (ws != null && txtMsg != '') {
        ws.send(txtMsg);
    }
}
$(document).ready(function() {
	startServer();
});
</script>
</html>
