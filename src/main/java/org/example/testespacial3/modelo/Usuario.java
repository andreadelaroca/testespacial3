package org.example.testespacial3.modelo;

import lombok.*;
import javax.persistence.*;
import org.openxava.annotations.*;
import java.util.Date;

@Entity @Getter @Setter @Table(name = "usuario")
public class Usuario {

	@Id @Hidden @GeneratedValue
	private int id;

	private String nombres;

	private String apellidos;

	private String email;

	private String estudios;

	private Date fechaNac;

	private String password;

	private String profesion;

	private char sexo;

	private String username;
	public void cerrarSesion(){

	}

	public void iniciarSesion(){

	}

}