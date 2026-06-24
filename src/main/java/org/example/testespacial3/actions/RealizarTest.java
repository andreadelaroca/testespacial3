package org.example.testespacial3.actions;

import org.example.testespacial3.modelo.*;
import org.openxava.jpa.XPersistence;

import javax.persistence.EntityManager;
import java.time.Duration;
import java.time.LocalTime;

public class RealizarTest {

    public DetalleAplicacion iniciarTest(
            Test test,
            Sujeto sujeto) {

        EntityManager em = XPersistence.getManager();

        DetalleAplicacion detalle =
                new DetalleAplicacion();

        detalle.setTest(test);
        detalle.setSujeto(sujeto);
        detalle.setFecha(new java.util.Date());

        detalle.setHoraInicio(
                LocalTime.now());

        detalle.setAciertos(0);
        detalle.setDesaciertos(0);

        em.persist(detalle);

        return detalle;
    }

    public void guardarRespuesta(
            DetalleAplicacion detalle,
            Pregunta pregunta,
            int opcionId) {

        EntityManager em = XPersistence.getManager();

        if (tiempoExpirado(detalle)) {

            finalizarTest(detalle);

            return;
        }

        Respuesta respuesta =
                new Respuesta();

        respuesta.setDetalleAplicacion(
                detalle);

        respuesta.setPregunta(
                pregunta);

        respuesta.setOpcion(
                opcionId);

        em.persist(respuesta);
    }

    public boolean tiempoExpirado(
            DetalleAplicacion detalle) {

        Duration tiempo =
                Duration.between(
                        detalle.getHoraInicio(),
                        LocalTime.now());

        return tiempo.getSeconds() >= 180;
    }

    public void calcularResultado(
            DetalleAplicacion detalle) {

        EntityManager em =
                XPersistence.getManager();

        int aciertos = 0;
        int desaciertos = 0;

        for (Respuesta respuesta :
                detalle.getRespuestas()) {

            Opcion opcion =
                    em.find(
                            Opcion.class,
                            respuesta.getOpcion());

            if (opcion != null &&
                    opcion.isAcierto()) {

                aciertos++;

            } else {

                desaciertos++;
            }
        }

        detalle.setAciertos(
                aciertos);

        detalle.setDesaciertos(
                desaciertos);
    }

    public void finalizarTest(
            DetalleAplicacion detalle) {

        EntityManager em =
                XPersistence.getManager();

        calcularResultado(detalle);

        detalle.setHoraFin(
                LocalTime.now());

        em.merge(detalle);
    }
}
