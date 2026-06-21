package org.example.testespacial3.modelo;

import lombok.*;

import javax.ejb.Local;
import javax.persistence.*;
import org.openxava.annotations.*;
import java.time.*;
import org.hibernate.annotations.Where;

@Entity @Getter @Setter @Table(name = "usuario")
public class Usuario {

	@Id @Hidden @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "usuarioid")
	private int id;

	@Required @Column(name = "username", length = 255)
	private String username;

	@Required @Column(name = "password", length = 255) @Stereotype("PASSWORD")
	private String password;

	@Required @Column(name = "nombres", length = 255)
	private String nombres;

	@Required @Column(name = "apellidos", length = 255)
	private String apellidos;

	@Column(name = "email", length = 255) @Stereotype("EMAIL")
	private String email;

	@Column(name = "fechanac")
	private LocalDate fechaNac;

	@Column(name = "sexo", length = 1)
	private String sexo;

	@Column(name = "estudios", length = 255)
	private String estudios;

	@Column(name = "profesion", length = 255)
	private String profesion;

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

	public void cerrarSesion(){

	}

	public void iniciarSesion(){

	}

}