<%@ page isELIgnored="false" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Test Espacial</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/html/css/style.css">
</head>
<body>
<div class="test-container">
    <header class="test-header">
        <h1>${test.nombre}</h1>
        <div class="instrucciones-box">
            <p>${test.instrucciones}</p>
        </div>

        <c:if test="${!test.estado}">
            <div class="alerta-inactivo">
                <strong>Aviso:</strong> Este test está inactivo. No puedes seleccionar opciones.
            </div>
        </c:if>
    </header>

    <form action="../guardarTest" method="POST">

        <c:forEach var="pregunta" items="${test.preguntas}" varStatus="status">
            <div class="pregunta-card">
                <h3>Pregunta ${status.index + 1}</h3>

                <div class="imagen-container">
                    <img src="data:image/png;base64,${imagenes[pregunta.id]}" alt="Imagen de la pregunta">
                </div>

                <div class="opciones-container">
                    <c:forEach var="opcion" items="${pregunta.opciones}">
                        <label class="opcion-label">
                            <input type="radio"
                                   name="respuesta_${pregunta.id}"
                                   value="${opcion.id}"
                                   <c:if test="${!test.estado}">disabled</c:if> >
                            Opción ${opcion.respuesta}
                        </label>
                    </c:forEach>
                </div>
            </div>
            <hr>
        </c:forEach>

        <c:if test="${test.estado}">
            <button type="submit" class="btn-enviar">Finalizar Test</button>
        </c:if>
    </form>
</div>

</body>
</html>