package Gestiones;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import Modelos.Productos;
import Modelos.VentaProductos;
import java.util.ArrayList;
import java.util.List;

public class GestionProductos {

    public List<Productos> obtenerProductosDesdeBD() {
        List<Productos> lista = new ArrayList<>();

        String sql = "SELECT nombre, cantidad, precioMostrar, lugar FROM producto";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Productos p = new Productos();
                p.setNombre(rs.getString("nombre"));
                p.setCantidad(rs.getInt("cantidad"));
                p.setPrecioMostrar(rs.getString("precioMostrar")); // ya viene con formato $xx.xxx
                p.setLugar(rs.getString("lugar"));
                lista.add(p);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener productos: " + e.getMessage());
        }

        return lista;
    }
    
    public List<VentaProductos> obtenerProductosParaVentaProductos() {
        List<VentaProductos> lista = new ArrayList<>();

        String sql = """
            SELECT nombre, precio, url_imagen, cantidad FROM producto""";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                VentaProductos p = new VentaProductos();

                p.setNombre(rs.getString("nombre"));
                p.setPrecio(rs.getDouble("precio"));
                p.setImagen(rs.getString("url_imagen"));
                p.setCantidad(rs.getInt("cantidad")); 

                lista.add(p);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener productos para venta: " + e.getMessage());
        }

        return lista;
    }

    // Insertar nuevo producto (TODOS LOS CAMPOS)
    public boolean agregarProducto(Productos p) {
        String sql = """
            INSERT INTO producto 
            (nombre, categoria, lugar, cantidad, cantidadMinima, costo, costoMostrar, precio, precioMostrar, fechaEntrada, descripcion)
            VALUES (?, 
                    (SELECT id_categoria FROM categoria WHERE nombre_categoria = ?),
                    ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getCategoria());
            ps.setString(3, p.getLugar());
            ps.setDouble(4, p.getCantidad());
            ps.setDouble(5, p.getCantidadMinima());
            ps.setDouble(6, p.getCosto());
            ps.setString(7, p.getCostoMostrar());
            ps.setDouble(8, p.getPrecio());
            ps.setString(9, p.getPrecioMostrar());
            ps.setDate(10, java.sql.Date.valueOf(p.getFechaEntrada()));
            ps.setString(11, p.getDescripcion());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Error al agregar producto: " + e.getMessage());
            return false;
        }
    }

    public int obtenerIdPorNombre(String nombre) {
        int id = -1;
        String sql = "SELECT id_producto FROM producto WHERE nombre = ?";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id_producto");
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener ID por nombre: " + e.getMessage());
        }
        return id;
    }

    public boolean eliminarProducto(int idProducto) {
        String sql = "DELETE FROM producto WHERE id_producto = ?";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.out.println("⚠️ Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }

    public Productos informacionCompleta(int idProducto) {
        Productos producto = null;
        String sql = "SELECT * FROM producto WHERE id_producto = ?";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                producto = new Productos();
                producto.setNombre(rs.getString("nombre"));
                producto.setDescripcion(rs.getString("descripcion"));
                producto.setCategoria(rs.getString("categoria"));
                producto.setLugar(rs.getString("lugar"));
                producto.setCantidad(rs.getInt("cantidad"));
                producto.setCantidadMinima(rs.getInt("cantidadMinima"));
                producto.setCosto(rs.getDouble("costo"));
                producto.setCostoMostrar(rs.getString("costoMostrar"));
                producto.setPrecio(rs.getDouble("precio"));
                producto.setPrecioMostrar(rs.getString("precioMostrar"));
                producto.setFechaEntrada(rs.getString("fechaEntrada"));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener producto: " + e.getMessage());
        }

        return producto;
    }

    public boolean existeProducto(String nombreCategoria) {
        boolean existe = false;
        String sql = "SELECT COUNT(*) FROM producto WHERE nombre = ?";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreCategoria);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // COUNT(*) devuelve una sola columna, así que usamos índice 1
                int count = rs.getInt(1);
                existe = count > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return existe;
    }

    public boolean modificarProducto(Productos p,int idProducto) {
        String sql = "UPDATE producto SET nombre = ?, categoria = ?, lugar = ?, cantidad = ?, "
                + "cantidadMinima = ?, costo = ?, costoMostrar = ?, precio = ?, precioMostrar = ?, "
                + "fechaEntrada = ?, descripcion = ? WHERE id_producto = ?";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getCategoria());
            ps.setString(3, p.getLugar());
            ps.setInt(4, p.getCantidad());
            ps.setInt(5, p.getCantidadMinima());
            ps.setDouble(6, p.getCosto());
            ps.setString(7, p.getCostoMostrar());
            ps.setDouble(8, p.getPrecio());
            ps.setString(9, p.getPrecioMostrar());
            ps.setDate(10, java.sql.Date.valueOf(p.getFechaEntrada()));
            ps.setString(11, p.getDescripcion());
            ps.setInt(12, idProducto);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al modificar producto: " + e.getMessage());
            return false;
        }
    }

}
