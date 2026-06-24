package org.example.testespacial3.actions;
import org.openxava.actions.*;

public class GrabarYRedirigirRegistro extends SaveAction implements IForwardAction {

    private String forwardURI;

    @Override
    public void execute() throws Exception {
        super.execute();

        if (getErrors().isEmpty()) {
            addMessage("Registro completado con �xito. Por favor, inicia sesi�n.");
            this.forwardURI = "/html/login.html";
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