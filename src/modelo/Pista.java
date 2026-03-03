package modelo;

/**
 * Entidad de dominio que representa una pista deportiva del club.
 */
public class Pista {
    private final String idPista;
    private String deporte;
    private String descripcion;
    private boolean disponible;

    /**
     * Crea una pista validando los datos obligatorios.
     *
     * @param idPista identificador unico de la pista.
     * @param deporte deporte asociado a la pista.
     * @param descripcion descripcion de la pista.
     * @param disponible estado operativo de la pista.
     * @throws IdObligatorioException si algun dato obligatorio es invalido.
     */
    public Pista(String idPista, String deporte, String descripcion, boolean disponible) throws IdObligatorioException {
        if (idPista == null || idPista.isBlank()) {
            throw new IdObligatorioException("El id de la pista no puede ser vacio");
        }
        if (deporte == null || deporte.isBlank()) {
            throw new IdObligatorioException("El deporte no puede ser vacio");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IdObligatorioException("La descripcion no puede ser vacia");
        }

        this.idPista = idPista;
        this.deporte = deporte;
        this.descripcion = descripcion;
        this.disponible = disponible;
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
     * Devuelve el deporte de la pista.
     *
     * @return deporte asociado.
     */
    public String getDeporte() {
        return deporte;
    }

    /**
     * Actualiza el deporte de la pista.
     *
     * @param deporte nuevo deporte.
     * @throws IdObligatorioException si el deporte es invalido.
     */
    public void setDeporte(String deporte) {
        if (deporte == null || deporte.isBlank()) {
            throw new IdObligatorioException("El deporte no puede ser vacio");
        }
        this.deporte = deporte;
    }

    /**
     * Devuelve la descripcion de la pista.
     *
     * @return descripcion actual.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Actualiza la descripcion de la pista.
     *
     * @param descripcion nueva descripcion.
     * @throws IdObligatorioException si la descripcion es invalida.
     */


    /**
     * Indica si la pista esta disponible para reservas.
     *
     * @return true si esta disponible.
     */
    public boolean isDisponible() {
        return disponible;
    }

    /**
     * Actualiza la disponibilidad de la pista.
     *
     * @param disponible nuevo estado de disponibilidad.
     */
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}
