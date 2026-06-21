package org.example.testespacial3.modelo;
import lombok.*;
import org.openxava.annotations.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

@Entity @Table(name = "test") @Getter @Setter
public class Test {

	@Id @Hidden @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "testid")
	private int id;

	@Required @Column(name = "nombre", length = 255)
	private String nombre;

	@Required @Column(name = "instrucciones") @Stereotype("MEMO")
	private String instrucciones;

	@Required @Column(name = "tiempomax")
	private float tiempoMax;

	@Required @Column(name = "estado")
	private boolean estado;

	@OneToMany(mappedBy = "test", cascade = CascadeType.ALL) @ListProperties("imagen")
	public Collection<Pregunta> preguntas;

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