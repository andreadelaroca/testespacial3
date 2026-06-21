package org.example.testespacial3.modelo;
import lombok.*;
import javax.persistence.*;

@Entity @Getter @Setter
public class Test {

	private boolean estado;

	private String instrucciones;
	private String nombre;
	private float tiempoMax;


	public Pregunta pregunta;
	public Pregunta m_Pregunta;

	public Test(){

	}

	public void iniciarTiempo(){

	}
}