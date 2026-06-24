package org.example.testespacial3.actions;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import javax.persistence.*;

public class IniciarSesion extends ViewBaseAction implements IForwardAction {
    private String forwardURI;

    public void execute() throws Exception {
        String username = getView().getValueString("usuario");
        String password = getView().getValueString("contrasena");

        try {
            EntityManager em = XPersistence.getManager();

            Query query = em.createQuery("SELECT u FROM Usuario u WHERE u.username = :username AND u.password = :password");
            query.setParameter("username", username);
            query.setParameter("password", password);

            Object usuarioLogueado = query.getSingleResult();

            getRequest().getSession().setAttribute("xava.user", username);

            if (usuarioLogueado.getClass().getSimpleName().contains("Sujeto")) {
                this.forwardURI = "/html/res/index.html";
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