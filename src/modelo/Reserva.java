package modelo;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entidad de dominio que representa una reserva de pista.
 */
public class Reserva {
    private final String idReserva;
    private final String idSocio;
    private final String idPista;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private int duracionMin;
    private double precio;

    /**
     * Crea una reserva validando datos obligatorios y coherencia basica.
     *
     * @param idReserva identificador unico de la reserva.
     * @param idSocio identificador del socio que reserva.
     * @param idPista identificador de la pista reservada.
     * @param fecha fecha de la reserva.
     * @param horaInicio hora de inicio.
     * @param duracionMin duracion en minutos.
     * @param precio precio de la reserva.
     * @throws IdObligatorioException si algun campo obligatorio es invalido.
     */
    public Reserva(String idReserva, String idSocio, String idPista, LocalDate fecha, LocalTime horaInicio, int duracionMin, double precio) throws IdObligatorioException {
        if (idReserva == null || idReserva.isBlank()) {
            throw new IdObligatorioException("idReserva obligatorio");
        }
        if (idSocio == null || idSocio.isBlank()) {
            throw new IdObligatorioException("idSocio obligatorio");
        }
        if (idPista == null || idPista.isBlank()) {
            throw new IdObligatorioException("idPista obligatorio");
        }
        if (fecha == null) {
            throw new IdObligatorioException("fecha obligatoria");
        }
        if (horaInicio == null) {
            throw new IdObligatorioException("horaInicio obligatoria");
        }
        if (duracionMin <= 0) {
            throw new IdObligatorioException("duracion debe ser > 0");
        }
        if (precio < 0) {
            throw new IdObligatorioException("precio debe ser >= 0");
        }

        this.idReserva = idReserva;
        this.idSocio = idSocio;
        this.idPista = idPista;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.duracionMin = duracionMin;
        this.precio = precio;
    }

    /**
     * Devuelve el identificador de la reserva.
     *
     * @return id de la reserva.
     */
    public String getIdReserva() {
        return idReserva;
    }

    /**
     * Devuelve el identificador del socio.
     *
     * @return id del socio.
     */
    public String getIdSocio() {
        return idSocio;
    }

    /**
     * Devuelve el identificador de la pista.
     *
     * @return id de la pista.
     */
    public String getIdPista() {
        return idPista;
    }

    /**
     * Devuelve la fecha de la reserva.
     *
     * @return fecha de la reserva.
     */
    public LocalDate getFecha() {
        return fecha;
    }



    /**
     * Devuelve la hora de inicio.
     *
     * @return hora de inicio.
     */
    public LocalTime getHoraInicio() {
        return horaInicio;
    }



    /**
     * Devuelve la duracion en minutos.
     *
     * @return duracion en minutos.
     */
    public int getDuracionMin() {
        return duracionMin;
    }



    /**
     * Devuelve el precio de la reserva.
     *
     * @return precio final.
     */
    public double getPrecio() {
        return precio;
    }

    /**
     * Actualiza el precio de la reserva.
     *
     * @param precio nuevo precio.
     */
    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
