package org.example.testespacial3.actions;

import org.example.testespacial3.modelo.Sujeto;
import javax.persistence.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class ProcesarLogin extends HttpServlet {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        EntityManager em = emf.createEntityManager();

        try {
            // Buscamos si existe un sujeto con ese usuario y contraseña
            TypedQuery<Sujeto> query = em.createQuery(
                    "SELECT s FROM Sujeto s WHERE s.username = :username AND s.password = :password",
                    Sujeto.class);
            query.setParameter("username", username.trim());
            query.setParameter("password", password.trim());

            Sujeto sujeto = query.getSingleResult(); // Lanza NoResultException si falla

            // Si llegamos aquí, el login fue exitoso.
            // Guardamos la sesión exactamente como lo espera tu GuardarTest.java
            request.getSession().setAttribute("xava.user", sujeto.getUsername());

            // Lo disparamos directo al test
            response.sendRedirect(request.getContextPath() + "/cargarTest");

        } catch (NoResultException e) {
            // Si las credenciales son incorrectas, lo regresamos al login con un error (puedes agregar lógica JS luego para mostrarlo)
            response.sendRedirect(request.getContextPath() + "/html/login.html?error=credenciales");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/html/login.html?error=server");
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}