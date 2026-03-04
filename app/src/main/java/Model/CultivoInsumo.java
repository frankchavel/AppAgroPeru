package Model;

public class CultivoInsumo {
    private int cultivoInsumoID;
    private int cultivoID;
    private int insumoID;

    private double cantidad;
    private double costoTotal;
    private String fechaAplicacion;

    // Getters y Setters

    public int getCultivoInsumoID() {
        return cultivoInsumoID;
    }

    public void setCultivoInsumoID(int cultivoInsumoID) {
        this.cultivoInsumoID = cultivoInsumoID;
    }

    public int getCultivoID() {
        return cultivoID;
    }

    public void setCultivoID(int cultivoID) {
        this.cultivoID = cultivoID;
    }

    public int getInsumoID() {
        return insumoID;
    }

    public void setInsumoID(int insumoID) {
        this.insumoID = insumoID;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public String getFechaAplicacion() {
        return fechaAplicacion;
    }

    public void setFechaAplicacion(String fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }
    public void setCostoTotal(double costoTotal) { this.costoTotal = costoTotal; }
    public double getCostoTotal() { return costoTotal; }
}
