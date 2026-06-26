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

        boolean transaccionPropia = false;

        try {
            String testIdStr = request.getParameter("testId");

            if (testIdStr == null || testIdStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/cargarTest?error=true");
                return;
            }

            int testId = Integer.parseInt(testIdStr);

            Test test = em.find(Test.class, testId);

            if (test == null) {
                response.sendRedirect(request.getContextPath() + "/cargarTest?error=true");
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

            /*
             * OpenXava a veces ya trae una transacción activa.
             * Por eso solo iniciamos una nueva si NO existe una activa.
             */
            if (!tx.isActive()) {
                tx.begin();
                transaccionPropia = true;
            }

            DetalleAplicacion detalle = new DetalleAplicacion();

            detalle.setTest(test);
            detalle.setSujeto(sujeto);
            detalle.setFecha(new Date());

            LocalTime horaInicio = (LocalTime) request.getSession().getAttribute("horaInicio");

            if (horaInicio == null) {
                horaInicio = LocalTime.now();
            }

            detalle.setHoraInicio(horaInicio);
            detalle.setHoraFin(LocalTime.now());

            Long totalPreguntas = (Long) em.createQuery(
                            "SELECT COUNT(p) FROM Pregunta p WHERE p.test.id = :testId"
                    )
                    .setParameter("testId", testId)
                    .getSingleResult();

            List<Respuesta> respuestas = new ArrayList<>();

            int aciertos = 0;

            Map<String, String[]> parametros = request.getParameterMap();

            for (Map.Entry<String, String[]> entry : parametros.entrySet()) {

                String nombreParametro = entry.getKey();

                if (!nombreParametro.startsWith("respuesta_")) {
                    continue;
                }

                String preguntaIdStr = nombreParametro.replace("respuesta_", "");
                String[] valores = entry.getValue();

                if (valores == null || valores.length == 0) {
                    continue;
                }

                String opcionIdStr = valores[0];

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

            int desaciertos = totalPreguntas.intValue() - aciertos;

            detalle.setAciertos(aciertos);
            detalle.setDesaciertos(desaciertos);
            detalle.setRespuestas(respuestas);

            em.persist(detalle);

            /*
             * Si la transacción la iniciamos nosotros, hacemos commit.
             * Si ya venía activa por OpenXava, solo hacemos flush.
             */
            if (transaccionPropia) {
                tx.commit();
            } else {
                em.flush();
            }

            request.getSession().invalidate();

            response.setContentType("text/html;charset=UTF-8");

            response.getWriter().println("<!DOCTYPE html>");
            response.getWriter().println("<html lang='es'>");
            response.getWriter().println("<head>");
            response.getWriter().println("<meta charset='UTF-8'>");
            response.getWriter().println("<title>Test Finalizado</title>");
            response.getWriter().println("</head>");
            response.getWriter().println("<body style='margin:0;height:100vh;display:flex;justify-content:center;align-items:center;background:#083c57;font-family:Arial,sans-serif;'>");
            response.getWriter().println("<div style='background:white;padding:40px;border-radius:15px;text-align:center;max-width:500px;box-shadow:0 10px 30px rgba(0,0,0,.35);'>");
            response.getWriter().println("<h1 style='color:#2f855a;margin-top:0;'>Test guardado con éxito</h1>");
            response.getWriter().println("<p style='color:#555;'>Las respuestas fueron registradas correctamente en DetalleAplicacion.</p>");
            response.getWriter().println("</div>");
            response.getWriter().println("</body>");
            response.getWriter().println("</html>");

        } catch (Exception e) {

            /*
             * Solo hacemos rollback si nosotros iniciamos la transacción.
             * Si era de OpenXava, NO la tocamos para evitar errores al destruir el request.
             */
            if (transaccionPropia && tx != null && tx.isActive()) {
                tx.rollback();
            }

            e.printStackTrace();

            response.sendRedirect(request.getContextPath() + "/cargarTest?error=true");
        }
    }
}