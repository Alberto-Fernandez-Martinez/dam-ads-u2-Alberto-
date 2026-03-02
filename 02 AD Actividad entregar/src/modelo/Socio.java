package modelo;

/**
 * Entidad de dominio que representa un socio del club.
 */
public class Socio {
    private final String idSocio;
    private String dni;
    private String nombre;
    private String apellidos;
    private String telefono;
    private String email;

    /**
     * Crea un socio validando los campos obligatorios.
     *
     * @param idSocio identificador unico del socio.
     * @param dni documento de identidad del socio.
     * @param nombre nombre del socio.
     * @param apellidos apellidos del socio.
     * @param telefono telefono del socio (solo digitos).
     * @param email correo electronico del socio.
     * @throws IdObligatorioException si algun dato obligatorio es invalido.
     */
    public Socio(String idSocio, String dni, String nombre, String apellidos, String telefono, String email) throws IdObligatorioException {
        if (idSocio == null || idSocio.isBlank()) {
            throw new IdObligatorioException("El id del socio no puede ser vacio");
        }
        if (dni == null || dni.isBlank()) {
            throw new IdObligatorioException("El DNI no puede ser vacio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IdObligatorioException("El nombre no puede ser vacio");
        }
        if (apellidos == null || apellidos.isBlank()) {
            throw new IdObligatorioException("Los apellidos no pueden ser vacios");
        }
        if (telefono == null || telefono.isBlank()) {
            throw new IdObligatorioException("El telefono no puede ser vacio");
        }
        if (!telefono.matches("\\d+")) {
            throw new IdObligatorioException("El telefono solo puede contener numeros");
        }
        if (email == null || email.isBlank()) {
            throw new IdObligatorioException("El email no puede ser vacio");
        }

        this.idSocio = idSocio;
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.email = email;
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
     * Devuelve el DNI del socio.
     *
     * @return DNI del socio.
     */
    public String getDni() {
        return dni;
    }



    /**
     * Devuelve el nombre del socio.
     *
     * @return nombre del socio.
     */
    public String getNombre() {
        return nombre;
    }



    /**
     * Devuelve los apellidos del socio.
     *
     * @return apellidos del socio.
     */
    public String getApellidos() {
        return apellidos;
    }



    /**
     * Devuelve el telefono del socio.
     *
     * @return telefono del socio.
     */
    public String getTelefono() {
        return telefono;
    }



    /**
     * Devuelve el email del socio.
     *
     * @return email del socio.
     */
    public String getEmail() {
        return email;
    }


}
