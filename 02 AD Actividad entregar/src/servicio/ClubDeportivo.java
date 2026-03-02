package servicio;

import modelo.*;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Servicio principal del dominio del club deportivo.
 *
 * Esta clase centraliza:
 * 1) Estado en memoria de socios, pistas y reservas.
 * 2) Reglas de negocio sobre altas, bajas y validaciones.
 * 3) Persistencia contra MariaDB mediante JDBC.
 *
 * La UI debe invocar los metodos publicos de este servicio y evitar
 * implementar reglas de negocio directamente en las vistas.
 */
public class ClubDeportivo {

    private ArrayList<Socio> socios;
    private ArrayList<Pista> pistas;
    private ArrayList<Reserva> reservas;

    private Connection conexion;
    public final String URL_BD = "jdbc:mysql://localhost:3306/club_dama";
    public final String USER_BD = "root";
    public final String PASS_BD = "123";

    private boolean cambiosPendientes = false;

    /**
     * Construye el servicio y deja el estado listo para trabajar.
     *
     * Pasos de inicializacion:
     * 1) Crea las colecciones internas en memoria.
     * 2) Abre la conexion JDBC con la base de datos configurada.
     * 3) Carga socios, pistas y reservas desde BD.
     * 4) Deja el indicador de cambios pendientes a {@code false}.
     *
     * @throws SQLException si hay error de conexion o lectura inicial.
     */
    public ClubDeportivo() throws SQLException {
        socios = new ArrayList<>();
        pistas = new ArrayList<>();
        reservas = new ArrayList<>();

        try {
            conexion = DriverManager.getConnection(URL_BD, USER_BD, PASS_BD);
        } catch (SQLException e) {
            throw new RuntimeException("No se ha podido establecer la conexion con la base de datos.");
        }

        cargarSociosDesdeBd();
        cargarPistasDesdeBd();
        cargarReservasDesdeBd();

        cambiosPendientes = false;
    }

    /**
     * Devuelve la referencia actual de socios en memoria.
     *
     * Se utiliza para mostrar datos en UI y para operaciones de negocio
     * posteriores dentro del servicio.
     *
     * @return lista de socios en memoria.
     */
    public ArrayList<Socio> getSocios() {
        return socios;
    }

    /**
     * Devuelve la referencia actual de pistas en memoria.
     *
     * Se utiliza para mostrar disponibilidad y para validar reservas.
     *
     * @return lista de pistas en memoria.
     */
    public ArrayList<Pista> getPistas() {
        return pistas;
    }

    /**
     * Devuelve la referencia actual de reservas en memoria.
     *
     * Esta lista representa el estado de trabajo antes de persistir en BD.
     *
     * @return lista de reservas en memoria.
     */
    public ArrayList<Reserva> getReservas() {
        return reservas;
    }

    /**
     * Indica si el estado en memoria tiene cambios sin persistir.
     *
     * Se marca a {@code true} tras operaciones de alta/baja/modificacion y
     * vuelve a {@code false} despues de un guardado correcto.
     *
     * @return true si hay cambios sin persistir.
     */
    public boolean hayCambiosPendientes() {
        return cambiosPendientes;
    }

    /**
     * Marca internamente que existe una modificacion pendiente de guardar.
     *
     * Este metodo no persiste nada por si mismo; solo actualiza el flag
     * que controla si  debe ejecutar sincronizacion.
     */
    private void marcarCambios() {
        cambiosPendientes = true;
    }

    /**
     * Persiste en BD el estado actual de memoria.
     *
     * Comportamiento:
     * 1) Si no hay cambios pendientes, retorna sin hacer operaciones JDBC.
     * 2) Guarda socios y pistas mediante upsert.
     * 3) Sincroniza reservas (alta/actualizacion/bajas) y precios finales.
     * 4) Si tod va bien, limpia el indicador de cambios pendientes.
     *
     * @throws SQLException si ocurre un error de escritura.
     */
    public void guardarDatos() throws SQLException {
        if (!cambiosPendientes) {
            return;
        }

        guardarSociosEnBd();
        guardarPistasEnBd();
        guardarReservasEnBd();
        cambiosPendientes = false;
    }

    /**
     * Cierra de forma segura la conexion JDBC del servicio.
     *
     * Si aun existen cambios pendientes, intenta guardarlos antes del cierre
     * para no perder operaciones hechas en memoria.
     *
     * @throws SQLException si falla al cerrar.
     */
    public void cerrar() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            return;
        }
        if (hayCambiosPendientes()) {
            guardarDatos();
        }
        conexion.close();
    }


    //////////////////////////////////////////////////// SOCIOS /////////////////////////////////////////////////////////


    /**
     * Recarga la coleccion de socios desde la tabla {@code socios}.
     *
     * Limpia primero la lista en memoria y despues la rellena con lo que
     * actualmente existe en BD.
     *
     * @throws SQLException si falla la lectura.
     */
    private void cargarSociosDesdeBd() throws SQLException {
        socios.clear();

        String sql = "SELECT id_socio, dni, nombre, apellidos, telefono, email FROM socios";
        try (PreparedStatement pst = conexion.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                socios.add(new Socio(
                        rs.getString("id_socio"),
                        rs.getString("dni"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("telefono"),
                        rs.getString("email")
                ));
            }
        }
    }

    /**
     * Registra un nuevo socio en memoria.
     *
     * Validaciones aplicadas:
     * 1) El objeto no puede ser {@code null}.
     * 2) El identificador de socio es obligatorio.
     * 3) No se permite duplicar {@code idSocio} respecto a memoria.
     *
     * El alta no se escribe en BD hasta ejecutar guardado/cierre.
     *
     * @param socio socio a registrar.
     * @return true si se registra correctamente.
     */
    public boolean altaSocio(Socio socio) {
        if (socio == null) {
            throw new RuntimeException("Socio null");
        }
        if (socio.getIdSocio() == null || socio.getIdSocio().isBlank()) {
            throw new RuntimeException("idSocio obligatorio");
        }

        for (Socio s : socios) {
            if (s.getIdSocio().equals(socio.getIdSocio())) {
                throw new RuntimeException("Ya existe un socio con id: " + socio.getIdSocio());
            }
        }

        socios.add(socio);
        marcarCambios();
        return true;
    }

    /**
     * Da de baja un socio cumpliendo reglas de negocio.
     *
     * Reglas aplicadas:
     * 1) El id es obligatorio.
     * 2) Si tiene reservas futuras o en curso, se bloquea la baja.
     * 3) Si el socio no existe en memoria, se informa error.
     * 4) Si procede la baja, elimina tambien sus reservas en memoria.
     *
     * La baja se persiste en BD en el siguiente guardado/cierre.
     *
     * @param idSocio identificador del socio.
     * @return true si se elimina correctamente.
     */
    public boolean bajaSocio(String idSocio) {
        if (idSocio == null || idSocio.isBlank()) {
            throw new RuntimeException("idSocio obligatorio");
        }

        for (Reserva r : reservas) {
            if (r.getIdSocio().equals(idSocio)) {
                throw new RuntimeException("No se puede dar de baja: el socio tiene reservas asociadas");
            }
        }

        Socio encontrado = null;
        for (Socio s : socios) {
            if (s.getIdSocio().equals(idSocio)) {
                encontrado = s;
                break;
            }
        }
        if (encontrado == null) {
            throw new RuntimeException("Socio no encontrado: " + idSocio);
        }



        socios.remove(encontrado);
        marcarCambios();
        return true;
    }



    /**
     * Sincroniza socios de memoria hacia la tabla {@code socios}.
     *
     * Usa estrategia upsert ({@code INSERT ... ON DUPLICATE KEY UPDATE}) para
     * insertar nuevos socios o actualizar existentes por clave primaria.
     *
     * @throws SQLException si ocurre un error de escritura.
     */
    private void guardarSociosEnBd() throws SQLException {
        String sql = "INSERT INTO socios (id_socio, dni, nombre, apellidos, telefono, email) VALUES (?, ?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE dni = VALUES(dni), nombre = VALUES(nombre), apellidos = VALUES(apellidos), "
                + "telefono = VALUES(telefono), email = VALUES(email)";
        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            for (Socio socio : socios) {
                pst.setString(1, socio.getIdSocio());
                pst.setString(2, socio.getDni());
                pst.setString(3, socio.getNombre());
                pst.setString(4, socio.getApellidos());
                pst.setString(5, socio.getTelefono());
                pst.setString(6, socio.getEmail());
                pst.executeUpdate();
            }
        }
    }

    //////////////////////////////////////////////////// PISTAS /////////////////////////////////////////////////////////

    /**
     * Recarga la coleccion de pistas desde la tabla {@code pistas}.
     *
     * Limpia la lista en memoria y la vuelve a poblar con los datos leidos
     * en BD para mantener consistencia de estado al iniciar.
     *
     * @throws SQLException si falla la lectura.
     */
    private void cargarPistasDesdeBd() throws SQLException {
        pistas.clear();

        String sql = "SELECT id_pista, deporte, descripcion, disponible FROM pistas";
        try (PreparedStatement pst = conexion.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                pistas.add(new Pista(
                        rs.getString("id_pista"),
                        rs.getString("deporte"),
                        rs.getString("descripcion"),
                        rs.getBoolean("disponible")
                ));
            }
        }
    }

    /**
     * Registra una nueva pista en memoria.
     *
     * Validaciones aplicadas:
     * 1) El objeto no puede ser {@code null}.
     * 2) El identificador y el deporte son obligatorios.
     * 3) El deporte se normaliza a valores permitidos del dominio.
     * 4) No se permite duplicar {@code idPista} en memoria.
     *
     * La persistencia en BD se realiza al guardar/cerrar.
     *
     * @param pista pista a registrar.
     * @return true si se registra correctamente.
     */
    public boolean altaPista(Pista pista) {
        if (pista == null) {
            throw new RuntimeException("Pista null");
        }
        if (pista.getIdPista() == null || pista.getIdPista().isBlank()) {
            throw new RuntimeException("idPista obligatorio");
        }
        String deporte = pista.getDeporte();
        if (deporte == null || deporte.isBlank()) {
            throw new RuntimeException("deporte obligatorio");
        }
        deporte = deporte.trim().toLowerCase();
        if (deporte.equals("tenis")) {
            pista.setDeporte("tenis");
        } else if (deporte.equals("padel") || deporte.equals("pádel")) {
            pista.setDeporte("pádel");
        } else if (deporte.equals("futbol sala") || deporte.equals("fútbol sala")) {
            pista.setDeporte("fútbol sala");
        } else {
            throw new RuntimeException("Deporte invalido. Valores permitidos: tenis, pádel, fútbol sala");
        }

        for (Pista p : pistas) {
            if (p.getIdPista().equals(pista.getIdPista())) {
                throw new RuntimeException("Ya existe una pista con id: " + pista.getIdPista());
            }
        }

        pistas.add(pista);
        marcarCambios();
        return true;
    }

    /**
     * Cambia el estado operativo de una pista existente.
     *
     * Reglas aplicadas:
     * 1) El id de pista es obligatorio.
     * 2) La pista debe existir en memoria.
     * 3) Se actualiza el flag de disponibilidad y se marca cambio pendiente.
     *
     * @param idPista identificador de la pista.
     * @param disponible nuevo estado de disponibilidad.
     * @return true si se actualiza correctamente.
     */
    public boolean cambiarDisponibilidadPista(String idPista, boolean disponible) {
        if (idPista == null || idPista.isBlank()) {
            throw new RuntimeException("idPista obligatorio");
        }

        Pista encontrada = null;
        for (Pista p : pistas) {
            if (p.getIdPista().equals(idPista)) {
                encontrada = p;
                break;
            }
        }
        if (encontrada == null) {
            throw new RuntimeException("Pista no encontrada: " + idPista);
        }

        encontrada.setDisponible(disponible);
        marcarCambios();
        return true;
    }

    /**
     * Sincroniza pistas de memoria hacia la tabla {@code pistas}.
     *
     * Usa upsert para que una misma operacion gestione tanto altas nuevas
     * como cambios de datos/disponibilidad en pistas existentes.
     *
     * @throws SQLException si ocurre un error de escritura.
     */
    private void guardarPistasEnBd() throws SQLException {
        String sql = "INSERT INTO pistas (id_pista, deporte, descripcion, disponible) VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE deporte = VALUES(deporte), descripcion = VALUES(descripcion), disponible = VALUES(disponible)";
        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            for (Pista pista : pistas) {
                pst.setString(1, pista.getIdPista());
                pst.setString(2, pista.getDeporte());
                pst.setString(3, pista.getDescripcion());
                pst.setBoolean(4, pista.isDisponible());
                pst.executeUpdate();
            }
        }
    }

    //////////////////////////////////////////////////// RESERVAS /////////////////////////////////////////////////////////

    /**
     * Recarga la coleccion de reservas desde la tabla {@code reservas}.
     *
     * Se usa al iniciar para disponer en memoria de la agenda completa y
     * permitir validaciones de solapes y bajas de socios con reservas.
     *
     * @throws SQLException si falla la lectura.
     */
    private void cargarReservasDesdeBd() throws SQLException {
        reservas.clear();

        String sql = "SELECT id_reserva, id_socio, id_pista, fecha, hora_inicio, duracion_min, precio FROM reservas";
        try (PreparedStatement pst = conexion.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                reservas.add(new Reserva(
                        rs.getString("id_reserva"),
                        rs.getString("id_socio"),
                        rs.getString("id_pista"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getTime("hora_inicio").toLocalTime(),
                        rs.getInt("duracion_min"),
                        rs.getDouble("precio")
                ));
            }
        }
    }

    /**
     * Crea una nueva reserva en memoria con validaciones de negocio previas.
     *
     * Validaciones aplicadas antes de guardar:
     * 1) Reserva no nula y {@code idReserva} no duplicado.
     * 2) Socio y pista deben existir.
     * 3) La pista debe estar disponible.
     * 4) No puede existir solape horario con otra reserva de la misma pista
     *    en la misma fecha.
     *
     * La insercion fisica en BD se hace al guardar/cerrar usando el
     * procedimiento del script SQL.
     *
     * @param r reserva a crear.
     * @return true si la reserva se registra correctamente.
     */
    public boolean crearReserva(Reserva r) {
        if (r == null) {
            throw new RuntimeException("Reserva null");
        }

        for (Reserva x : reservas) {
            if (x.getIdReserva().equals(r.getIdReserva())) {
                throw new RuntimeException("Ya existe una reserva con id: " + r.getIdReserva());
            }
        }

        boolean socioExiste = false;
        for (Socio s : socios) {
            if (s.getIdSocio().equals(r.getIdSocio())) {
                socioExiste = true;
                break;
            }
        }
        if (!socioExiste) {
            throw new RuntimeException("El socio no existe: " + r.getIdSocio());
        }

        Pista pista = null;
        for (Pista p : pistas) {
            if (p.getIdPista().equals(r.getIdPista())) {
                pista = p;
                break;
            }
        }
        if (pista == null) {
            throw new RuntimeException("La pista no existe: " + r.getIdPista());
        }
        if (!pista.isDisponible()) {
            throw new RuntimeException("La pista no esta operativa (no disponible)");
        }

        LocalDateTime inicioNueva = LocalDateTime.of(r.getFecha(), r.getHoraInicio());
        LocalDateTime finNueva = inicioNueva.plusMinutes(r.getDuracionMin());
        for (Reserva existente : reservas) {
            if (!existente.getIdPista().equals(r.getIdPista())) {
                continue;
            }
            if (!existente.getFecha().equals(r.getFecha())) {
                continue;
            }

            LocalDateTime inicioExistente = LocalDateTime.of(existente.getFecha(), existente.getHoraInicio());
            LocalDateTime finExistente = inicioExistente.plusMinutes(existente.getDuracionMin());
            boolean seSolapan = inicioExistente.isBefore(finNueva) && inicioNueva.isBefore(finExistente);
            if (seSolapan) {
                throw new RuntimeException("La pista ya tiene una reserva que se solapa en ese horario");
            }
        }

        reservas.add(r);
        marcarCambios();
        return true;
    }

    /**
     * Cancela una reserva existente en memoria.
     *
     * Reglas aplicadas:
     * 1) El id de reserva es obligatorio.
     * 2) La reserva debe existir en memoria.
     * 3) Se elimina de la lista y se marca cambio pendiente.
     *
     * La eliminacion en BD se materializa en el siguiente guardado/cierre.
     *
     * @param idReserva identificador de la reserva.
     * @return true si se elimina correctamente.
     */
    public boolean cancelarReserva(String idReserva) {
        if (idReserva == null || idReserva.isBlank()) {
            throw new RuntimeException("idReserva obligatorio");
        }

        Reserva encontrada = null;
        for (Reserva r : reservas) {
            if (r.getIdReserva().equals(idReserva)) {
                encontrada = r;
                break;
            }
        }
        if (encontrada == null) {
            throw new RuntimeException("Reserva no encontrada: " + idReserva);
        }

        reservas.remove(encontrada);
        marcarCambios();
        return true;
    }


    /**
     * Sincroniza el estado de reservas (y sus bajas asociadas) entre memoria y BD.
     *
     * Flujo que realiza este metodo:
     * 1) Recorre todas las reservas en memoria.
     * 2) Si una reserva ya existe en BD, la actualiza y recalcula el precio con
     *    {@code fn_precio_reserva(...)}.
     * 3) Si no existe en BD, la inserta usando el procedimiento almacenado
     *    {@code sp_crear_reserva(...)} para mantener las validaciones SQL del script.
     * 4) Tras insertar/actualizar, vuelve a leer el precio real en BD y lo copia
     *    al objeto en memoria para mantener ambos estados consistentes.
     * 5) Elimina de BD las reservas que ya no existen en memoria (canceladas/bajas).
     * 6) Elimina de BD los socios que ya no existen en memoria, para persistir la
     *    baja de socio realizada en el servicio.

     */
    private void guardarReservasEnBd() throws SQLException {
        String sql = "{CALL sp_crear_reserva(?, ?, ?, ?, ?, ?)}";
        try (CallableStatement cst = conexion.prepareCall(sql)) {
            for (Reserva r : reservas) {
                boolean existeEnBd = false;
                String sqlExiste = "SELECT id_reserva FROM reservas WHERE id_reserva = ?";
                try (PreparedStatement pst = conexion.prepareStatement(sqlExiste)) {
                    pst.setString(1, r.getIdReserva());
                    try (ResultSet rs = pst.executeQuery()) {
                        existeEnBd = rs.next();
                    }
                }

                if (existeEnBd) {
                    String sqlUpdate = "UPDATE reservas SET id_socio = ?, id_pista = ?, fecha = ?, hora_inicio = ?, duracion_min = ?, "
                            + "precio = fn_precio_reserva(?) WHERE id_reserva = ?";
                    try (PreparedStatement pst = conexion.prepareStatement(sqlUpdate)) {
                        pst.setString(1, r.getIdSocio());
                        pst.setString(2, r.getIdPista());
                        pst.setDate(3, Date.valueOf(r.getFecha()));
                        pst.setTime(4, Time.valueOf(r.getHoraInicio()));
                        pst.setInt(5, r.getDuracionMin());
                        pst.setInt(6, r.getDuracionMin());
                        pst.setString(7, r.getIdReserva());
                        pst.executeUpdate();
                    }
                } else {
                    cst.setString(1, r.getIdReserva());
                    cst.setString(2, r.getIdSocio());
                    cst.setString(3, r.getIdPista());
                    cst.setDate(4, Date.valueOf(r.getFecha()));
                    cst.setTime(5, Time.valueOf(r.getHoraInicio()));
                    cst.setInt(6, r.getDuracionMin());
                    cst.execute();
                }

                String sqlPrecio = "SELECT precio FROM reservas WHERE id_reserva = ?";
                try (PreparedStatement pst = conexion.prepareStatement(sqlPrecio)) {
                    pst.setString(1, r.getIdReserva());
                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            r.setPrecio(rs.getDouble("precio"));
                        }
                    }
                }
            }

            String sqlReservasBd = "SELECT id_reserva FROM reservas";
            try (PreparedStatement pst = conexion.prepareStatement(sqlReservasBd);
                 ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String idReserva = rs.getString("id_reserva");
                    boolean existeEnMemoria = false;
                    for (Reserva r : reservas) {
                        if (r.getIdReserva().equals(idReserva)) {
                            existeEnMemoria = true;
                            break;
                        }
                    }
                    if (!existeEnMemoria) {
                        try (PreparedStatement del = conexion.prepareStatement("DELETE FROM reservas WHERE id_reserva = ?")) {
                            del.setString(1, idReserva);
                            del.executeUpdate();
                        }
                    }
                }
            }

            String sqlSociosBd = "SELECT id_socio FROM socios";
            try (PreparedStatement pst = conexion.prepareStatement(sqlSociosBd);
                 ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String idSocio = rs.getString("id_socio");
                    boolean existeEnMemoria = false;
                    for (Socio s : socios) {
                        if (s.getIdSocio().equals(idSocio)) {
                            existeEnMemoria = true;
                            break;
                        }
                    }
                    if (!existeEnMemoria) {
                        try (PreparedStatement del = conexion.prepareStatement("DELETE FROM socios WHERE id_socio = ?")) {
                            del.setString(1, idSocio);
                            del.executeUpdate();
                        }
                    }
                }
            }
        }
    }

}
