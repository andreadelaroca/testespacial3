package org.example.testespacial3.web;

import org.example.testespacial3.modelo.Sujeto;
import org.openxava.jpa.XPersistence;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/api/registro-sujeto")
public class RegistroSujetoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        String nombres = trim(request.getParameter("nombres"));
        String apellidos = trim(request.getParameter("apellidos"));
        String email = trim(request.getParameter("email"));
        String username = trim(request.getParameter("username"));
        String fechaNac = trim(request.getParameter("fechaNac"));
        String sexo = trim(request.getParameter("sexo"));
        String profesion = trim(request.getParameter("profesion"));
        String estudios = trim(request.getParameter("estudios"));
        String password = trim(request.getParameter("password"));

        if (isBlank(nombres) || isBlank(apellidos) || isBlank(email) || isBlank(username) || isBlank(fechaNac) || isBlank(sexo) || isBlank(profesion) || isBlank(estudios) || isBlank(password)) {
            writeJson(response, false, "Todos los campos obligatorios son requeridos.");
            return;
        }

        EntityManager em = null;

        try {
            em = XPersistence.getManager();

            TypedQuery<Long> existingUserQuery = em.createQuery(
                "SELECT COUNT(u) FROM Usuario u WHERE LOWER(u.username) = LOWER(:username)",
                Long.class
            );
            existingUserQuery.setParameter("username", username);

            if (existingUserQuery.getSingleResult() > 0) {
                writeJson(response, false, "Ya existe un usuario con ese nombre de usuario.");
                return;
            }

            Sujeto sujeto = new Sujeto();
            sujeto.setNombres(nombres);
            sujeto.setApellidos(apellidos);
            sujeto.setEmail(email);
            sujeto.setUsername(username);
            sujeto.setFechaNac(LocalDate.parse(fechaNac));
            sujeto.setSexo(sexo);
            sujeto.setProfesion(profesion);
            sujeto.setEstudios(estudios);
            sujeto.setPassword(password);

            em.getTransaction().begin();
            em.persist(sujeto);
            em.getTransaction().commit();

            writeJson(response, true, "Registro guardado correctamente.");
        } catch (Exception exception) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            writeJson(response, false, "No se pudo guardar el registro.");
        }
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void writeJson(HttpServletResponse response, boolean ok, String message) throws IOException {
        response.getWriter().write("{\"ok\":" + ok + ",\"message\":\"" + escapeJson(message) + "\"}");
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
    }
}