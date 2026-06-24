package org.example.testespacial3.actions;

import org.example.testespacial3.modelo.*;
import org.openxava.actions.*;
import org.openxava.jpa.*;
import javax.persistence.*;

public class IniciarSesion extends ViewBaseAction implements IForwardAction {

    private String forwardURI;

    public void execute() throws Exception {
        String username = getView().getValueString("username");
        if (username == null || username.isBlank()) {
            username = getView().getValueString("usuario");
        }

        if (username == null || username.isBlank()) {
            this.forwardURI = "/html/registro.html";
            return;
        }

        try {
            EntityManager em = XPersistence.getManager();

            TypedQuery<Usuario> query = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.username = :username",
                Usuario.class
            );
            query.setParameter("username", username);

            Usuario usuarioLogueado = query.getSingleResult();

            getRequest().getSession().setAttribute("xava.user", username);

            if (usuarioLogueado instanceof Sujeto) {

                this.forwardURI = "/html/index.html";
            } else {
                this.forwardURI = "/m/RegistroSujeto";
            }

        } catch (NoResultException e) {
            this.forwardURI = "/html/registro.html";
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