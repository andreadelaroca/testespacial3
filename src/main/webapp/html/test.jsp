<%@ page isELIgnored="false" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aplicación de Test Psicométrico</title>

    <style>
        * {
            box-sizing: border-box;
        }

        html, body {
            margin: 0;
            padding: 0;
            min-height: 100%;
            font-family: Arial, Helvetica, sans-serif;
            background: #eef3f8;
            color: #1f2937;
        }

        body {
            padding: 24px;
        }

        .test-container {
            width: 100%;
            max-width: 980px;
            margin: 0 auto;
            background: #ffffff;
            border-radius: 18px;
            overflow: hidden;
            box-shadow: 0 18px 45px rgba(15, 23, 42, 0.18);
        }

        .test-header {
            background: linear-gradient(135deg, #0f4c75, #1b6ca8);
            color: white;
            padding: 28px 32px;
            position: relative;
        }

        .test-title {
            margin: 0 0 10px;
            font-size: 28px;
            font-weight: 800;
            letter-spacing: 0.3px;
        }

        .subject-name {
            font-size: 15px;
            opacity: 0.95;
            margin-bottom: 18px;
        }

        .instructions-box {
            background: rgba(255, 255, 255, 0.12);
            border: 1px solid rgba(255, 255, 255, 0.20);
            border-radius: 12px;
            padding: 16px;
            line-height: 1.55;
            font-size: 14px;
        }

        .timer-container {
            position: sticky;
            top: 10px;
            z-index: 50;
            margin: 22px 32px 0 auto;
            width: fit-content;
            background: #dc2626;
            color: white;
            padding: 11px 18px;
            border-radius: 999px;
            font-weight: 800;
            box-shadow: 0 10px 25px rgba(220, 38, 38, 0.25);
        }

        .content {
            padding: 30px 32px 36px;
        }

        .pregunta-card {
            background: #f8fafc;
            border: 1px solid #dbe4ee;
            border-radius: 16px;
            padding: 22px;
            margin-bottom: 26px;
        }

        .question-heading {
            display: inline-block;
            background: #dbeafe;
            color: #1d4ed8;
            padding: 8px 14px;
            border-radius: 999px;
            font-weight: 800;
            margin-bottom: 18px;
        }

        .imagen-cubos {
            width: 100%;
            background: white;
            border: 1px solid #e5e7eb;
            border-radius: 14px;
            padding: 16px;
            margin-bottom: 18px;
            text-align: center;
        }

        .imagen-cubos img {
            max-width: 100%;
            height: auto;
            display: inline-block;
            border-radius: 8px;
        }

        .options-container {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(110px, 1fr));
            gap: 12px;
        }

        .option-label {
            display: flex;
            align-items: center;
            gap: 10px;
            background: white;
            border: 2px solid #dbe4ee;
            border-radius: 12px;
            padding: 13px 14px;
            cursor: pointer;
            font-weight: 700;
            transition: 0.2s ease;
        }

        .option-label:hover {
            border-color: #1b6ca8;
            background: #eff6ff;
            transform: translateY(-1px);
        }

        .option-label input {
            width: 18px;
            height: 18px;
            cursor: pointer;
        }

        .option-label.selected {
            border-color: #1b6ca8;
            background: #dbeafe;
            color: #0f4c75;
        }

        .actions-bar {
            margin-top: 28px;
        }

        .btn-submit {
            width: 100%;
            border: none;
            border-radius: 14px;
            background: #16a34a;
            color: white;
            font-size: 18px;
            font-weight: 800;
            padding: 16px;
            cursor: pointer;
            transition: 0.2s ease;
        }

        .btn-submit:hover {
            background: #15803d;
            transform: translateY(-1px);
        }

        .btn-submit:active {
            transform: scale(0.99);
        }

        .alert {
            margin: 24px 32px 0;
            padding: 15px;
            border-radius: 12px;
            font-weight: 800;
            text-align: center;
        }

        .alert-danger {
            background: #fee2e2;
            color: #991b1b;
            border: 1px solid #fecaca;
        }

        .empty-state {
            padding: 45px;
            text-align: center;
        }

        @media (max-width: 700px) {
            body {
                padding: 12px;
            }

            .test-header,
            .content {
                padding: 22px 18px;
            }

            .test-title {
                font-size: 23px;
            }

            .timer-container {
                margin: 16px 18px 0 auto;
            }

            .pregunta-card {
                padding: 16px;
            }
        }
    </style>
</head>

<body>

<div class="test-container">

    <c:if test="${param.error == 'true'}">
        <div class="alert alert-danger">
            Error al procesar el test. Contacte con el administrador.
        </div>
    </c:if>

    <c:choose>
        <c:when test="${not empty test}">

            <div class="test-header">
                <h1 class="test-title">
                    <c:out value="${test.nombre}" default="Evaluación Psicométrica"/>
                </h1>

                <div class="subject-name">
                    Sujeto:
                    <strong>
                        <c:out value="${sujeto.nombres}"/> <c:out value="${sujeto.apellidos}"/>
                    </strong>
                </div>

                <c:if test="${not empty test.instrucciones}">
                    <div class="instructions-box">
                        <strong>Instrucciones:</strong><br>
                        <c:out value="${test.instrucciones}"/>
                    </div>
                </c:if>
            </div>

            <div class="timer-container" id="timer-box">
                Tiempo restante: <span id="reloj">--:--</span>
            </div>

            <div class="content">
                <form id="formularioTest" action="${pageContext.request.contextPath}/guardarTest" method="POST">
                    <input type="hidden" name="testId" value="${test.id}">
                    <input type="hidden" name="tiempomaximo" value="${test.tiempoMax}">

                    <c:forEach var="pregunta" items="${test.preguntas}" varStatus="status">
                        <div class="pregunta-card">
                            <div class="question-heading">
                                Pregunta ${status.index + 1}
                            </div>

                            <c:if test="${not empty imagenes[pregunta.id]}">
                                <div class="imagen-cubos">
                                    <img src="data:image/png;base64,${imagenes[pregunta.id]}" alt="Figura de la pregunta ${status.index + 1}">
                                </div>
                            </c:if>

                            <div class="options-container">
                                <c:forEach var="opcion" items="${pregunta.opciones}">
                                    <label class="option-label">
                                        <input type="radio" name="respuesta_${pregunta.id}" value="${opcion.id}">
                                        <span>
                                            <c:out value="${opcion.respuesta}"/>
                                        </span>
                                    </label>
                                </c:forEach>
                            </div>
                        </div>
                    </c:forEach>

                    <div class="actions-bar">
                        <button type="submit" id="finish-button" class="btn-submit">
                            Finalizar Test
                        </button>
                    </div>
                </form>
            </div>

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
                    finishBtn: document.getElementById("finish-button")
                };

                document.addEventListener("DOMContentLoaded", init);

                function init() {
                    iniciarSeleccionVisual();
                    iniciarTemporizador();

                    if (elements.form) {
                        elements.form.addEventListener("submit", handleSubmit);
                    }
                }

                function iniciarSeleccionVisual() {
                    document.querySelectorAll(".option-label input").forEach(input => {
                        input.addEventListener("change", () => {
                            const groupName = input.name;

                            document.querySelectorAll("input[name='" + groupName + "']").forEach(radio => {
                                radio.closest(".option-label").classList.remove("selected");
                            });

                            input.closest(".option-label").classList.add("selected");
                        });
                    });
                }

                function iniciarTemporizador() {
                    let rawTime = "${test.tiempoMax}";
                    let tMax = 3.5;

                    if (rawTime && rawTime.trim() !== "") {
                        let parsed = parseFloat(rawTime.replace(",", "."));
                        if (!isNaN(parsed) && parsed > 0) {
                            tMax = parsed;
                        }
                    }

                    state.tiempoRestante = Math.floor(tMax * 60);
                    actualizarReloj();

                    state.timerId = setInterval(() => {
                        state.tiempoRestante = Math.max(0, state.tiempoRestante - 1);
                        actualizarReloj();

                        if (state.tiempoRestante <= 0) {
                            clearInterval(state.timerId);
                            terminarPorTiempo();
                        }
                    }, 1000);
                }

                function actualizarReloj() {
                    const minutes = Math.floor(state.tiempoRestante / 60).toString().padStart(2, "0");
                    const seconds = (state.tiempoRestante % 60).toString().padStart(2, "0");

                    if (elements.timer) {
                        elements.timer.textContent = minutes + ":" + seconds;
                    }

                    if (elements.timerBox && state.tiempoRestante <= 60) {
                        elements.timerBox.style.background = "#991b1b";
                    }
                }

                function terminarPorTiempo() {
                    if (state.locked) return;

                    state.locked = true;

                    if (elements.finishBtn) {
                        elements.finishBtn.textContent = "Tiempo agotado - Guardando...";
                    }

                    if (elements.form) {
                        elements.form.submit();
                    }
                }

                function handleSubmit(event) {
                    if (state.locked) {
                        event.preventDefault();
                        return;
                    }

                    state.locked = true;
                    clearInterval(state.timerId);

                    if (elements.finishBtn) {
                        elements.finishBtn.textContent = "Guardando test...";
                        elements.finishBtn.style.pointerEvents = "none";
                    }
                }
            </script>

        </c:when>

        <c:otherwise>
            <div class="empty-state">
                <div class="alert alert-danger">
                    No hay ningún test configurado como activo o la sesión es inválida.
                </div>
            </div>
        </c:otherwise>
    </c:choose>

</div>

</body>
</html>