package servicio;

import modelo.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class Inventario {
    private Connection conexion;
    private final String url = "jdbc:mysql://localhost:3306/tienda_online";
    private final String user = "root";
    private final String password = "alumno";



    public Inventario() throws SQLException {
        conexion = DriverManager.getConnection(url, user, password);

    }
    /*

     */
    public boolean insertarProductoElectronico(Producto producto) throws SQLException {
        Electronico electronico = (Electronico) producto;
        conexion.setAutoCommit(false);
        String sql="INSERT INTO productos\n" +
                "( tipo, nombre, precio, stock)\n" +
                "VALUES(?,?, ?, ?);";
        PreparedStatement pst=conexion.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
        pst.setString(1,"Electronico");
        pst.setString(2,electronico.getNombre());
        pst.setDouble(3,electronico.getPrecio());
        pst.setInt(4,electronico.getStock());
        int filas=pst.executeUpdate();
        if (filas>0){
            ResultSet rs=pst.getGeneratedKeys();
            if (rs.next()){
                int idProducto=rs.getInt(1);
                String sql1="INSERT INTO electronicos\n" +
                        "(id, marca, garantia)\n" +
                        "VALUES(?,?, ?);";
                PreparedStatement pst1=conexion.prepareStatement(sql1);
                pst1.setInt(1,idProducto);
                pst1.setString(2, electronico.getMarca());
                pst1.setDouble(3, electronico.getGarantia());
                int filas1=pst1.executeUpdate();
                if (filas1>0){
                    conexion.setAutoCommit(true);
                    rs.close();
                    pst1.close();
                    pst.close();
                    return true;
                }
            }

        }
        conexion.rollback();
        return false;
    }
    public boolean insertarProductoRopa(Producto producto) throws SQLException {
        Ropa ropa = (Ropa) producto;
        conexion.setAutoCommit(false);
        String sql="INSERT INTO productos\n" +
                "( tipo, nombre, precio, stock)\n" +
                "VALUES(?,?, ?, ?);";
        PreparedStatement pst=conexion.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
        pst.setString(1,"ropa");
        pst.setString(2,ropa.getNombre());
        pst.setDouble(3,ropa.getPrecio());
        pst.setInt(4,ropa.getStock());
        int filas=pst.executeUpdate();
        if (filas>0){
            ResultSet rs=pst.getGeneratedKeys();
            if (rs.next()){
                int idProducto=rs.getInt(1);
                String sql1="INSERT INTO ropa\n" +
                        "(id, talla, material)\n" +
                        "VALUES(?,?, ?);";
                PreparedStatement pst1=conexion.prepareStatement(sql1);
                pst1.setInt(1,idProducto);
                pst1.setString(2, ropa.getTalla());
                pst1.setString(3, ropa.getMaterial());
                int filas1=pst1.executeUpdate();
                if (filas1>0){
                    conexion.setAutoCommit(true);
                    rs.close();
                    pst1.close();
                    pst.close();
                    return true;
                }
            }

        }
        conexion.rollback();
        return false;
    }
    /**
     *
     * @param producto
     * @return true si el inserta el producto en el invenario si el producto pasado cómo parametro el id no está en el inventario
     */
    public boolean insertarProducto(Producto producto) throws SQLException {
        if (producto instanceof Ropa){
            return insertarProductoRopa(producto);
        }
        else{

            return  insertarProductoElectronico(producto);
        }



    }

    public ArrayList<Producto> getListaProductos() throws SQLException {
        ArrayList<Producto> listaProductos=new ArrayList<>();
        String sql="select p.id, p.nombre,p.precio,p.stock,marca, garantia \n" +
                "from productos p, electronicos e \n" +
                "where p.id=e.id";
        PreparedStatement pst=conexion.prepareStatement(sql);

        ResultSet rs=pst.executeQuery();

        while(rs.next()){

            Electronico productoElectronico=new Electronico(rs.getInt(1),rs.getString(2),rs.getDouble(3),rs.getInt(4),rs.getString(5),rs.getInt(6));
            listaProductos.add(productoElectronico);
        }

        return listaProductos;
    }



    /**
     *
     * @param idProducto a vender
     * @param cantidad de producto a vender
     * @return true si realiza la venta porque hay suficente stock, devuelve false si no hay suficiente stock
      */
    public boolean venderProducto(int idProducto, int cantidad) throws SQLException {
        //Comprobar si hay stok suficiente
        String sql="select stock\n" +
                "from productos\n" +
                "where id=?";


            PreparedStatement setenciaP=conexion.prepareStatement(sql);
            setenciaP.setInt(1,idProducto);
            ResultSet rs=setenciaP.executeQuery();
            rs.next();
            if (rs.getInt(1)>=cantidad){
                String sql1="update productos set stock=stock-? where id=?";
                PreparedStatement pst1=conexion.prepareStatement(sql1);
                pst1.setInt(1,cantidad);
                pst1.setInt(2,idProducto);
                pst1.executeUpdate();
                return true;

            }
            else{
                return false;
            }


    }

    /**
     *
     * @param idProducto a reponer
     * @param cantidad de producto a reponer
     *
     */
    public boolean reponerProducto(int idProducto, int cantidad) throws SQLException {
        String sql="update productos set stock=stock+? where id=?";
        PreparedStatement pst1=conexion.prepareStatement(sql);
        pst1.setInt(1,cantidad);
        pst1.setInt(2,idProducto);
        pst1.executeUpdate();
        return true;


    }
}
