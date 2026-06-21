package org.example.testespacial3.modelo;
import javax.persistence.*;
import jdk.jfr.Frequency;
import org.openxava.annotations.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import org.hibernate.annotations.Where;

@Entity @Table(name = "pregunta") @Getter @Setter @Where(clause = "deletedAt IS NULL")
public class Pregunta {

	@Id @Hidden @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "preguntaid")
	private int id;

	@Required @Column(name = "imagen", length = 255) @Stereotype("IMAGE")
	private String imagen;

	@ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "testid") @DescriptionsList(descriptionProperties = "nombre")
	private Test test;

	@OneToMany(mappedBy = "pregunta", cascade = CascadeType.ALL) @ListProperties("respuesta, acierto")
	private Collection<Opcion> opciones;

	//tiempos para fines de auditoria
	@ReadOnly @Column(name = "createdat", updatable = false)
	private LocalDateTime createdAt;

	@ReadOnly @Column(name = "updatedat")
	private LocalDateTime updatedAt;

	@Hidden @Column(name = "deletedat")
	private LocalDateTime deletedAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}
	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}