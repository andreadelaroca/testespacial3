<%Servlets.setCharacterEncoding(request, response);%>

<%@include file="../xava/imports.jsp"%>

<%@page import="org.openxava.web.servlets.Servlets"%>

<%
String app = request.getParameter("application");
if (app == null || app.isBlank()) {
	app = "testespacial3";
}
request.getSession().setAttribute("naviox.originalURL", request.getContextPath() + "/m/Usuario");
%>

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<base href="<%=request.getContextPath()%>/">
	<title>OpenXava</title>
</head>
<body>
	<div id="sign_in_box">
		<jsp:include page='<%="../xava/module.jsp?application=" + app + "&module=SignIn"%>'/>
	</div>
</body>
</html>