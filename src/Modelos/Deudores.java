
package Modelos;

public class Deudores {
    
    private String nombre, fechaLimite;
    private double totalAbonado, totalDebe; 
    private boolean estado;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(String fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public double getTotalAbonado() {
        return totalAbonado;
    }

    public void setTotalAbonado(double totalAbonado) {
        this.totalAbonado = totalAbonado;
    }

    public double getTotalDebe() {
        return totalDebe;
    }

    public void setTotalDebe(double totalDebe) {
        this.totalDebe = totalDebe;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
    
    
    
}
