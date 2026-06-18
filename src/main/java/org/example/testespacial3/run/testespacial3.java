package org.example.testespacial3.run;

import org.openxava.util.*;

/**
 * Ejecuta esta clase para arrancar la aplicación.
 */

public class testespacial3 {

	public static void main(String[] args) throws Exception {
		DBServer.start("testespacial3-db"); // Para usar tu propia base de datos comenta esta línea y configura src/main/webapp/META-INF/context.xml
		AppServer.run("testespacial3"); // Usa AppServer.run("") para funcionar en el contexto raíz
	}

}
