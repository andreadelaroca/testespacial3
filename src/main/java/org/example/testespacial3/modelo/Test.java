package org.example.testespacial3.modelo;
import lombok.*;
import javax.persistence.*;

@Entity @Getter @Setter
public class Test {

	@Column(length = 8)
	private boolean estado;

	private String instrucciones;
	private String nombre;
	private float tiempoMax;


	public Pregunta pregunta;

	public Test(){

	}

	public void iniciarTiempo(){

	}
}