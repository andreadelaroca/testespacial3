import javax.persistence.*;
import java.time.*;

@Entity
public class DetalleAplicacion {

	private int aciertos;
	private int desaciertos;
	private LocalDate fecha;
	private LocalTime horaFin;
	private LocalTime horaInicio;
	public Sujeto m_Sujeto;
	public Test m_Test;

	public DetalleAplicacion(){

	}
}