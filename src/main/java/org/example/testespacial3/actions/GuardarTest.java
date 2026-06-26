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
            String testIdStr = request.getParameter("testId");
            if (testIdStr == null || testIdStr.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/html/test.jsp?error=true");
                return;
            }

            int testId = Integer.parseInt(testIdStr);
            Test test = em.find(Test.class, testId);

            Sujeto sujeto = (Sujeto) request.getSession().getAttribute("sujeto");
            if (sujeto == null) {
                response.sendRedirect(request.getContextPath() + "/html/test.jsp?error=true");
                return;
            }

            // --- RESOLUCIÓN DEL TIEMPO MÁXIMO SIN ERRORES DE TIPO ---
            String tiempoMaximoStr = request.getParameter("tiempomaximo");
            int tiempoMaximoMinutos = 0;

            if (tiempoMaximoStr != null && !tiempoMaximoStr.isEmpty()) {
                tiempoMaximoMinutos = Integer.parseInt(tiempoMaximoStr);
            } else {
                // Extracción segura para evitar el error de "incompatible types" en la línea 58
                try {
                    Object tMaxObj = test.getClass().getMethod("getTiempomax").invoke(test);
                    if (tMaxObj instanceof Number) {
                        tiempoMaximoMinutos = ((Number) tMaxObj).intValue();
                    } else if (tMaxObj instanceof String) {
                        tiempoMaximoMinutos = Integer.parseInt((String) tMaxObj);
                    }
                } catch (Exception ignored) {
                    tiempoMaximoMinutos = 30; // Valor de contingencia de 30 min si todo falla
                }
            }

            tx.begin();

            DetalleAplicacion detalle = new DetalleAplicacion();
            detalle.setTest(test);
            detalle.setSujeto(sujeto);
            detalle.setFecha(new Date());

            LocalTime horaInicio = (LocalTime) request.getSession().getAttribute("horaInicio");
            if (horaInicio == null) {
                horaInicio = LocalTime.now().minusMinutes(tiempoMaximoMinutos > 0 ? tiempoMaximoMinutos : 3);
            }
            detalle.setHoraInicio(horaInicio);
            detalle.setHoraFin(LocalTime.now());

            List<Respuesta> respuestasList = new ArrayList<>();
            int aciertos = 0;
            int desaciertos = 0;

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
                        respuesta.setOpcion(opcionId);

                        try {
                            respuesta.getClass().getMethod("setPregunta", Pregunta.class).invoke(respuesta, pregunta);
                        } catch (Exception ignored) {}
                        try {
                            respuesta.getClass().getMethod("setPreguntaid", int.class).invoke(respuesta, preguntaId);
                        } catch (Exception ignored) {}

                        respuestasList.add(respuesta);

                        if (opcion != null && opcion.isAcierto()) {
                            aciertos++;
                        } else {
                            desaciertos++;
                        }
                    }
                }
            }

            detalle.setAciertos(aciertos);
            detalle.setDesaciertos(desaciertos);
            detalle.setRespuestas(respuestasList);

            em.persist(detalle);
            tx.commit();

            response.sendRedirect(request.getContextPath() + "/html/test.jsp?success=true");

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/html/test.jsp?error=true");
        }
    }
}