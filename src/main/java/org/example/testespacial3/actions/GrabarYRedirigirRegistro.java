package org.example.testespacial3.actions;
import org.openxava.actions.*;

public class GrabarYRedirigirRegistro extends SaveAction implements IForwardAction {

    private String forwardURI;

    @Override
    public void execute() throws Exception {
        super.execute();

        if (getErrors().isEmpty()) {
            addMessage("Registro completado con éxito. Por favor, inicia sesión.");
            this.forwardURI = "/m/SignIn";
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