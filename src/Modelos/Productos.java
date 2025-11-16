
package Modelos;


public class Productos {
    
    private double precio, costo;
    private int cantidad, cantidadMinima;
    private String fechaEntrada, categoria, nombre, lugar, precioMostrar, descripcion, costoMostrar;
    private String imagen;

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    
    public String getCostoMostrar() {
        return costoMostrar;
    }

    public void setCostoMostrar(String costoMostrar) {
        this.costoMostrar = costoMostrar;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPrecioMostrar() {
        return precioMostrar;
    }

    public void setPrecioMostrar(String precioMostrar) {
        this.precioMostrar = precioMostrar;
    }
    
   
    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getCantidadMinima() {
        return cantidadMinima;
    }

    public void setCantidadMinima(int cantidadMinima) {
        this.cantidadMinima = cantidadMinima;
    }

    public String getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(String fechaEntradaString) {
        this.fechaEntrada = fechaEntradaString;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoriaString) {
        this.categoria = categoriaString;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }


    
    
    
}
