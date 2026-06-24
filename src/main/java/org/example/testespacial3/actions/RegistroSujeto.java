package org.example.testespacial3.actions;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import javax.persistence.*;
import org.openxava.view.View;

public class RegistroSujeto extends ViewBaseAction {
    public void execute() throws Exception {
        String username = getView().getValueString("usuario");
        String password = getView().getValueString("contrasena");

        try {
            EntityManager em = XPersistence.getManager();
            Query query = em.createQuery("SELECT u FROM sujeto u WHERE u.username = :username AND u.password = :password");
            query.setParameter("username", username);
            query.setParameter("password", password);

            Object usuarioLogueado = query.getSingleResult();

            if (usuarioLogueado.getClass().getSimpleName().contains("Sujeto")) {
                getRequest().getSession().setAttribute("xava.user", username);
                String urlTest = getRequest().getContextPath() + "/test-sujeto.html";
                getView().setNextURL(urlTest);

            } else {
                getRequest().getSession().setAttribute("xava.user", username);
                String urlAdmin = getRequest().getContextPath() + "/m/Test";
                getView().setNextURL(urlAdmin);
            }

        } catch (NoResultException e) {
            addError("usuario_o_password_incorrecto");
        }
    }
}
