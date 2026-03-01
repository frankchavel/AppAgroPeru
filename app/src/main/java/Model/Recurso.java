package Model;

public class Recurso {
    private int recursoID;
    private String nombreRecurso;
    private String tipoRecurso;
    private String unidadMedida;
    private float precioUnitario;
    private String fechaRegistro;

    public Recurso() {
    }

    public Recurso(String nombreRecurso, String tipoRecurso, String unidadMedida, float precioUnitario, String fechaRegistro) {
        this.nombreRecurso = nombreRecurso;
        this.tipoRecurso = tipoRecurso;
        this.unidadMedida = unidadMedida;
        this.precioUnitario = precioUnitario;
        this.fechaRegistro = fechaRegistro;
    }

    public int getRecursoID() {
        return recursoID;
    }

    public String getNombreRecurso() {
        return nombreRecurso;
    }

    public String getTipoRecurso() {
        return tipoRecurso;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public float getPrecioUnitario() {
        return precioUnitario;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }
}
