package org.example.testespacial3.actions;

import org.example.testespacial3.modelo.*;
import javax.persistence.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalTime;
import java.util.*;

public class GuardarTest extends HttpServlet {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        String username = (String) session.getAttribute("xava.user");
        if (username == null) {
            response.sendRedirect(request.getContextPath() + "/html/login.html");
            return;
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            TypedQuery<Sujeto> sujetoQuery = em.createQuery(
                    "SELECT s FROM Sujeto s WHERE s.username = :username", Sujeto.class);
            sujetoQuery.setParameter("username", username);
            Sujeto sujeto = sujetoQuery.getSingleResult();

            int aciertos = 0;
            int desaciertos = 0;
            LocalTime horaInicio = (LocalTime) session.getAttribute("horaInicioTest");
            if (horaInicio == null) {
                horaInicio = LocalTime.now().minusMinutes(5);
            }
            LocalTime horaFin = LocalTime.now();

            DetalleAplicacion detalle = new DetalleAplicacion();
            detalle.setSujeto(sujeto);
            detalle.setFecha(new Date());
            detalle.setHoraInicio(horaInicio);
            detalle.setHoraFin(horaFin);

            List<Respuesta> listaRespuestas = new ArrayList<>();
            Test testAsociado = null;

            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();

                if (paramName.startsWith("preg_")) {
                    int preguntaId = Integer.parseInt(paramName.replace("preg_", ""));
                    int opcionId = Integer.parseInt(request.getParameter(paramName));

                    Pregunta pregunta = em.find(Pregunta.class, preguntaId);
                    Opcion opcion = em.find(Opcion.class, opcionId);

                    if (pregunta != null && opcion != null) {
                        if (testAsociado == null) {
                            testAsociado = pregunta.getTest();
                        }

                        if (opcion.isAcierto()) {
                            aciertos++;
                        } else {
                            desaciertos++;
                        }

                        Respuesta respuestaNode = new Respuesta();
                        respuestaNode.setPregunta(pregunta);
                        respuestaNode.setOpcion(opcion.getRespuesta()); // Guarda el número de respuesta (1, 2, 3...)
                        respuestaNode.setDetalleAplicacion(detalle);

                        listaRespuestas.add(respuestaNode);
                    }
                }
            }

            detalle.setAciertos(aciertos);
            detalle.setDesaciertos(desaciertos);
            detalle.setTest(testAsociado);
            detalle.setRespuestas(listaRespuestas);

            tx.begin();
            em.persist(detalle);
            tx.commit();

            // Limpiamos la hora de inicio de la sesión
            session.removeAttribute("horaInicioTest");

            // 6. Redirigimos al usuario a una pantalla de éxito mostrando sus aciertos
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println(
                    "<script>" +
                            "alert('Test finalizado con éxito.');" +
                            "window.location.href='" + request.getContextPath() + "/html/login.html';" +
                            "</script>"
            );

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            response.getWriter().println("Hubo un error al procesar tus respuestas: " + e.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}