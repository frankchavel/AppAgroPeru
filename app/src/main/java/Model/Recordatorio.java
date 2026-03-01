package Model;

public class Recordatorio {
    private int recordatorioID;
    private int cultivoID;
    private String actividad;
    private String fechaProgramada;
    private boolean notificado;
    private String estado;
    private String fechaCompletado;
    private String fechaRegistro;

    public Recordatorio() {
    }

    public Recordatorio(int cultivoID, String actividad, String fechaProgramada, boolean notificado, String estado, String fechaCompletado, String fechaRegistro) {
        this.cultivoID = cultivoID;
        this.actividad = actividad;
        this.fechaProgramada = fechaProgramada;
        this.notificado = notificado;
        this.estado = estado;
        this.fechaCompletado = fechaCompletado;
        this.fechaRegistro = fechaRegistro;
    }

    public int getRecordatorioID() {
        return recordatorioID;
    }

    public int getCultivoID() {
        return cultivoID;
    }

    public void setCultivoID(int cultivoID) {
        this.cultivoID = cultivoID;
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public String getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(String fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public boolean isNotificado() {
        return notificado;
    }

    public void setNotificado(boolean notificado) {
        this.notificado = notificado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaCompletado() {
        return fechaCompletado;
    }

    public void setFechaCompletado(String fechaCompletado) {
        this.fechaCompletado = fechaCompletado;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
