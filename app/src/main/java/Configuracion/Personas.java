package Configuracion;

public class Personas {
    private Integer id;
    private String nombres;
    private String apellido;
    private Integer edad;
    private String correo;
    private String foto;

    public Personas(Integer id, String nombres, String apellido, Integer edad, String correo, String foto) {
        this.id = id;
        this.nombres = nombres;
        this.apellido = apellido;
        this.edad = edad;
        this.correo = correo;
        this.foto = foto;
    }

    public Personas() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
