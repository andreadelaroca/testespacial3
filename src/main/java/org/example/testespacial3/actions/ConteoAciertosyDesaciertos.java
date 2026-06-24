package org.example.testespacial3.actions;

import org.example.testespacial3.modelo.DetalleAplicacion;
import org.example.testespacial3.modelo.Opcion;
import org.example.testespacial3.modelo.Respuesta;
import org.openxava.jpa.XPersistence;

import javax.persistence.EntityManager;

public class ConteoAciertosyDesaciertos {

    public static void calcular(DetalleAplicacion detalle) {

        EntityManager em = XPersistence.getManager();

        int aciertos = 0;
        int desaciertos = 0;

        for (Respuesta respuesta : detalle.getRespuestas()) {

            Opcion opcion = em.find(
                    Opcion.class,
                    respuesta.getOpcion()
            );

            if (opcion != null && opcion.isAcierto()) {
                aciertos++;
            } else {
                desaciertos++;
            }
        }

        detalle.setAciertos(aciertos);
        detalle.setDesaciertos(desaciertos);
    }
}