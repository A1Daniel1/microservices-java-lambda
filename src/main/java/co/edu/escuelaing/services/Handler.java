package co.edu.escuelaing.services;

public class Handler {
    public Respuesta handleRequest(Peticion peticion) {
        String nombre = (peticion.getNombre() == null || peticion.getNombre().isBlank())
                ? "mundo" : peticion.getNombre();
        int suma = peticion.getA() + peticion.getB();
        return new Respuesta("Hola " + nombre, suma);
    }
}