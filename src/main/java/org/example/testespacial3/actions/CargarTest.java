package org.example.testespacial3.actions;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import org.example.testespacial3.modelo.Usuario;
import org.example.testespacial3.modelo.Sujeto;

@WebServlet("/cargarTest")
public class CargarTest extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * El método POST recibe los datos del formulario de login de forma segura.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Evitamos problemas de caracteres raros o tildes en los inputs
        request.setCharacterEncoding("UTF-8");

        // 1. Capturamos los datos que vienen desde el html/login.html
        // (Asegúrate de que los inputs en tu HTML tengan name="usuario" y name="password")
        String txtUsuario = request.getParameter("usuario");
        String txtClave = request.getParameter("password");

        // Mensaje de control en la consola de IntelliJ para verificar que los datos llegan
        System.out.println(">>> CargarTest: Intento de acceso con el usuario: " + txtUsuario);

        // 2. [ZONA DE TU LÓGICA]
        // Aquí puedes validar contra tu base de datos usando tus clases 'Usuario' o 'Sujeto'
        // Ejemplo ficticio:
        // if (ValidarUsuario.existe(txtUsuario, txtClave)) { ... }

        // 3. Pasamos el control al método doGet para preparar y redirigir la interfaz
        doGet(request, response);
    }

    /**
     * El método GET se encarga de preparar la sesión y despachar al usuario hacia el test.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Creamos u obtenemos la sesión HTTP del paciente para guardar su progreso
        HttpSession session = request.getSession();

        // Aquí puedes setear atributos iniciales para el test, por ejemplo:
        // session.setAttribute("aciertos", 0);
        // session.setAttribute("preguntaActual", 1);

        System.out.println(">>> CargarTest: Redirigiendo limpiamente a pregunta.html...");

        // 4. LA SOLUCIÓN AL BLANCO: Redirección explícita mediante el navegador.
        // Usamos request.getContextPath() para asegurar que la ruta sea: /testespacial3/html/pregunta.html
        response.sendRedirect(request.getContextPath() + "/html/pregunta.html");
    }
}