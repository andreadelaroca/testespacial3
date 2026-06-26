<%@ page isELIgnored="false" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Test de Inteligencia Espacial</title>
    <style>
        body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #1a365d; color: #ffffff; margin: 0; padding: 20px; }
        .container { max-width: 850px; margin: auto; background: #2d3748; padding: 30px; border-radius: 8px; box-shadow: 0 4px 15px rgba(0,0,0,0.3); }
        .header { border-bottom: 2px solid #4299e1; padding-bottom: 15px; margin-bottom: 25px; }
        .header h2 { margin: 0; color: #63b3ed; font-size: 28px; }
        .instructions { background: #4a5568; padding: 15px; border-radius: 6px; margin-bottom: 25px; line-height: 1.6; font-size: 14px; border-left: 5px solid #4299e1; }
        .timer-container { font-size: 20px; font-weight: bold; color: #fc8181; margin-bottom: 20px; text-align: right; }
        .pregunta-card { background: #3d4a5d; margin-bottom: 30px; padding: 20px; border-radius: 6px; box-shadow: 0 2px 8px rgba(0,0,0,0.15); }
        .pregunta-card h4 { margin-top: 0; color: #90cdf4; font-size: 18px; border-bottom: 1px solid #4a5568; padding-bottom: 8px; }
        .imagen-wrapper { background: #ffffff; padding: 15px; border-radius: 6px; display: inline-block; margin: 15px 0; }
        .imagen-wrapper img { max-width: 100%; height: auto; display: block; }
        .opciones-container { display: flex; flex-wrap: wrap; gap: 15px; margin-top: 15px; background: #2d3748; padding: 12px; border-radius: 4px; }
        .opcion-label { display: flex; align-items: center; gap: 8px; cursor: pointer; padding: 8px 12px; border-radius: 4px; background: #4a5568; transition: background 0.2s; }
        .opcion-label:hover { background: #4299e1; }
        .opcion-label input { cursor: pointer; transform: scale(1.2); }
        .btn-submit { display: block; width: 100%; padding: 14px; font-size: 18px; font-weight: bold; color: #fff; background-color: #38a169; border: none; border-radius: 6px; cursor: pointer; transition: background 0.3s; margin-top: 20px; }
        .btn-submit:hover { background-color: #2f855a; }
        .alert { padding: 15px; border-radius: 6px; margin-bottom: 25px; text-align: center; font-weight: bold; font-size: 16px; }
        .alert-success { background-color: #c6f6d5; color: #22543d; border: 1px solid #9ae6b4;}
        .alert-danger { background-color: #fed7d7; color: #742a2a; border: 1px solid #feb2b2;}
    </style>
</head>
<body>

<div class="container">
    <c:if test="${param.success == 'true'}">
        <div class="alert alert-success">¡El test se ha procesado y guardado con éxito!</div>
    </c:if>
    <c:if test="${param.error == 'true'}">
        <div class="alert alert-danger">Error al guardar el test. Verifique la base de datos.</div>
    </c:if>

    <c:choose>
        <c:when test="${not empty test}">

            <div class="header">
                <h2>${test.nombre != null ? test.nombre : 'Evaluación Espacial'}</h2>
            </div>

            <div class="instructions">
                <strong>Sujeto:</strong> ${sujeto.nombres} ${sujeto.apellidos} <br><br>
                <strong>Instrucciones:</strong> ${test.instrucciones}
            </div>

            <div class="timer-container">
                Tiempo restante: <span id="reloj">--:--</span>
            </div>

            <form id="formularioTest" action="${pageContext.request.contextPath}/guardarTest" method="post">
                <input type="hidden" name="testId" value="${test.id}">
                <input type="hidden" name="tiempomaximo" value="${test.tiempoMax}">

                <c:forEach var="pregunta" items="${test.preguntas}" varStatus="status">
                    <div class="pregunta-card">

                        <h4>Pregunta ${status.index + 1}</h4>

                        <c:if test="${not empty imagenes[pregunta.id]}">
                            <div class="imagen-wrapper">
                                <img src="data:image/png;base64,${imagenes[pregunta.id]}" alt="Estímulo visual">
                            </div>
                        </c:if>

                        <div class="opciones-container">
                            <c:forEach var="opcion" items="${pregunta.opciones}">
                                <label class="opcion-label">
                                    <input type="radio" name="respuesta_${pregunta.id}" value="${opcion.id}" required>
                                        ${opcion.respuesta}
                                </label>
                            </c:forEach>
                        </div>
                    </div>
                </c:forEach>

                <button type="submit" class="btn-submit">Finalizar y Guardar Test</button>
            </form>

            <script>
                // Temporizador robusto leyendo la variable del modelo
                let tMaxStr = '${test.tiempoMax}';
                let tMax = parseFloat(tMaxStr.replace(',', '.')); // Soporte por si usa coma decimal
                if (isNaN(tMax) || tMax <= 0) tMax = 3.5; // Por defecto

                let tiempoRestante = Math.floor(tMax * 60);

                function actualizarTemporizador() {
                    let minutos = Math.floor(tiempoRestante / 60);
                    let segundos = tiempoRestante % 60;

                    minutos = minutos < 10 ? "0" + minutos : minutos;
                    segundos = segundos < 10 ? "0" + segundos : segundos;

                    const relojEl = document.getElementById("reloj");
                    if (relojEl) relojEl.innerHTML = minutos + ":" + segundos;

                    if (tiempoRestante > 0) {
                        tiempoRestante--;
                    } else {
                        clearInterval(intervalo);
                        const form = document.getElementById("formularioTest");
                        if (form) {
                            form.submit(); // Envío automático si se agota
                        }
                    }
                }

                let intervalo = setInterval(actualizarTemporizador, 1000);
                actualizarTemporizador();
            </script>

        </c:when>

        <c:otherwise>
            <div class="alert alert-danger" style="margin-top: 50px;">
                No hay ningún test configurado como activo en el sistema o su sesión ha expirado.
            </div>
        </c:otherwise>
    </c:choose>

</div>
</body>
</html>