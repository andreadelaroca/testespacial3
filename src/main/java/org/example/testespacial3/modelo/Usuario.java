import lombok.*;
import javax.persistence.*;
import org.openxava.annotations.*;
import java.util.Date;

@Entity @Getter @Setter
public class Usuario {

	@Column(length = 60) @Required
	private String nombres;

	@Column(length = 60) @Required
	private String apellidos;

	@Column(length = 60) @Required
	private String email;

	@Column(length = 30)
	private String estudios;

	@Column(length = 10) @Required
	private Date fechaNac;

	@Column(length = 20)
	private String password;

	@Column(length = 30)
	private String profesion;

	@Column(length = 1)
	private char sexo;

	@Column(length = 30)
	private String username;
}