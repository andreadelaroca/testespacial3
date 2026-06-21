
import lombok.*;
import javax.persistence.*;

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