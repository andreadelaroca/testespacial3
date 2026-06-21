import lombok.*;
import javax.persistence.*;
import org.openxava.annotations.*;
import java.util.Date;

public class Usuario {

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