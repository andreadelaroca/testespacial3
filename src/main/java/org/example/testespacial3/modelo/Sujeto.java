package org.example.testespacial3.modelo;

import javax.persistence.*;
import lombok.*;
import org.openxava.annotations.*;
import java.util.*;

@Entity @Getter @Setter @Table(name = "sujeto") @PrimaryKeyJoinColumn(name = "sujetoid")
@View(name = "Registro", members = "username, password; nombres, apellidos; email, sexo; fechaNac, estudios, profesion")
public class Sujeto extends Usuario {

	@OneToMany(mappedBy = "sujeto", cascade = CascadeType.ALL) @ReadOnly @ListProperties("fecha, horaInicio, test.nombre, aciertos, desaciertos")
	private Collection<DetalleAplicacion> aplicaciones;

	public void realizarTest(){

	}
}