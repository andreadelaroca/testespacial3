import javax.persistence.Entity;

@Entity
public class Opcion {

	@Required
	private boolean acierto;


	private int respuesta;

	public Opcion(){

	}

}