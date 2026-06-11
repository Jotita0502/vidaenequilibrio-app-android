package com.example.vidaenequilibrio.models;

public class Receta {
    private int id;
    private String nombre;
    private String descripcion;
    private String ingredientes;

    public Receta(int id, String nombre, String descripcion, String ingredientes) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ingredientes = ingredientes;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getIngredientes() {
        return ingredientes;
    }
}
