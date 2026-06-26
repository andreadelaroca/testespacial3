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

import org.example.testespacial3.modelo.DetalleAplicacion;
import org.example.testespacial3.modelo.Opcion;
import org.example.testespacial3.modelo.Pregunta;
import org.example.testespacial3.modelo.Respuesta;
import org.example.testespacial3.modelo.Sujeto;
import org.example.testespacial3.modelo.Test;
import org.openxava.jpa.XPersistence;

@WebServlet("/guardarTest")
public class GuardarTest extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        EntityManager em = XPersistence.getManager();
        EntityTransaction tx = em.getTransaction();

        try {
            String testIdStr = request.getParameter("testId");

            if (testIdStr == null || testIdStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/html/test.jsp?error=true");
                return;
            }

            int testId = Integer.parseInt(testIdStr);
            Test test = em.find(Test.class, testId);

            if (test == null) {
                response.sendRedirect(request.getContextPath() + "/html/test.jsp?error=true");
                return;
            }

            Integer sujetoId = (Integer) request.getSession().getAttribute("sujetoId");

            if (sujetoId == null) {
                response.sendRedirect(request.getContextPath() + "/html/login.html?error=sesion_expirada");
                return;
            }

            Sujeto sujeto = em.find(Sujeto.class, sujetoId);

            if (sujeto == null) {
                response.sendRedirect(request.getContextPath() + "/html/login.html?error=sesion_expirada");
                return;
            }

            if (!tx.isActive()) {
                tx.begin();
            }

            DetalleAplicacion detalle = new DetalleAplicacion();
            detalle.setTest(test);
            detalle.setSujeto(sujeto);
            detalle.setFecha(new Date());

            LocalTime horaInicio = (LocalTime) request.getSession().getAttribute("horaInicio");

            if (horaInicio == null) {
                float tiempoMax = test.getTiempoMax();
                long segundos = Math.round(tiempoMax * 60);
                horaInicio = LocalTime.now().minusSeconds(segundos);
            }

            detalle.setHoraInicio(horaInicio);
            detalle.setHoraFin(LocalTime.now());

            List<Respuesta> respuestas = new ArrayList<>();

            int aciertos = 0;

            Long totalPreguntas = (Long) em.createQuery(
                            "SELECT COUNT(p) FROM Pregunta p WHERE p.test.id = :testId"
                    )
                    .setParameter("testId", testId)
                    .getSingleResult();

            Map<String, String[]> parametros = request.getParameterMap();

            for (Map.Entry<String, String[]> entry : parametros.entrySet()) {
                String nombreParametro = entry.getKey();

                if (nombreParametro.startsWith("respuesta_")) {
                    String preguntaIdStr = nombreParametro.replace("respuesta_", "");
                    String opcionIdStr = entry.getValue()[0];

                    if (opcionIdStr == null || opcionIdStr.trim().isEmpty()) {
                        continue;
                    }

                    int preguntaId = Integer.parseInt(preguntaIdStr);
                    int opcionId = Integer.parseInt(opcionIdStr);

                    Pregunta pregunta = em.find(Pregunta.class, preguntaId);
                    Opcion opcion = em.find(Opcion.class, opcionId);

                    if (pregunta == null || opcion == null) {
                        continue;
                    }

                    Respuesta respuesta = new Respuesta();
                    respuesta.setDetalleAplicacion(detalle);
                    respuesta.setPregunta(pregunta);
                    respuesta.setOpcion(opcionId);

                    respuestas.add(respuesta);

                    if (opcion.isAcierto()) {
                        aciertos++;
                    }
                }
            }

            int desaciertos = totalPreguntas.intValue() - aciertos;

            detalle.setAciertos(aciertos);
            detalle.setDesaciertos(desaciertos);
            detalle.setRespuestas(respuestas);

            em.persist(detalle);

            tx.commit();

            request.getSession().invalidate();

            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("""
                    <!DOCTYPE html>
                    <html lang='es'>
                    <head>
                        <meta charset='UTF-8'>
                        <title>Test Finalizado</title>
                    </head>
                    <body style='margin:0; height:100vh; display:flex; justify-content:center; align-items:center; background:#1a365d; font-family:Arial, sans-serif;'>
                        <div style='background:white; padding:40px; border-radius:12px; text-align:center; max-width:500px; box-shadow:0 8px 25px rgba(0,0,0,.25);'>
                            <h1 style='color:#2f855a;'>Test guardado con éxito</h1>
                            <p style='color:#4a5568;'>Las respuestas fueron registradas en DetalleAplicacion.</p>
                        </div>
                    </body>
                    </html>
                    """);

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }

            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/html/test.jsp?error=true");
        }
    }
}