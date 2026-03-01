package Model;

public class CultivoRecurso {
    private int cultivoRecursoID;
    private int cultivoID;
    private int recursoID;
    private int cantidad;
    private String unidadMedida;
    private float costoTotal;
    private String fechaAplicacion;

    public CultivoRecurso() {
    }

    public CultivoRecurso(int cultivoID, int recursoID, int cantidad, String unidadMedida, float costoTotal, String fechaAplicacion) {
        this.cultivoID = cultivoID;
        this.recursoID = recursoID;
        this.cantidad = cantidad;
        this.unidadMedida = unidadMedida;
        this.costoTotal = costoTotal;
        this.fechaAplicacion = fechaAplicacion;
    }

    public int getCultivoRecursoID() {
        return cultivoRecursoID;
    }

    public int getCultivoID() {
        return cultivoID;
    }

    public void setCultivoID(int cultivoID) {
        this.cultivoID = cultivoID;
    }

    public int getRecursoID() {
        return recursoID;
    }

    public void setRecursoID(int recursoID) {
        this.recursoID = recursoID;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public float getCostoTotal() {
        return costoTotal;
    }

    public void setCostoTotal(float costoTotal) {
        this.costoTotal = costoTotal;
    }

    public String getFechaAplicacion() {
        return fechaAplicacion;
    }

    public void setFechaAplicacion(String fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }
}
