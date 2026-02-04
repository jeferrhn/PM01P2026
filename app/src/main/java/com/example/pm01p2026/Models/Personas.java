package com.example.pm01p2026.Models;

public class Personas
{
    private Integer id;
    private String nombres;
    private String apellido;
    private Integer edad;
    private String correo;

    public Personas() {
    }
    public Personas(String nombres, Integer id, String apellido, Integer edad, String correo) {
        this.nombres = nombres;
        this.id = id;
        this.apellido = apellido;
        this.edad = edad;
        this.correo = correo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
