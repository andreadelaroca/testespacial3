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
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        html {
            background: #083c57;
            min-height: 100%;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #083c57;
            min-height: 100vh;
            padding: 25px;
            color: #1f2937;
        }

        .page-wrapper {
            width: 1100px;
            max-width: 100%;
            margin: 0 auto;
            background: #edf2f5;
            border-radius: 18px;
            overflow: hidden;
            box-shadow: 0 10px 30px rgba(0,0,0,.35);
        }

        .test-header {
            background: #083c57;
            color: white;
            padding: 35px 45px;
            position: relative;
            overflow: hidden;
        }

        .test-header::before {
            content: "";
            position: absolute;
            width: 230px;
            height: 230px;
            border-radius: 50%;
            background: #59bfe6;
            opacity: .15;
            bottom: -90px;
            left: -80px;
        }

        .test-header::after {
            content: "";
            position: absolute;
            width: 130px;
            height: 130px;
            border: 5px solid #59bfe6;
            opacity: .25;
            top: 45px;
            right: -40px;
        }

        .test-title {
            position: relative;
            z-index: 2;
            text-align: center;
            font-size: 36px;
            text-transform: uppercase;
            margin-bottom: 18px;
        }

        .subject {
            position: relative;
            z-index: 2;
            font-size: 16px;
            margin-bottom: 18px;
        }

        .subject strong {
            color: #59bfe6;
        }

        .instructions {
            position: relative;
            z-index: 2;
            background: rgba(255,255,255,.12);
            border-left: 5px solid #59bfe6;
            padding: 18px;
            border-radius: 10px;
            line-height: 1.6;
            font-size: 15px;
        }

        .timer-box {
            background: #0097d1;
            color: white;
            padding: 14px 25px;
            border-radius: 999px;
            font-weight: bold;
            width: fit-content;
            margin: 25px auto 0;
            box-shadow: 0 6px 15px rgba(0,0,0,.25);
        }

        .timer-box.danger {
            background: #d9534f;
        }

        .content {
            padding: 35px 45px 45px;
        }

        .alert {
            background: #ffe5e5;
            color: #d9534f;
            border: 1px solid #d9534f;
            padding: 14px;
            border-radius: 10px;
            text-align: center;
            font-weight: bold;
            margin: 25px 45px 0;
        }

        .pregunta-card {
            background: white;
            border: 2px solid #d8edf5;
            border-radius: 16px;
            padding: 25px;
            margin-bottom: 30px;
            box-shadow: 0 5px 15px rgba(0,0,0,.08);
        }

        .question-heading {
            display: inline-block;
            background: #d8edf5;
            color: #006b94;
            padding: 10px 18px;
            border-radius: 999px;
            font-size: 18px;
            font-weight: bold;
            margin-bottom: 20px;
        }

        .imagen-cubos {
            background: #ffffff;
            border: 2px solid #59bfe6;
            border-radius: 14px;
            padding: 18px;
            margin: 15px 0 22px;
            text-align: center;
        }

        .imagen-cubos img {
            max-width: 100%;
            height: auto;
            display: inline-block;
            border-radius: 8px;
        }

        .options-container {
            display: flex;
            flex-wrap: wrap;
            gap: 14px;
            margin-top: 15px;
        }

        .option-label {
            min-width: 90px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 9px;
            background: #d8edf5;
            border: 2px solid #59bfe6;
            border-radius: 10px;
            padding: 13px 20px;
            cursor: pointer;
            font-weight: bold;
            color: #083c57;
            transition: .25s;
        }

        .option-label:hover {
            background: white;
            border-color: #0097d1;
            transform: translateY(-2px);
        }

        .option-label.selected {
            background: #0097d1;
            color: white;
            border-color: #007aa8;
        }

        .option-label input {
            transform: scale(1.2);
            cursor: pointer;
        }

        .actions-bar {
            margin-top: 35px;
        }

        .btn-submit {
            width: 100%;
            border: none;
            background: #0097d1;
            color: white;
            padding: 16px;
            border-radius: 10px;
            font-size: 18px;
            font-weight: bold;
            cursor: pointer;
            transition: .3s;
        }

        .btn-submit:hover {
            background: #007aa8;
            transform: translateY(-2px);
        }

        .empty-state {
            padding: 45px;
            text-align: center;
            color: #d9534f;
            font-weight: bold;
            font-size: 18px;
        }

        @media(max-width: 768px) {
            body {
                padding: 12px;
            }

            .test-header,
            .content {
                padding: 25px 20px;
            }

            .test-title {
                font-size: 26px;
            }

            .options-container {
                flex-direction: column;
            }

            .option-label {
                width: 100%;
            }

            .alert {
                margin: 20px 20px 0;
            }
        }
    </style>
</head>

<body>

<div class="page-wrapper">

    <c:if test="${param.error == 'true'}">
        <div class="alert">
            Error al procesar el test. Contacte con el administrador.
        </div>
    </c:if>

    <c:choose>
        <c:when test="${not empty test}">

            <header class="test-header">
                <h1 class="test-title">
                    <c:out value="${test.nombre}" default="Evaluación Psicométrica"/>
                </h1>

                <div class="subject">
                    Sujeto:
                    <strong>
                        <c:out value="${sujeto.nombres}"/> <c:out value="${sujeto.apellidos}"/>
                    </strong>
                </div>

                <c:if test="${not empty test.instrucciones}">
                    <div class="instructions">
                        <strong>Instrucciones:</strong><br>
                        <c:out value="${test.instrucciones}"/>
                    </div>
                </c:if>

                <div class="timer-box" id="timer-box">
                    Tiempo restante: <span id="reloj">--:--</span>
                </div>
            </header>

            <main class="content">
                <form id="formularioTest" action="${pageContext.request.contextPath}/guardarTest" method="POST">
                    <input type="hidden" name="testId" value="${test.id}">
                    <input type="hidden" name="tiempomaximo" value="${test.tiempoMax}">

                    <c:forEach var="pregunta" items="${test.preguntas}" varStatus="status">
                        <section class="pregunta-card">
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
                        </section>
                    </c:forEach>

                    <div class="actions-bar">
                        <button type="submit" id="finish-button" class="btn-submit">
                            Finalizar Test
                        </button>
                    </div>
                </form>
            </main>

            <script>
                const state = {
                    timerId: null,
                    locked: false,
                    tiempoRestante: 0
                };

                const reloj = document.getElementById("reloj");
                const timerBox = document.getElementById("timer-box");
                const form = document.getElementById("formularioTest");
                const finishBtn = document.getElementById("finish-button");

                document.addEventListener("DOMContentLoaded", function () {
                    iniciarSeleccionVisual();
                    iniciarTemporizador();

                    if (form) {
                        form.addEventListener("submit", function (event) {
                            if (state.locked) {
                                event.preventDefault();
                                return;
                            }

                            state.locked = true;

                            if (state.timerId) {
                                clearInterval(state.timerId);
                            }

                            if (finishBtn) {
                                finishBtn.textContent = "Guardando respuestas...";
                            }
                        });
                    }
                });

                function iniciarSeleccionVisual() {
                    document.querySelectorAll(".option-label input").forEach(function (input) {
                        input.addEventListener("change", function () {
                            const groupName = input.name;

                            document.querySelectorAll("input[name='" + groupName + "']").forEach(function (radio) {
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

                    state.timerId = setInterval(function () {
                        state.tiempoRestante = Math.max(0, state.tiempoRestante - 1);
                        actualizarReloj();

                        if (state.tiempoRestante <= 0) {
                            clearInterval(state.timerId);
                            terminarPorTiempo();
                        }
                    }, 1000);
                }

                function actualizarReloj() {
                    const minutos = Math.floor(state.tiempoRestante / 60).toString().padStart(2, "0");
                    const segundos = (state.tiempoRestante % 60).toString().padStart(2, "0");

                    if (reloj) {
                        reloj.textContent = minutos + ":" + segundos;
                    }

                    if (timerBox && state.tiempoRestante <= 60) {
                        timerBox.classList.add("danger");
                    }
                }

                function terminarPorTiempo() {
                    if (state.locked) {
                        return;
                    }

                    state.locked = true;

                    if (finishBtn) {
                        finishBtn.textContent = "Tiempo agotado - Guardando...";
                    }

                    if (form) {
                        form.submit();
                    }
                }
            </script>

        </c:when>

        <c:otherwise>
            <div class="empty-state">
                No hay ningún test configurado como activo o la sesión es inválida.
            </div>
        </c:otherwise>
    </c:choose>

</div>

</body>
</html>