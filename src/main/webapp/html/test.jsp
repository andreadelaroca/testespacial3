<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="javax.persistence.EntityManager" %>
<%@ page import="org.openxava.jpa.XPersistence" %>
<%@ page import="org.example.testespacial3.modelo.Test" %>
<%@ page import="org.example.testespacial3.modelo.Pregunta" %>
<%@ page import="org.example.testespacial3.modelo.Opcion" %>

<%
    // Recuperamos el ID del test desde la URL o el Request
    String testIdStr = request.getParameter("testId");
    Test testActual = null;
    int tiempoMaximo = 30; // Tiempo por defecto
    List<Pregunta> preguntas = null;

    if (testIdStr != null && !testIdStr.isEmpty()) {
        try {
            EntityManager em = XPersistence.getManager();
            int testId = Integer.parseInt(testIdStr);
            testActual = em.find(Test.class, testId);

            if (testActual != null) {
                // Extraer el tiempo de forma segura según tu modelo
                try {
                    Object tMaxObj = testActual.getClass().getMethod("getTiempomax").invoke(testActual);
                    if (tMaxObj instanceof Number) tiempoMaximo = ((Number) tMaxObj).intValue();
                    else if (tMaxObj instanceof String) tiempoMaximo = Integer.parseInt((String) tMaxObj);
                } catch (Exception ignored) {}

                // Cargar las preguntas asociadas
                preguntas = em.createQuery("SELECT p FROM Pregunta p WHERE p.test.id = :testId", Pregunta.class)
                        .setParameter("testId", testId)
                        .getResultList();

                // Guardar hora de inicio en la sesión
                if(session.getAttribute("horaInicio") == null) {
                    session.setAttribute("horaInicio", java.time.LocalTime.now());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Evaluación Espacial</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f6f9; color: #333; margin: 0; padding: 20px; }
        .container { max-width: 800px; margin: auto; background: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
        .header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #007bff; padding-bottom: 10px; margin-bottom: 20px; }
        .timer { font-size: 24px; font-weight: bold; color: #dc3545; }
        .pregunta-card { margin-bottom: 25px; padding: 15px; border: 1px solid #e9ecef; border-radius: 5px; background: #fafafa; }
        .opcion { display: block; margin: 10px 0; }
        .btn-submit { display: block; width: 100%; padding: 12px; font-size: 16px; font-weight: bold; color: #fff; background-color: #28a745; border: none; border-radius: 5px; cursor: pointer; transition: 0.3s; }
        .btn-submit:hover { background-color: #218838; }
        .alert { padding: 15px; border-radius: 5px; margin-bottom: 20px; text-align: center; font-weight: bold; }
        .alert-success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .alert-danger { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
    </style>
</head>
<body>

<div class="container">
    <% if ("true".equals(request.getParameter("success"))) { %>
    <div class="alert alert-success">¡El test se ha guardado correctamente!</div>
    <% } %>

    <% if ("true".equals(request.getParameter("error"))) { %>
    <div class="alert alert-danger">Ha ocurrido un error al procesar el test. Por favor, contacte con el administrador.</div>
    <% } %>

    <% if (testActual != null && preguntas != null) { %>
    <div class="header">
        <h2><%= testActual.getNombre() != null ? testActual.getNombre() : "Evaluación" %></h2>
        <div class="timer" id="reloj">--:--</div>
    </div>

    <form id="formularioTest" action="<%= request.getContextPath() %>/guardarTest" method="post">
        <input type="hidden" name="testId" value="<%= testIdStr %>">
        <input type="hidden" name="tiempomaximo" value="<%= tiempoMaximo %>">

        <% for (Pregunta p : preguntas) { %>
        <div class="pregunta-card">
            <h4><%= p.getPregunta() != null ? p.getPregunta() : "¿Pregunta sin título?" %></h4>

            <%
                List<Opcion> opciones = XPersistence.getManager()
                        .createQuery("SELECT o FROM Opcion o WHERE o.pregunta.id = :pregId", Opcion.class)
                        .setParameter("pregId", p.getId())
                        .getResultList();

                for (Opcion o : opciones) {
            %>
            <label class="opcion">
                <input type="radio" name="respuesta_<%= p.getId() %>" value="<%= o.getId() %>" required>
                <%= o.getRespuesta() %>
            </label>
            <% } %>
        </div>
        <% } %>

        <button type="submit" class="btn-submit">Finalizar y Enviar Test</button>
    </form>
    <% } else if (request.getParameter("success") == null && request.getParameter("error") == null) { %>
    <div class="alert alert-danger">No se ha encontrado el test solicitado o no hay preguntas disponibles.</div>
    <% } %>
</div>

<script>
    // Recuperar el tiempo máximo del atributo en Java (en minutos) y pasarlo a segundos
    let tiempoRestante = <%= tiempoMaximo %> * 60;

    function actualizarTemporizador() {
        let minutos = Math.floor(tiempoRestante / 60);
        let segundos = tiempoRestante % 60;

        minutos = minutos < 10 ? "0" + minutos : minutos;
        segundos = segundos < 10 ? "0" + segundos : segundos;

        const relojEl = document.getElementById("reloj");
        if(relojEl) {
            relojEl.innerHTML = minutos + ":" + segundos;
        }

        if (tiempoRestante > 0) {
            tiempoRestante--;
        } else {
            // Cuando llega a 0, deshabilitamos el botón y enviamos el formulario obligatoriamente
            clearInterval(intervalo);
            const form = document.getElementById("formularioTest");
            if(form) {
                form.submit();
            }
        }
    }

    // Iniciar el reloj si existe el test
    <% if (testActual != null) { %>
    let intervalo = setInterval(actualizarTemporizador, 1000);
    actualizarTemporizador();
    <% } %>
</script>

</body>
</html>