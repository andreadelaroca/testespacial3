<%@ page import="java.util.List" %>
<%@ page import="org.example.testespacial3.modelo.Pregunta" %>
<%@ page import="org.example.testespacial3.modelo.Opcion" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Recuperamos las listas que nos mandó el Servlet
    List<Pregunta> preguntas = (List<Pregunta>) request.getAttribute("preguntas");
    Map<Integer, String> imagenes = (Map<Integer, String>) request.getAttribute("imagenes");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Test de Habilidad Espacial</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f7f6; padding: 20px; }
        .contenedor { max-width: 800px; margin: auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }
        .pregunta-card { border: 1px solid #e0e0e0; padding: 20px; margin-bottom: 25px; border-radius: 8px; background: #fafafa; }
        img { max-width: 100%; height: auto; border: 1px solid #ccc; margin-bottom: 15px; border-radius: 4px; }
        .opcion { font-size: 16px; margin-bottom: 10px; display: block; cursor: pointer; }
        .btn-enviar { background-color: #28a745; color: white; border: none; padding: 12px 25px; font-size: 18px; border-radius: 5px; cursor: pointer; width: 100%; }
        .btn-enviar:hover { background-color: #218838; }
    </style>
</head>
<body>

<div class="contenedor">
    <h1 style="text-align: center;">Test de Habilidad Espacial</h1>
    <p style="text-align: center;">Responde todas las preguntas basándote en las imágenes.</p>

    <form action="<%=request.getContextPath()%>/guardarTest" method="POST">

        <% if (preguntas != null && !preguntas.isEmpty()) {
            int numero = 1;
            for (Pregunta p : preguntas) { %>
        <div class="pregunta-card">
            <h3>Pregunta <%=numero%></h3>

            <%-- Dibuja la imagen si existe --%>
            <% if (imagenes.containsKey(p.getId())) { %>
            <img src="<%=imagenes.get(p.getId())%>" alt="Imagen del test"/>
            <% } %>

            <div style="margin-top: 15px;">
                <%-- Recorremos las opciones de ESTA pregunta --%>
                <% for (Opcion o : p.getOpciones()) { %>
                <label class="opcion">
                    <%-- El name agrupa las opciones por pregunta, el value es el ID de la opción que elegirá --%>
                    <input type="radio" name="preg_<%=p.getId()%>" value="<%=o.getId()%>" required>
                    Opción <%=o.getRespuesta()%>
                </label>
                <% } %>
            </div>
        </div>
        <%      numero++;
        }
        } else { %>
        <p style="text-align: center; color: red;">No hay preguntas configuradas en la base de datos actualmente.</p>
        <% } %>

        <button type="submit" class="btn-enviar">Finalizar y Evaluar</button>
    </form>
</div>

</body>
</html>