
package Modelos;


public class VentaProductos {
    
    private String nombre, imgUrl, precioMostrar;

    public String getPrecioMostrar() {
        return precioMostrar;
    }

    public void setPrecioMostrar(String precioMostrar) {
        this.precioMostrar = precioMostrar;
    }
    private double precio;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
    
    
    
}
