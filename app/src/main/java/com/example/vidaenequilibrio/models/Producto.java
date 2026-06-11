package com.example.vidaenequilibrio.models;

public class Producto {
    private int id;
    private int usuario_id;
    private String nombre;
    private int cantidad;
    private String fecha_caducidad;
    private String estado;
    private String categoria;


    public int getId() { return id; }
    public int getUsuario_id() { return usuario_id; }
    public String getNombre() { return nombre; }
    public int getCantidad() { return cantidad; }
    public String getFecha_caducidad() { return fecha_caducidad; }
    public String getEstado() { return estado; }
    public String getCategoria(){return categoria; }

    public void setId(int id) { this.id = id; }
    public void setUsuario_id(int usuario_id) { this.usuario_id = usuario_id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public void setFecha_caducidad(String fecha_caducidad) { this.fecha_caducidad = fecha_caducidad; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setCategoria(String categoria) {this.categoria = categoria; }
}
