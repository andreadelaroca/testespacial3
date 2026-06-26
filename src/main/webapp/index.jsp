<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Verificamos si existe un usuario administrador logueado en la sesión de OpenXava
    String navioxUser = (String) session.getAttribute("naviox_user");

    if (navioxUser != null && !navioxUser.isEmpty()) {
        // SI YA INICIÓ SESIÓN COMO ADMIN: Lo dejamos en el panel de administración
        response.sendRedirect(request.getContextPath() + "/m/SignIn");
    } else {
        // SI ES UN PACIENTE NUEVO: Lo mandamos a tu pantalla de login personalizada
        response.sendRedirect(request.getContextPath() + "/html/login.html");
    }
%>