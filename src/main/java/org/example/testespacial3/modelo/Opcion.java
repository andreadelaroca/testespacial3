package org.example.testespacial3.modelo;
import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity @Table(name = "opcion") @Getter @Setter
public class Opcion {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "opcionid") @Hidden
	private int id;

	@Required @Column(name = "acierto")
	private boolean acierto;

	@Required @Column(name = "respuesta")
	private int respuesta;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "preguntaid") @DescriptionsList
	private Pregunta pregunta;
}