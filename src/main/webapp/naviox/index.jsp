<%
    String xavaUser = (String) request.getSession().getAttribute("xava.user");

    if (xavaUser != null && !xavaUser.trim().isEmpty()) {
        response.sendRedirect(request.getContextPath() + "/m/Test");
    } else {
        response.sendRedirect(request.getContextPath() + "/html/login.html");
    }
%>