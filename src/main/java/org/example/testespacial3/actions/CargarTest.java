package org.example.testespacial3.actions;

import org.example.testespacial3.modelo.Pregunta;
import javax.persistence.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

public class CargarTest extends HttpServlet {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Pregunta> query = em.createQuery(
                    "SELECT DISTINCT p FROM Pregunta p LEFT JOIN FETCH p.opciones WHERE p.deletedAt IS NULL",
                    Pregunta.class);
            List<Pregunta> preguntas = query.getResultList();

            Map<Integer, String> imagenesBase64 = new HashMap<>();
            for (Pregunta p : preguntas) {
                if (p.getImagen() != null) {
                    String base64 = Base64.getEncoder().encodeToString(p.getImagen());
                    imagenesBase64.put(p.getId(), "data:image/png;base64," + base64);
                }
            }

            request.setAttribute("preguntas", preguntas);
            request.setAttribute("imagenes", imagenesBase64);

            request.getSession().setAttribute("horaInicioTest", java.time.LocalTime.now());

            request.getRequestDispatcher("test.jsp").forward(request, response);

            request.getRequestDispatcher("test.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error cargando el test: " + e.getMessage());
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}