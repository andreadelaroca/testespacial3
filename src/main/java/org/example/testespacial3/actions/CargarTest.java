package org.example.testespacial3.actions;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import org.example.testespacial3.modelo.Pregunta;
import org.example.testespacial3.modelo.Test;
import org.example.testespacial3.modelo.Usuario;
import org.example.testespacial3.modelo.Sujeto;

@WebServlet("/cargarTest")
public class CargarTest extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String user = request.getParameter("username");
        String pass = request.getParameter("password");

        // Validamos si enviaron campos vacíos
        if (user == null || pass == null || user.trim().isEmpty() || pass.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/html/login.html?error=1");
            return;
        }

        javax.persistence.EntityManager em = org.openxava.jpa.XPersistence.getManager();

        try {
            String queryLogin = "SELECT s FROM Sujeto s WHERE s.username = :usuario AND s.password = :clave";
            Sujeto sujetoLogueado = em.createQuery(queryLogin, Sujeto.class)
                    .setParameter("usuario", user)
                    .setParameter("clave", pass)
                    .getSingleResult();

            // Login Exitoso
            javax.servlet.http.HttpSession session = request.getSession();
            session.setAttribute("sujetoId", sujetoLogueado.getId());

            response.sendRedirect(request.getContextPath() + "/cargarTest");

        } catch (javax.persistence.NoResultException e) {
            // Login Fallido: Lo regresamos a su login.html con el aviso de error
            response.sendRedirect(request.getContextPath() + "/html/login.html?error=1");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // Protección de Ruta: Si no se ha logueado, de vuelta al login
        if (session.getAttribute("sujetoId") == null) {
            response.sendRedirect(request.getContextPath() + "/html/login.html");
            return;
        }

        javax.persistence.EntityManager em = org.openxava.jpa.XPersistence.getManager();

        try {
            // Buscamos el test que esté activo (estado = true)
            String query = "SELECT t FROM Test t LEFT JOIN FETCH t.preguntas WHERE t.estado = true";
            java.util.List<Test> testsActivos = em.createQuery(query, Test.class).getResultList();

            if (testsActivos.isEmpty()) {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().println("<h1 style='text-align:center;margin-top:50px;'>No hay ningún test habilitado por el administrador en este momento.</h1>");
                return;
            }

            Test testActual = testsActivos.get(0);
            Integer idSujeto = (Integer) session.getAttribute("sujetoId");
            Sujeto sujetoLogueado = em.find(Sujeto.class, idSujeto);
            request.setAttribute("sujeto", sujetoLogueado);
            session.setAttribute("horaInicio", java.time.LocalTime.now());

            // Mapeo e inversión de byte[] a cadenas Base64 para el JSP
            java.util.Map<Integer, String> imagenesBase64 = new java.util.HashMap<>();
            if (testActual.getPreguntas() != null) {
                for (Pregunta p : testActual.getPreguntas()) {
                    if (p.getImagen() != null) {
                        String base64 = java.util.Base64.getEncoder().encodeToString(p.getImagen());
                        imagenesBase64.put(p.getId(), base64);
                    }
                }
            }

            // Enviamos los objetos directo al formulario dinámico hacia abajo
            request.setAttribute("test", testActual);
            request.setAttribute("imagenes", imagenesBase64);

            request.getRequestDispatcher("/html/test.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error interno al cargar el test.");
        }
    }
}