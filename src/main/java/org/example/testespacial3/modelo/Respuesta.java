package org.example.testespacial3.modelo;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Table(name = "respuesta")
@Getter
@Setter
public class Respuesta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Hidden
	@Column(name = "respuestaid")
	private int id;

	@Required
	@Column(name = "opcion")
	private int opcion;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "preguntaid")
	@DescriptionsList(descriptionProperties = "imagen")
	private Pregunta pregunta;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "detalleaplicacionid")
	private DetalleAplicacion detalleAplicacion;
}