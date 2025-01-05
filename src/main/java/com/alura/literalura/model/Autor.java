package com.alura.literalura.model;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "autores")
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private int anoNacimiento;

    private int anoFallecimiento;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Libro> libros;


    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getAnoNacimiento() {
        return anoNacimiento;
    }

    public void setAnoNacimiento(int anoNacimiento) {
        this.anoNacimiento = anoNacimiento;
    }

    public int getAnoFallecimiento() {
        return anoFallecimiento;
    }

    public void setAnoFallecimiento(int anoFallecimiento) {
        this.anoFallecimiento = anoFallecimiento;
    }

    public List<Libro> getLibros() {
        return libros;
    }

    public void setLibros(List<Libro> libros) {
        this.libros = libros;
    }

    // toString


    @Override
    public String toString() {
        return "Autor{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", anoFallecimiento=" + anoFallecimiento +
                ", anoNacimiento=" + anoNacimiento +
                ", libros=" + libros +
                '}';
    }
}
