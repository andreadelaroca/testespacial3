package org.example.testespacial3.actions;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openxava.jpa.XPersistence;
import org.example.testespacial3.modelo.DetalleAplicacion;
import org.example.testespacial3.modelo.Test;
import org.example.testespacial3.modelo.Sujeto;
import org.example.testespacial3.modelo.Respuesta;
import org.example.testespacial3.modelo.Opcion;
import org.example.testespacial3.modelo.Pregunta;

@WebServlet("/guardarTest")
public class GuardarTest extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EntityManager em = XPersistence.getManager();
        EntityTransaction tx = em.getTransaction();

        try {
            // 1. Validamos que venga un Test en el formulario
            String testIdStr = request.getParameter("testId");
            if (testIdStr == null || testIdStr.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/html/test.jsp?error=true");
                return;
            }
            int testId = Integer.parseInt(testIdStr);
            Test test = em.find(Test.class, testId);

            // 2. CORRECCIÓN CRÍTICA: Buscamos al Sujeto usando el 'sujetoId' de la sesión
            Integer sujetoId = (Integer) request.getSession().getAttribute("sujetoId");
            if (sujetoId == null) {
                // Si expiró la sesión, lo mandamos al index o login
                response.sendRedirect(request.getContextPath() + "/html/login.html?error=sesion_expirada");
                return;
            }
            Sujeto sujeto = em.find(Sujeto.class, sujetoId);

            // 3. Capturamos el Tiempo Máximo
            String tiempoMaximoStr = request.getParameter("tiempomaximo");
            int tiempoMaximoMinutos = 30;
            try {
                tiempoMaximoMinutos = Integer.parseInt(tiempoMaximoStr);
            } catch (Exception e) {
                try {
                    Object tMaxObj = test.getClass().getMethod("getTiempomax").invoke(test);
                    if (tMaxObj instanceof Number) tiempoMaximoMinutos = ((Number) tMaxObj).intValue();
                    else if (tMaxObj instanceof String) tiempoMaximoMinutos = Integer.parseInt((String) tMaxObj);
                } catch (Exception ignored) {}
            }

            // 4. Iniciamos la transacción con la base de datos
            tx.begin();

            DetalleAplicacion detalle = new DetalleAplicacion();
            detalle.setTest(test);
            detalle.setSujeto(sujeto);
            detalle.setFecha(new Date());

            LocalTime horaInicio = (LocalTime) request.getSession().getAttribute("horaInicio");
            if (horaInicio == null) {
                horaInicio = LocalTime.now().minusMinutes(tiempoMaximoMinutos);
            }
            detalle.setHoraInicio(horaInicio);
            detalle.setHoraFin(LocalTime.now());

            List<Respuesta> respuestasList = new ArrayList<>();
            int aciertos = 0;

            // 5. Contamos el total de preguntas que tiene el examen en la BD
            Long totalPreguntas = 0L;
            try {
                totalPreguntas = (Long) em.createQuery("SELECT COUNT(p) FROM Pregunta p WHERE p.test.id = :tId")
                        .setParameter("tId", testId).getSingleResult();
            } catch (Exception ex) {
                // Alternativa por si el mapeo de la relación cambia de nombre
                totalPreguntas = (Long) em.createQuery("SELECT COUNT(p) FROM Pregunta p WHERE p.testid = :tId")
                        .setParameter("tId", testId).getSingleResult();
            }

            // 6. Recorremos lo que el sujeto sí contestó
            Map<String, String[]> parameterMap = request.getParameterMap();
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String paramName = entry.getKey();
                if (paramName.startsWith("respuesta_")) {
                    String opcionIdStr = entry.getValue()[0];
                    if (opcionIdStr != null && !opcionIdStr.isEmpty()) {

                        String preguntaIdStr = paramName.replace("respuesta_", "");
                        int preguntaId = Integer.parseInt(preguntaIdStr);
                        int opcionId = Integer.parseInt(opcionIdStr);

                        Pregunta pregunta = em.find(Pregunta.class, preguntaId);
                        Opcion opcion = em.find(Opcion.class, opcionId);

                        Respuesta respuesta = new Respuesta();
                        respuesta.setDetalleAplicacion(detalle);

                        // Reflection seguro para setear Opción y Pregunta
                        try {
                            respuesta.getClass().getMethod("setOpcion", int.class).invoke(respuesta, opcionId);
                        } catch (Exception e1) {
                            try { respuesta.getClass().getMethod("setOpcion", Opcion.class).invoke(respuesta, opcion); } catch (Exception e2){}
                        }
                        try {
                            respuesta.getClass().getMethod("setPregunta", Pregunta.class).invoke(respuesta, pregunta);
                        } catch (Exception ignored) {}
                        try {
                            respuesta.getClass().getMethod("setPreguntaid", int.class).invoke(respuesta, preguntaId);
                        } catch (Exception ignored) {}

                        respuestasList.add(respuesta);

                        // Si la que contestó es la correcta, suma un acierto
                        if (opcion != null && opcion.isAcierto()) {
                            aciertos++;
                        }
                    }
                }
            }

            // 7. CÁLCULO ESTRELLA: Las preguntas ignoradas o contestadas mal se vuelven desaciertos
            int desaciertos = totalPreguntas.intValue() - aciertos;

            detalle.setAciertos(aciertos);
            detalle.setDesaciertos(desaciertos);
            detalle.setRespuestas(respuestasList);

            // 8. Guardamos toda la calificación en cascada
            em.persist(detalle);
            tx.commit();

            // 9. Cerramos la sesión del usuario para impedir que retroceda la página y envíe de nuevo el test
            request.getSession().invalidate();

            // 10. Pintamos una pantalla limpia de éxito
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'><title>Test Finalizado</title></head>");
            response.getWriter().println("<body style='background-color: #1a365d; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; font-family: Arial, sans-serif;'>");
            response.getWriter().println("<div style='background: white; padding: 40px; border-radius: 8px; text-align: center; box-shadow: 0 4px 15px rgba(0,0,0,0.2); max-width: 500px;'>");
            response.getWriter().println("<h1 style='color: #2f855a; margin-top:0;'>¡Test Guardado con Éxito!</h1>");
            response.getWriter().println("<p style='color: #4a5568; font-size: 16px; line-height: 1.5;'>Tus resultados han sido procesados y enviados al administrador. Ya puedes cerrar esta ventana.</p>");
            response.getWriter().println("</div></body></html>");

        } catch (Exception e) {
            // Deshacemos todo si falla
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            // Si hay error, reenviamos a la página del test adjuntando el ID original para que no salte el error de test fantasma
            String fallbackId = request.getParameter("testId") != null ? request.getParameter("testId") : "";
            response.sendRedirect(request.getContextPath() + "/html/test.jsp?error=true&testId=" + fallbackId);
        }
    }
}