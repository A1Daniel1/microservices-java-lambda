package co.edu.escuelaing.services;

public class Respuesta {
    private String mensaje;
    private int suma;

    public Respuesta() {}

    public Respuesta(String mensaje, int suma) {
        this.mensaje = mensaje;
        this.suma = suma;
    }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public int getSuma() { return suma; }
    public void setSuma(int suma) { this.suma = suma; }
}