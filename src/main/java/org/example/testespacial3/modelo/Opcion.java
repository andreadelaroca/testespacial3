import org.example.testespacial3.modelo;
import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity @Getter @Setter
public class Opcion {

	@Required @Column(length = 5)
	private boolean acierto;

	@Required @Column(length = 2)
	private int respuesta;
}