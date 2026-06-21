package org.example.testespacial3.modelo;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;
import java.time.*;
import java.util.*;

@Entity @Getter @Setter @Table(name = "detalleaplicacion")
public class DetalleAplicacion {

	@Id @Hidden @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "detalleaplicacionid")
	private int id;

	@Required @Column(name = "aciertos")
	private int aciertos;

	@Required @Column(name = "desaciertos")
	private int desaciertos;

	@Required @Column(name = "fecha") @DefaultValueCalculator(org.openxava.calculators.CurrentDateCalculator.class)
	private LocalDate fecha;

	@Required @Column(name = "horainicio")
	private LocalTime horaInicio;

	@Required @Column(name = "horafin")
	private LocalTime horaFin;

	@ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "testid") @DescriptionsList(descriptionProperties = "nombre")
	private Test test;

	@ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "sujetoid") @DescriptionsList(descriptionProperties = "nombres" + " " + "apellidos")
	private Sujeto sujeto;

	@OneToMany(mappedBy = "detalleAplicacion", cascade = CascadeType.ALL) @ListProperties("preguntaid, opcion")
	private	Collection<Respuesta> respuestas;
}