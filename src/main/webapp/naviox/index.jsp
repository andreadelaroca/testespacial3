<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Evitamos que el navegador guarde en caché la redirección
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    String navioxUser = (String) request.getSession().getAttribute("naviox.user");

    if (navioxUser != null && !navioxUser.trim().isEmpty()) {
        // Si el admin ya está logueado, entra directo
        response.sendRedirect(request.getContextPath() + "/m/Test");
    } else {
        // Si no hay sesión, salta de inmediato a tu login funcional
        response.sendRedirect(request.getContextPath() + "/html/login.html");
    }
%>