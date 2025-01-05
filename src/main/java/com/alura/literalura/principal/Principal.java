package com.alura.literalura.principal;


import com.alura.literalura.dto.AutorDTO;
import com.alura.literalura.dto.LibroDTO;
import com.alura.literalura.dto.RespuestaLibrosDTO;
import com.alura.literalura.model.Autor;
import com.alura.literalura.model.Libro;
import com.alura.literalura.service.AutorService;
import com.alura.literalura.service.ConsumoAPI;
import com.alura.literalura.service.ConvierteDatos;
import com.alura.literalura.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class Principal {

    @Autowired
    private LibroService libroService;

    @Autowired
    private AutorService autorService;

    @Autowired
    private ConsumoAPI consumoAPI;

    @Autowired
    private ConvierteDatos convierteDatos;

    private final String URL = "https://gutendex.com/books/?search=";

    public void mostrarMenu(){
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do{
            System.out.println("LITERALURA");
            System.out.println("Escriba un número para elegir opción: ");
            System.out.println("1 - buscar libro por título");
            System.out.println("2 - Listar libros registrados");
            System.out.println("3 - Listar autores registrados");
            System.out.println("4 - Listar autores vivos en un año");
            System.out.println("5 - Listar libros por idioma");
            System.out.println();
            System.out.println("0 - Salir");
            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {

                case 1:
                    System.out.println("Ingrese el título del libro: ");
                    String titulo = scanner.nextLine();
                    try {
                        String encodedTitulo = URLEncoder.encode(titulo, StandardCharsets.UTF_8);
                        String json = consumoAPI.obtenerDatos(URL + encodedTitulo);
                        RespuestaLibrosDTO respuestaLibrosDTO = convierteDatos.obtenerDatos(json, RespuestaLibrosDTO.class);
                        List<LibroDTO> librosDTO = respuestaLibrosDTO.getLibros();
                        if (librosDTO.isEmpty()) {
                            System.out.println("Libro no encontrado en la API");
                        } else {
                            boolean libroRegistrado = false;
                            for (LibroDTO libroDTO : librosDTO) {
                                if (libroDTO.getTitulo().equalsIgnoreCase(titulo)) {
                                    Optional<Libro> libroExistente = libroService.obtenerLibroPorTitulo(titulo);
                                    if (libroExistente.isPresent()) {
                                        System.out.println("Detalle: Clave (titulo)=(" + titulo + ") ya existe");
                                        System.out.println("No se puede registrar el libro más de una vez");
                                        libroRegistrado = true;
                                        break;
                                    } else {
                                        Libro libro = new Libro();
                                        libro.setTitulo(libroDTO.getTitulo());
                                        libro.setIdioma(libroDTO.getIdiomas().get(0));
                                        libro.setNumeroDescargas(libroDTO.getNumeroDescargas());

                                        // Buscar o crear el autor
                                        AutorDTO primerAutorDTO = libroDTO.getAutores().get(0);
                                        Autor autor = autorService.obtenerAutorPorNombre(primerAutorDTO.getNombre())
                                                .orElseGet(() -> {
                                                    Autor nuevoAutor = new Autor();
                                                    nuevoAutor.setNombre(primerAutorDTO.getNombre());
                                                    nuevoAutor.setAnoNacimiento(primerAutorDTO.getAnoNacimiento());
                                                    nuevoAutor.setAnoFallecimiento(primerAutorDTO.getAnoFallecimiento());

                                                    return autorService.crearAutor(nuevoAutor);
                                                });

                                        // asociar el autor al libro
                                        libro.setAutor(autor);


                                        // Guardar el libro en la base de datos
                                        libroService.crearLibro(libro);
                                        System.out.println("Libro registrado: " + libro.getTitulo());
                                        mostrarDetallesLibro(libroDTO);
                                        libroRegistrado = true;
                                        break;
                                    }
                                }

                            }
                            if (!libroRegistrado) {
                                System.out.println("No se encontró un libro con el título: '" + titulo + "'");
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error al obtener datos de la API: " + e.getMessage());
                    }
                    break;

                case 2:
                    libroService.listarLibros().forEach(libro -> {
                        System.out.println("Libro: ");
                        System.out.println("Titulo: " + libro.getTitulo());
                        System.out.println("Autor: " + (libro.getAutor() != null ? libro.getAutor().getNombre() : "Desconocido"));
                        System.out.println("Idioma: " + libro.getIdioma());
                        System.out.println("Número de descargas: " + libro.getNumeroDescargas());
                    });
                    break;


                case 3:
                    autorService.listarAutores().forEach(autor -> {
                        System.out.println("Autor");
                        System.out.println("Autor nombre: " + autor.getNombre());
                        System.out.println("Fecha de nacimiento: " + autor.getAnoNacimiento());
                        System.out.println("Fecha de fallecimiento: " + autor.getAnoFallecimiento());
                        String libros = autor.getLibros().stream()
                                .map(Libro::getTitulo)
                                .collect(Collectors.joining(", "));
                        System.out.println("Libros: " + libros);
                    });
                    break;

                case 4:
                    System.out.println("Ingrese año para ver determinados autores en esa época: ");
                    int ano = scanner.nextInt();
                    scanner.nextLine();
                    List<Autor> autoresVivos = autorService.listarAutoresVivosEnAno(ano);
                    if (autoresVivos.isEmpty()) {
                        System.out.println("No se encontraron autores vivos en el año: " + ano);
                    } else {

                        autoresVivos.forEach(autor -> {
                            System.out.println("Autor: ");
                            System.out.println("Autor nombre: " + autor.getNombre());
                            System.out.println("Año de nacimiento: " + autor.getAnoNacimiento());
                            System.out.println("Año de fallecimiento: " + autor.getAnoFallecimiento());
                            System.out.println("Libros: " + autor.getLibros().size());
                        });
                    }
                    break;

                case 5:
                    System.out.println("Ingrese el idioma: ");
                    System.out.println("es");
                    System.out.println("en");
                    System.out.println("fr");
                    System.out.println("pt");
                    String idioma = scanner.nextLine();
                    if("es".equalsIgnoreCase(idioma) || "en".equalsIgnoreCase(idioma)|| "fr".equalsIgnoreCase(idioma)|| "pt".equalsIgnoreCase(idioma)){

                        libroService.listarLibrosPorIdioma(idioma).forEach(libro -> {
                            System.out.println("Libro");
                            System.out.println("Titulo: "+libro.getTitulo());
                            System.out.println("Autor: " + (libro.getAutor() != null ? libro.getAutor().getNombre() : "Desconocido"));
                            System.out.println("Idioma: "+libro.getIdioma());
                            System.out.println("Número de descargas: "+libro.getNumeroDescargas());
                        });
                    }else{

                        System.out.println("Idioma no válido intente de nuevo");
                    }
                    break;

                case 0:
                    System.out.println("Salir ");
                    break;

                default:
                    System.out.println("Opción no válida");
            }



        }while (opcion!=0);

        scanner.close();
    }

    private void mostrarDetallesLibro(LibroDTO libroDTO){
        System.out.println("Libro");
        System.out.println("Titulo: "+libroDTO.getTitulo());
        System.out.println("Autor: " + (libroDTO.getAutores().isEmpty() ? "Desconocido" : libroDTO.getAutores().get(0).getNombre()));
        System.out.println("Idioma: "+libroDTO.getIdiomas().get(0));
        System.out.println("Número de descargas: "+libroDTO.getNumeroDescargas());
    }
}