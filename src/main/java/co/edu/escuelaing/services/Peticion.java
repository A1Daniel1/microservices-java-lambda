package co.edu.escuelaing.services;

public class Peticion {
    private String nombre;
    private int a;
    private int b;

    public Peticion() {}   // constructor vacío obligatorio

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getA() { return a; }
    public void setA(int a) { this.a = a; }
    public int getB() { return b; }
    public void setB(int b) { this.b = b; }
}