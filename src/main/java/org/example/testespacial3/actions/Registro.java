package org.example.testespacial3.actions;

import org.example.testespacial3.modelo.Usuario;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/registro")
public class Registro extends HttpServlet {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("default");

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        EntityManager em = emf.createEntityManager();

        try {

            Usuario usuario = new Usuario();

            usuario.setNombres(
                    request.getParameter("nombres"));

            usuario.setApellidos(
                    request.getParameter("apellidos"));

            usuario.setEmail(
                    request.getParameter("email"));

            usuario.setUsername(
                    request.getParameter("username"));

            usuario.setPassword(
                    request.getParameter("password"));

            usuario.setFechaNac(
                    LocalDate.parse(
                            request.getParameter("fechaNac")));

            usuario.setSexo(
                    request.getParameter("sexo"));

            usuario.setProfesion(
                    request.getParameter("profesion"));

            usuario.setEstudios(
                    request.getParameter("estudios"));

            em.getTransaction().begin();

            em.persist(usuario);

            em.getTransaction().commit();

            response.sendRedirect("index.html");

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            e.printStackTrace();

            response.sendRedirect("registro.html");

        } finally {

            em.close();
        }
    }
}