<%@ page isELIgnored="false" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Aplicación de Test Psicométrico</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f7fafc;
            margin: 0;
            padding: 20px;
            color: #2d3748;
        }
        .test-container {
            max-width: 800px;
            margin: 0 auto;
            background: #ffffff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            position: relative;
        }
        .test-header {
            border-bottom: 2px solid #edf2f7;
            padding-bottom: 20px;
            margin-bottom: 20px;
        }
        /* ESTILO DEL TEMPORIZADOR DINÁMICO */
        .timer-container {
            position: absolute;
            top: 30px;
            right: 30px;
            background-color: #2b6cb0;
            color: white;
            padding: 10px 20px;
            border-radius: 20px;
            font-weight: bold;
            font-size: 1.1em;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            transition: all 0.3s ease;
        }
        .instructions-box {
            background-color: #ebf8ff;
            border-left: 4px solid #3182ce;
            padding: 15px;
            margin: 20px 0;
            border-radius: 4px;
        }

        /* ESTILOS DE LAS PREGUNTAS */
        .pregunta-card {
            background: #f7fafc;
            border: 1px solid #e2e8f0;
            padding: 20px;
            margin-bottom: 20px;
            border-radius: 6px;
            transition: opacity 0.3s ease;
        }
        .question-heading {
            font-size: 1.1em;
            font-weight: 600;
            margin-bottom: 15px;
            color: #2b6cb0;
        }
        .imagen-cubos img {
            max-width: 100%;
            height: auto;
            border-radius: 4px;
            border: 1px solid #cbd5e0;
            margin-bottom: 15px;
        }
        .options-container {
            display: flex;
            flex-direction: row;
            flex-wrap: wrap;
            gap: 15px;
        }
        .option-label {
            display: flex;
            align-items: center;
            padding: 10px 15px;
            background: #ffffff;
            border: 1px solid #e2e8f0;
            border-radius: 4px;
            cursor: pointer;
            transition: background 0.2s;
        }
        .option-label:hover { background: #edf2f7; }
        .option-label input {
            transform: scale(1.2);
            margin-right: 10px;
            cursor: pointer;
        }

        /* BOTONES */
        .actions-bar {
            margin-top: 30px;
            display: flex;
            justify-content: center;
        }
        .btn-submit {
            background-color: #3182ce;
            color: white;
            padding: 14px 30px;
            border: none;
            border-radius: 6px;
            font-weight: bold;
            cursor: pointer;
            font-size: 1.1em;
            transition: background 0.2s;
            width: 100%;
        }
        .btn-submit:hover { background-color: #2b6cb0; }
        .alert { padding: 15px; border-radius: 5px; margin-bottom: 20px; text-align: center; font-weight: bold; }
        .alert-danger { background-color: #fed7d7; color: #9b2c2c; border: 1px solid #fc8181; }
    </style>
</head>
<body>

<div class="test-container">

    <c:if test="${param.error == 'true'}">
        <div class="alert alert-danger">Error al procesar el test. Por favor, contacte con el administrador.</div>
    </c:if>

    <c:choose>
        <c:when test="${not empty test}">
            <div class="test-header">
                <h2 style="margin-top: 0; color: #2d3748;">${test.nombre != null ? test.nombre : 'Evaluación Psicométrica'}</h2>
                <div style="font-weight: 500; font-size: 1.1em; color: #4a5568;">
                    Sujeto: ${sujeto.nombres} ${sujeto.apellidos}
                </div>

                <c:if test="${not empty test.instrucciones}">
                    <div class="instructions-box">
                        <strong>Instrucciones:</strong><br>
                            ${test.instrucciones}
                    </div>
                </c:if>

                <div class="timer-container" id="timer-box">
                    Tiempo: <span id="reloj">--:--</span>
                </div>
            </div>

            <form id="formularioTest" action="${pageContext.request.contextPath}/guardarTest" method="POST">
                <input type="hidden" name="testId" value="${test.id}">
                <input type="hidden" name="tiempomaximo" value="${test.tiempoMax}">

                <div id="preguntas-render-area">
                    <c:forEach var="pregunta" items="${test.preguntas}" varStatus="status">
                        <div class="pregunta-card">
                            <div class="question-heading">
                                Pregunta ${status.index + 1}
                            </div>

                            <c:if test="${not empty imagenes[pregunta.id]}">
                                <div class="imagen-cubos">
                                    <img src="data:image/png;base64,${imagenes[pregunta.id]}" alt="Figura de la pregunta">
                                </div>
                            </c:if>

                            <div class="options-container">
                                <c:forEach var="opcion" items="${pregunta.opciones}">
                                    <label class="option-label">
                                        <input type="radio" name="respuesta_${pregunta.id}" value="${opcion.id}">
                                        <span class="option-text">${opcion.respuesta}</span>
                                    </label>
                                </c:forEach>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <div class="actions-bar">
                    <button type="submit" id="finish-button" class="btn-submit">Finalizar y Guardar Test</button>
                </div>
            </form>

            <script>
                const state = {
                    timerId: null,
                    locked: false,
                    tiempoRestante: 0
                };

                const elements = {
                    timer: document.getElementById("reloj"),
                    timerBox: document.getElementById("timer-box"),
                    form: document.getElementById("formularioTest"),
                    finishBtn: document.getElementById("finish-button"),
                    questions: document.querySelectorAll('.pregunta-card')
                };

                document.addEventListener("DOMContentLoaded", init);

                function init() {
                    if (elements.form) {
                        elements.form.addEventListener("submit", handleManualSubmit);
                    }

                    // Parseo seguro del tiempo (soporta comas o puntos)
                    let tMaxStr = '${test.tiempoMax}';
                    let tMax = 3.5; // Respaldo 3:30 min
                    if (tMaxStr && tMaxStr.trim() !== '') {
                        let parsed = parseFloat(tMaxStr.replace(',', '.'));
                        if (!isNaN(parsed) && parsed > 0) {
                            tMax = parsed;
                        }
                    }

                    state.tiempoRestante = Math.floor(tMax * 60);

                    updateTimerDisplay();
                    configureTimer(true);
                }

                function configureTimer(active) {
                    if (state.timerId) {
                        clearInterval(state.timerId);
                        state.timerId = null;
                    }
                    if (!active) return;

                    state.timerId = setInterval(() => {
                        state.tiempoRestante = Math.max(0, state.tiempoRestante - 1);
                        updateTimerDisplay();

                        if (state.tiempoRestante === 0) {
                            clearInterval(state.timerId);
                            state.timerId = null;
                            handleTimeUp();
                        }
                    }, 1000);
                }

                function updateTimerDisplay() {
                    if (!elements.timer) return;

                    const minutes = Math.floor(state.tiempoRestante / 60).toString().padStart(2, "0");
                    const seconds = (state.tiempoRestante % 60).toString().padStart(2, "0");

                    elements.timer.textContent = `\${minutes}:\${seconds}`;

                    // Alerta visual en el último minuto
                    if (state.tiempoRestante <= 60 && state.tiempoRestante > 0) {
                        elements.timerBox.style.backgroundColor = "#e53e3e"; // Rojo
                    }
                }

                function handleTimeUp() {
                    if (state.locked) return;

                    if (elements.timerBox) {
                        elements.timerBox.style.backgroundColor = "#9b2c2c";
                    }

                    if (elements.finishBtn) {
                        elements.finishBtn.innerHTML = "Tiempo Agotado - Guardando...";
                        elements.finishBtn.style.backgroundColor = "#9b2c2c";
                    }

                    // Bloqueo sin 'disabled' para conservar los datos
                    elements.questions.forEach(card => {
                        card.style.pointerEvents = "none";
                        card.style.opacity = "0.6";
                    });

                    alert("¡El tiempo se ha agotado! Tus respuestas serán guardadas y el test finalizará.");

                    if (elements.form) {
                        state.locked = true;
                        elements.form.submit();
                    }
                }

                function handleManualSubmit(event) {
                    if (state.locked) {
                        event.preventDefault();
                        return;
                    }
                    state.locked = true;
                    configureTimer(false);

                    if (elements.finishBtn) {
                        elements.finishBtn.innerHTML = "Procesando resultados...";
                        elements.finishBtn.style.pointerEvents = "none";
                    }
                }
            </script>
        </c:when>

        <c:otherwise>
            <div class="alert alert-danger" style="margin-top: 50px;">
                No hay ningún test configurado como activo en el sistema o la sesión es inválida.
            </div>
        </c:otherwise>
    </c:choose>
</div>

</body>
</html>