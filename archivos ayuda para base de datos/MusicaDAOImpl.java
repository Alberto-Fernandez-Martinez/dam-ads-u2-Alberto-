package Dao;

import Conexiones.MySQLConnection;
import Modelo.MusicaConstructor;
import java.sql.*;
import java.util.ArrayList;

public class MusicaDAOImpl implements MusicaDAO {

    /**
     * Variable de instancia que almacena la conexión a la base de datos.
     * (final) significa que no se puede cambiar después de asignarse en el
     * constructor. Variable pública que usamos para indicar si una operación
     * fue exitosa. Constructor de la clase MusicaDAOImpl que recibe una
     * conexión a la base de datos. Asignamos la conexión pasada como parámetro
     * a la variable de instancia (conexion).
     */
    private final Connection conexion;
    public boolean FUNCIONA = false;

    public MusicaDAOImpl(Connection conn) {
        this.conexion = conn;

    }

    /**
     * Función para insertando los discos. Obtenemos una nueva conexión a la
     * base de datos. Creamos una Variable para indicar si la operación fue
     * exitosa. Hacemos la consulta SQL de inserción con parámetros. Asignamos
     * los valores del objeto (disco) a la consulta. Ejecutamos la inserción y
     * se guarda si fue exitosa. Manejamos los errores si falla la inserción.
     * Cerramos la conexión a la base de datos. Retornamos true si la inserción
     * fue exitosa, false si no. Manejamos los errores si falla al abrir la
     * conexión. Retornamos false si hubo alguna excepción general.
     *
     * @param disco
     * @return
     */
    @Override
    public boolean insertarDiscoAlberto(MusicaConstructor disco) {
        try {
            Connection con = MySQLConnection.newInstance();
            FUNCIONA = false;

            try {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO Disco (id, titulo, artista, precio, fecha_lanzamiento, disponible) VALUES (?, ?, ?, ?, ?, ?)"
                );

                ps.setInt(1, disco.getId());
                ps.setString(2, disco.getTitulo());
                ps.setString(3, disco.getArtista());
                ps.setObject(4, disco.getPrecio(), java.sql.Types.DOUBLE); // comprobacion para poder guardar null en la base de datos usamos un setObject
                ps.setObject(5, disco.getFechaLanzamiento(), java.sql.Types.DATE); // comprobacion para poder guardar null en la base de datos usamos un setObject
                ps.setBoolean(6, disco.isDisponible());

                int ValorConsulta = ps.executeUpdate();
                FUNCIONA = ValorConsulta > 0;

            } catch (SQLException ex) {
                System.out.println("Error al insertar disco: " + ex.getMessage());
            }

            MySQLConnection.cerrarConexion(con);
            return FUNCIONA;

        } catch (SQLException ex) {
            System.out.println("Error de conexión: " + ex.getMessage());
        }

        return false;
    }

    /**
     * Funcion para actializar discos Obtenemos una nueva conexión a la base de
     * datos Hacemos la consulta SQL para actualizar un disco Asignamos los
     * valores del objeto (disco) a la consulta Se va actualizando y se verifica
     * si afectó alguna fila y se cierra la conexion con la base de datos
     *
     * @param disco
     * @return
     */
    @Override
    public boolean actualizarDiscoAlberto(MusicaConstructor disco) {
        try {
            Connection con = MySQLConnection.newInstance();
            FUNCIONA = false;

            try {
                PreparedStatement ps = con.prepareStatement("UPDATE Disco SET titulo=?, artista=?, precio=?, fecha_lanzamiento=?, disponible=? WHERE id=?");

                ps.setString(1, disco.getTitulo());
                ps.setString(2, disco.getArtista());
                ps.setObject(3, disco.getPrecio(), java.sql.Types.DOUBLE); // comprobacion para poder guardar null en la base de datos usamos un setObject
                ps.setObject(4, disco.getFechaLanzamiento(), java.sql.Types.DATE); // comprobacion para poder guardar null en la base de datos usamos un setObject
                ps.setBoolean(5, disco.isDisponible());
                ps.setInt(6, disco.getId());

                int ValorConsulta = ps.executeUpdate();
                FUNCIONA = ValorConsulta > 0;

            } catch (SQLException ex) {
                System.out.println("Error al actualizar disco: " + ex.getMessage());
            }

            MySQLConnection.cerrarConexion(con);
            return FUNCIONA;

        } catch (SQLException ex) {
            System.out.println("Error de conexión: " + ex.getMessage());
        }

        return false;
    }

    /**
     * Funcion para obtener discos por id Obtenemos una nueva conexión a la base
     * de datos Inicializamos el objeto que contendrá los datos del disco
     * Hacemos la consulta SQL para obtener un disco por su ID Establecemos el
     * parámetro de ID en la consulta Ejecutamos la consulta Si se encuentra un
     * resultado, se crea un objeto MusicaConstructor con los datos obtenidos
     * Cerramos la conexion con la base de datos En el caso de fallo se
     * devolvera un null
     *
     * @param id
     * @return
     */
    @Override
    public MusicaConstructor obtenerDiscoPorIdAlberto(int id) {
        try {
            Connection con = MySQLConnection.newInstance();
            MusicaConstructor disco = null;

            try {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM Disco WHERE id=?");
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    disco = new MusicaConstructor(rs.getInt("id"), rs.getString("titulo"), rs.getString("artista"), rs.getObject("precio") != null ? rs.getDouble("precio") : null, rs.getDate("fecha_lanzamiento"), rs.getBoolean("disponible"));

                }

            } catch (SQLException ex) {
                System.out.println("Error al obtener la id del disco: " + ex.getMessage());
            }

            MySQLConnection.cerrarConexion(con);
            return disco;

        } catch (SQLException ex) {
            System.out.println("Error de conexión: " + ex.getMessage());
        }

        return null;
    }

    /**
     * Funcion para eliminar discos por id Obtenemos nueva conexión a la base de
     * datos Hacemos la consulta SQL para eliminar un disco por su ID
     * Establecemos el ID del disco a eliminar Realizamos la consulta y vemos
     * las filas afectadas Funciona si se eliminos minimo un registro de la
     * operacion Cerramos la conexion con la base de datos Devolvemos el
     * resultado de la operacion Y devuelve un falso si hay algun error
     *
     * @param id
     * @return
     */
    @Override
    public boolean eliminarDiscoPorIdAlberto(int id) {
        try {
            Connection con = MySQLConnection.newInstance();
            FUNCIONA = false;

            try {
                PreparedStatement ps = con.prepareStatement("DELETE FROM Disco WHERE id=?");
                ps.setInt(1, id);
                int ValorConsulta = ps.executeUpdate();
                FUNCIONA = ValorConsulta > 0;

            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }

            MySQLConnection.cerrarConexion(con);
            return FUNCIONA;

        } catch (SQLException ex) {
            System.out.println("Error de conexión: " + ex.getMessage());
        }

        return false;
    }

    /**
     * Funcion para eliminar discos objeto Obtenemos una nueva conexion a la
     * base de datos Hacemos una varaible booleana que indicara si la
     * eliminación fue exitosa Hacemos la consulta y verificamos si se elimino
     * un registro como minimo Establecemos el id del disco a eliminar Cerramos
     * la conexion con la base de datos Devolvemos el resultado de la funcion
     * Ahora devolvemos un false si hay algun problema
     *
     * @param disco
     * @return
     */
    @Override
    public boolean eliminarDiscoAlberto(MusicaConstructor disco) {
        try {
            Connection con = MySQLConnection.newInstance();

            FUNCIONA = false;

            try {
                PreparedStatement ps = con.prepareStatement("DELETE FROM Disco WHERE id=?");
                ps.setInt(1, disco.getId());
                int ValorConsulta = ps.executeUpdate();
                FUNCIONA = ValorConsulta > 0;
            } catch (SQLException e) {
                System.out.println("Error al eliminar disco: " + e.getMessage());
            }

            MySQLConnection.cerrarConexion(con);
            return FUNCIONA;

        } catch (SQLException ex) {
            System.out.println("Error de conexión: " + ex.getMessage());
        }

        return false;
    }

    /**
     * Funcion para ver si existe los discos Obtenemos una nueva conexion a la
     * base de datos Hacemos una varaible booleana que indicara si el disco
     * existe Hacemos la consulta y verificamos si se elimino un registro como
     * minimo Establecemos el id de la consulta Ejecutamos la consulta Si el
     * resultado es un regsitro o mas existe la booleana funciona Cerramos la
     * conexion con la base de datos Devolvemos el resultado de la funcion Ahora
     * devolvemos un false si hay algun problema
     *
     * @param disco
     * @return
     */
    @Override
    public boolean existeDiscoAlberto(MusicaConstructor disco) {
        try {
            Connection con = MySQLConnection.newInstance();

            FUNCIONA = false;

            try {
                PreparedStatement ps = con.prepareStatement("SELECT 1 FROM Disco WHERE id=?");
                ps.setInt(1, disco.getId());
                ResultSet rs = ps.executeQuery();
                FUNCIONA = rs.next();

            } catch (SQLException e) {
                System.out.println("Error al verificar existencia: " + e.getMessage());

            }

            MySQLConnection.cerrarConexion(con);
            return FUNCIONA;

        } catch (SQLException ex) {
            System.out.println("Error de conexión: " + ex.getMessage());
        }

        return false;
    }

    /**
     * Funcion para obtener todos los discos Creamos un ArrayList que almacenará
     * los discos obtenidos Obtenemos la conexion a la base de datos Hacemos la
     * consulta que seleciona todos los regsitros de la tabla Disco Ahora con un
     * while recorremos el resultado de la consulta y creamos el objeto de
     * MusicaConstructor para cada fila Cerramos la conexion con la base de
     * datos y devolvemos el ArrayList de discos obtenida
     *
     * @return
     */
    @Override
    public ArrayList<MusicaConstructor> obtenerTodosDiscosAlberto() {
        ArrayList<MusicaConstructor> DiscoDevolver = new ArrayList<>();

        try {
            Connection con = MySQLConnection.newInstance();

            try {

                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM Disco");

                while (rs.next()) {
                    DiscoDevolver.add(new MusicaConstructor(rs.getInt("id"), rs.getString("titulo"), rs.getString("artista"), rs.getObject("precio") != null ? rs.getDouble("precio") : null, rs.getDate("fecha_lanzamiento"), rs.getBoolean("disponible")));

                }

            } catch (SQLException e) {
                System.out.println("Error al obtener discos: " + e.getMessage());

            }

            MySQLConnection.cerrarConexion(con);
        } catch (SQLException ex) {
            System.out.println("Error de conexión: " + ex.getMessage());
        }

        return DiscoDevolver;
    }

    /**
     * Funcion para obtener en numero total de discos Creamos una varaible para
     * almacenar en total de discos Obtenemos la conexion con la base de datos
     * Creamos un Statement para ejecutar una consulta sin parámetros Hacemos
     * consulta SQL que cuenta todos los registros de la tabla Disco Hacemos el
     * que el cursor pase a la primera fila y obtenemos el numero total La
     * primera columna que es el Id contiene el total de discos Cerramos la
     * conexion con la base de datos y devolvemos el resultado de los discos
     *
     * @return
     */
    @Override
    public int totalDiscosBDAlberto() {
        int DiscosTotales = 0;
        try {
            Connection con = MySQLConnection.newInstance();
            try {

                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Disco");
                rs.next();
                DiscosTotales = rs.getInt(1);

            } catch (SQLException e) {
                System.out.println("Error al contar discos: " + e.getMessage());
            }

            MySQLConnection.cerrarConexion(con);
        } catch (SQLException ex) {
            System.out.println("Error de conexión: " + ex.getMessage());
        }
        return DiscosTotales;
    }

    /**
     * Funcion para obtener el disco mas caro Hacemos una varaible para
     * almacenar el disco mas caro Obtenemos la conexion con la base de datos
     * Creamos un Statement para ejecutar la base de datos Creamos la consulta
     * sql que seleciona el dsico mas caro Ahora creamos el objeto con los datos
     * en MusicaConstructor si existe algun dato Cerramos la conexion con la
     * base de datos Y devolvemos el disco mas caro con el return.
     *
     * @return
     */
    @Override
    public MusicaConstructor obtenerDiscoMasCaroAlberto() {
        MusicaConstructor discoCaro = null;
        try {
            Connection con = MySQLConnection.newInstance();

            try {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM Disco WHERE precio = (SELECT MAX(precio) FROM Disco WHERE precio IS NOT NULL);");

                if (rs.next()) {

                    discoCaro = new MusicaConstructor(rs.getInt("id"), rs.getString("titulo"), rs.getString("artista"), rs.getObject("precio") != null ? rs.getDouble("precio") : null, rs.getDate("fecha_lanzamiento"), rs.getBoolean("disponible"));

                }

            } catch (SQLException e) {
                System.out.println("Error al obtener disco más caro: " + e.getMessage());
            }

            MySQLConnection.cerrarConexion(con);
        } catch (SQLException ex) {
            System.out.println("Error de conexión: " + ex.getMessage());
        }

        return discoCaro;

    }

    /**
     * Funcion para obtener el disco por rango de precio Hacemos el ArrayList
     * para almacenar los discos encontrados Obtenemos la conexion con la base
     * de datos Hacemos la consulta para obtener los datos de la base de datos
     * Creamos los valores para el precio minimo y el precio maximo Ejecutamos
     * la consulta Ahora recorremos los datos y cremamos el objeto de
     * MusicaConstructor por cada disco Cerramos la conexion con la base de
     * datos. Y devolvemos la lista con los disco que se encuentran en el precio
     * indicado
     *
     * @param precioMin
     * @param precioMax
     * @return
     */
    @Override
    public ArrayList<MusicaConstructor> obtenerDiscosPorRangoDePreciosAlberto(double precioMin, double precioMax) {
        ArrayList<MusicaConstructor> DiscoDevolverPrecioRango = new ArrayList<>();

        try {
            Connection con = MySQLConnection.newInstance();

            try {

                PreparedStatement ps = con.prepareStatement("SELECT * FROM Disco WHERE precio BETWEEN ? AND ?");
                ps.setDouble(1, precioMin);
                ps.setDouble(2, precioMax);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    DiscoDevolverPrecioRango.add(new MusicaConstructor(rs.getInt("id"), rs.getString("titulo"), rs.getString("artista"), rs.getObject("precio") != null ? rs.getDouble("precio") : null, rs.getDate("fecha_lanzamiento"), rs.getBoolean("disponible")));
                }

            } catch (SQLException e) {
                System.out.println("Error al obtener discos por rango de precios: " + e.getMessage());
            }

            MySQLConnection.cerrarConexion(con);

        } catch (SQLException ex) {
            System.out.println("Error de conexión: " + ex.getMessage());
        }

        return DiscoDevolverPrecioRango;
    }

    /**
     * Funcion para obtener el disco por una Letra Creamos el ArryList para
     * almacenar los discos obtenidos de la base de datos Obtenemos la conexion
     * con la base de datos Hacemos la consulta SQL para buscar discos cuyo
     * título comience con la letra proporcionada Ejecutamos la consulta
     * Recorremos los resultados con el While y creamos el objeto
     * MusicaConstructor Cerramos la conexion con la base de datos Y devolvemos
     * el ArryaList con los discos obtenidos
     *
     * @param letra
     * @return
     */
    @Override
    public ArrayList<MusicaConstructor> obtenerDiscosComiencenLetraAlberto(char letra) {
        ArrayList<MusicaConstructor> discosObtenidosPorLetra = new ArrayList<>();

        try {
            Connection con = MySQLConnection.newInstance();

            try {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM Disco WHERE titulo LIKE ?");
                ps.setString(1, letra + "%");
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    discosObtenidosPorLetra.add(new MusicaConstructor(rs.getInt("id"), rs.getString("titulo"), rs.getString("artista"), rs.getObject("precio") != null ? rs.getDouble("precio") : null, rs.getDate("fecha_lanzamiento"), rs.getBoolean("disponible")));
                }

            } catch (SQLException e) {
                System.out.println("Error al obtener discos por letra: " + e.getMessage());
            }

            MySQLConnection.cerrarConexion(con);
        } catch (SQLException ex) {
            System.out.println("Error de conexión: " + ex.getMessage());
        }

        return discosObtenidosPorLetra;
    }

    /**
     * Funcion para obtener los discos no disponibles Creamos un ArrayList para
     * almacenar los discos no disponibles obtenidos de la base de datos
     * Obtenemos la conexión con la base de datos Hacemos la consulta SQL para
     * seleccionar discos no disponibles, ordenados por artista Ejecutar la
     * consulta Recorremos los discos y agregamos cada dsico al ArrayList
     * Cerramos la conexion con la base de datos Devolvemos la lista de discos
     * no disponibles
     *
     * @return
     */
    @Override
    public ArrayList<MusicaConstructor> obtenerDiscosNoDisponiblesOrdenadosPorArtistaAlberto() {
        ArrayList<MusicaConstructor> discosNoDisponibles = new ArrayList<>();

        try {
            Connection con = MySQLConnection.newInstance();

            try {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM Disco WHERE disponible = 0 ORDER BY artista ASC");
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    discosNoDisponibles.add(new MusicaConstructor(rs.getInt("id"), rs.getString("titulo"), rs.getString("artista"), rs.getObject("precio") != null ? rs.getDouble("precio") : null, rs.getDate("fecha_lanzamiento"), rs.getBoolean("disponible")));
                }

            } catch (SQLException e) {
                System.out.println("Error al obtener discos por letra: " + e.getMessage());
            }
            MySQLConnection.cerrarConexion(con);
        } catch (SQLException e) {
            System.err.println("Error al obtener discos no disponibles: " + e.getMessage());
        }

        return discosNoDisponibles;
    }

    /**
     * Funcion para obtener lel precio con discos Creamos un ArrayList para
     * almacenar los discos con precio mayor a 0 o se encuentren en la base de
     * datos Obtenemos la conexión con la base de datos Hacemos la consulta SQL
     * para obtener los discos cuyo precio es mayor a 0 Ejecutamos la consulta
     * Recorremos los discos y agregamos cada disco al ArrayList mediante el
     * Objeto MusicaConstructor Cerramos la conexion con la base de datos
     * Devolvemos la lista de discos con precio
     *
     * @return
     */
    @Override
    public ArrayList<MusicaConstructor> obtenerDiscosConPrecioAlberto() {
        ArrayList<MusicaConstructor> discosConPrecio = new ArrayList<>();

        try {
            Connection con = MySQLConnection.newInstance();
            try {

                PreparedStatement ps = con.prepareStatement("SELECT * FROM Disco WHERE precio > 0");
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    discosConPrecio.add(new MusicaConstructor(rs.getInt("id"), rs.getString("titulo"), rs.getString("artista"), rs.getObject("precio") != null ? rs.getDouble("precio") : null, rs.getDate("fecha_lanzamiento"), rs.getBoolean("disponible")));
                }
            } catch (SQLException e) {
                System.out.println("Error al obtener discos por letra: " + e.getMessage());
            }
            MySQLConnection.cerrarConexion(con);

        } catch (SQLException e) {
            System.err.println("Error al obtener discos con precio: " + e.getMessage());
        }

        return discosConPrecio;
    }

    /**
     * Funcion para obtener discos por artista Creamos un ArrayList para
     * almacenar los discos encontrados del artista Obtenemos la conexión con la
     * base de datos Hacemos la consulta SQL para seleccionar los discos del
     * artista Ejecutamos la consulta Recorremos los discos y agregamos cada
     * disco al ArrayList mediante el Objeto MusicaConstructor Cerramos la
     * conexion con la base de datos Devolvemos el ArrayList de discos
     *
     * @param artista
     * @return
     */
    @Override
    public ArrayList<MusicaConstructor> obtenerDiscosArtistaAlberto(String artista) {
        ArrayList<MusicaConstructor> obtenerDsicosArtista = new ArrayList<>();
        try {
            Connection con = MySQLConnection.newInstance();
            try {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM Disco WHERE artista = ?");
                ps.setString(1, artista);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    obtenerDsicosArtista.add(new MusicaConstructor(rs.getInt("id"), rs.getString("titulo"), rs.getString("artista"), rs.getObject("precio") != null ? rs.getDouble("precio") : null, rs.getDate("fecha_lanzamiento"), rs.getBoolean("disponible")));
                }

            } catch (SQLException e) {
                System.err.println("Error al obtener discos con precio: " + e.getMessage());
            }
            MySQLConnection.cerrarConexion(con);
        } catch (SQLException e) {
            System.err.println("Error al obtener discos del artista: " + e.getMessage());
        }

        return obtenerDsicosArtista;
    }

    /**
     * Funcion para obtener Disco nuevo Creamos la varaible para almacenar el
     * disco mas reciente Obtenemos la conexión con la base de datos Hacemos la
     * consulta SQL para obtener el disco más reciente según la fecha de
     * lanzamiento Ejecutamos la consulta Recorremos los discos y agregamos cada
     * disco al ArrayList mediante el Objeto MusicaConstructor Cerramos la
     * conexion con la base de datos Devolvemos el disco más reciente, o null si
     * no se encontró ninguno.
     *
     * @return
     */
    @Override
    public MusicaConstructor obtenerDiscoNovedadAlberto() {
        MusicaConstructor obtenerDiscosNovedad = null;
        try {
            Connection con = MySQLConnection.newInstance();
            try {

                PreparedStatement ps = con.prepareStatement("SELECT * FROM Disco ORDER BY fecha_lanzamiento DESC LIMIT 1");
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    obtenerDiscosNovedad = new MusicaConstructor(rs.getInt("id"), rs.getString("titulo"), rs.getString("artista"), rs.getObject("precio") != null ? rs.getDouble("precio") : null, rs.getDate("fecha_lanzamiento"), rs.getBoolean("disponible"));
                }
            } catch (SQLException e) {
                System.err.println("Error al obtener discos del artista: " + e.getMessage());
            }
            MySQLConnection.cerrarConexion(con);
        } catch (SQLException e) {
            System.err.println("Error al obtener el disco más reciente: " + e.getMessage());
        }

        return obtenerDiscosNovedad;
    }

    /**
     * *
     * Funcion para Obtener dsicos disponibles ordenados por (T o A) Creamos un
     * ArrayList para almacenar los discos los discos disponibles ordenados
     * Determinamos el campo por el cual se ordenarán los resultados: 'A' para
     * artista, 'T' para título Obtenemos la conexión con la base de datos
     * Hacemos la consulta SQL para obtener discos disponibles ordenados por el
     * campo seleccionado Ejecutamos la consulta Recorremos los discos y
     * agregamos cada disco al ArrayList mediante el Objeto MusicaConstructor
     * Cerramos la conexion con la base de datos Devolvemos la lista de discos
     * ordenados
     *
     * @param ordenacion
     * @return
     */
    @Override
    public ArrayList<MusicaConstructor> obtenerDiscosDisponiblesOrdenadosPorAlberto(char ordenacion) {
        ArrayList<MusicaConstructor> ObtenerDiscoDisponibleOrdenados = new ArrayList<>();
        String campoOrden = (ordenacion == 'A' || ordenacion == 'a') ? "artista" : (ordenacion == 'T' || ordenacion == 't') ? "titulo" : null;
        try {
            Connection con = MySQLConnection.newInstance();

            try {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM Disco WHERE disponible = 1 ORDER BY " + campoOrden + " ASC");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    ObtenerDiscoDisponibleOrdenados.add(new MusicaConstructor(rs.getInt("id"), rs.getString("titulo"), rs.getString("artista"), rs.getObject("precio") != null ? rs.getDouble("precio") : null, rs.getDate("fecha_lanzamiento"), rs.getBoolean("disponible")));
                }
            } catch (SQLException e) {
                System.err.println("Error al obtener discos ordenados: " + e.getMessage());
            }
            MySQLConnection.cerrarConexion(con);
        } catch (SQLException e) {
            System.err.println("Error al obtener el disco más reciente: " + e.getMessage());
        }

        return ObtenerDiscoDisponibleOrdenados;
    }

}
