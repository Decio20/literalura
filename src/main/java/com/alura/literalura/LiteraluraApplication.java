package com.alura.literalura;

import com.alura.literalura.principal.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/*Clase principal de la aplicación Literalura.
 Esta clase es el punto de entrada de la aplicación Spring Boot.*/

@SpringBootApplication
//@EnableJpaRepositories(basePackages = "com.alura.literalura.repository")
public class LiteraluraApplication implements CommandLineRunner {


	@Autowired
	private final Principal Principal;


	public LiteraluraApplication(Principal Principal) {
		this.Principal = Principal;
	}


	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);

	}


	@Override
	public void run(String... args) throws Exception {
		Principal.mostrarMenu();

	}
}
