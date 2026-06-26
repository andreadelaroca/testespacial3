package org.example.testespacial3.actions;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import javax.persistence.*;

public class IniciarSesion extends ViewBaseAction implements IForwardAction {

    private String forwardURI;

    public void execute() throws Exception {
        String username = getView().getValueString("usuario");
        String password = getView().getValueString("contrasena");

        if (username == null || password == null) {
            addError("usuario_o_password_incorrecto");
            return;
        }

        if (username.trim().equals("admin") && password.trim().equals("admin")) {
            getRequest().getSession().setAttribute("xava.user", "admin");
            this.forwardURI = "/m/Test";
            return;
        }

        try {
            EntityManager em = XPersistence.getManager();
            Query query = em.createQuery("SELECT u FROM Usuario u WHERE u.username = :username AND u.password = :password");
            query.setParameter("username", username.trim());
            query.setParameter("password", password.trim());

            Object usuarioLogueado = query.getSingleResult();
            getRequest().getSession().setAttribute("xava.user", username.trim());

            if (usuarioLogueado.getClass().getSimpleName().toLowerCase().contains("sujeto")) {
                this.forwardURI = "/cargarTest";
            } else {
                this.forwardURI = "/m/Test";
            }

        } catch (NoResultException e) {
            addError("usuario_o_password_incorrecto");
        }
    }

    @Override
    public String getForwardURI() {
        return this.forwardURI;
    }

    @Override
    public boolean inNewWindow() {
        return false;
    }
}