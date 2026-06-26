package org.example.testespacial3.actions;

import org.example.testespacial3.modelo.Sujeto;
import javax.persistence.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;

public class Registro extends HttpServlet {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        EntityManager em = emf.createEntityManager();

        try {
            String username = trim(request.getParameter("username"));
            String password = trim(request.getParameter("password"));
            String fechaNac = trim(request.getParameter("fechaNac"));
            String sexo = normalizeSexo(request.getParameter("sexo"));

            if (isBlank(trim(request.getParameter("nombres"))) ||
                    isBlank(trim(request.getParameter("apellidos"))) ||
                    isBlank(trim(request.getParameter("email"))) ||
                    isBlank(username) ||
                    isBlank(password) ||
                    isBlank(fechaNac) ||
                    isBlank(trim(request.getParameter("profesion"))) ||
                    isBlank(trim(request.getParameter("estudios")))) {
                response.sendRedirect("/testespacial3/html/registro.html?error=campos");
                return;
            }

            if (!"F".equals(sexo) && !"M".equals(sexo)) {
                response.sendRedirect("/testespacial3/html/registro.html?error=sexo");
                return;
            }

            TypedQuery<Long> existingUserQuery = em.createQuery(
                    "SELECT COUNT(u) FROM Usuario u WHERE LOWER(u.username) = LOWER(:username)",
                    Long.class
            );
            existingUserQuery.setParameter("username", username);

            if (existingUserQuery.getSingleResult() > 0) {
                response.sendRedirect("/testespacial3/html/registro.html?error=usuario");
                return;
            }

            Sujeto sujeto = new Sujeto();
            sujeto.setNombres(trim(request.getParameter("nombres")));
            sujeto.setApellidos(trim(request.getParameter("apellidos")));
            sujeto.setEmail(trim(request.getParameter("email")));
            sujeto.setUsername(username);
            sujeto.setPassword(password);
            sujeto.setFechaNac(LocalDate.parse(fechaNac));
            sujeto.setSexo(sexo);
            sujeto.setProfesion(trim(request.getParameter("profesion")));
            sujeto.setEstudios(trim(request.getParameter("estudios")));

            em.getTransaction().begin();
            em.persist(sujeto);
            em.getTransaction().commit();

            response.sendRedirect("/testespacial3/html/login.html?registro=ok");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            response.sendRedirect("/testespacial3/html/registro.html?error=server");
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String normalizeSexo(String value) {
        if (value == null) return null;
        return value.trim().toUpperCase();
    }
}