package Modelos;

public class Diagnostico {

    private int idMoto, idTecnico, idDiagnostico;
    private String resultadoDiagnostico, placaPorIdMoto, nombreTecnicoPorIdTecnico, dueñoṔorIdMoto;
    private String fecha;

    public String getNombreTecnicoPorIdTecnico() {
        return nombreTecnicoPorIdTecnico;
    }

    public void setNombreTecnicoPorIdTecnico(String nombreTecnicoPorIdTecnico) {
        this.nombreTecnicoPorIdTecnico = nombreTecnicoPorIdTecnico;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    
    public String getPlacaPorIdMoto() {
        return placaPorIdMoto;
    }

    public void setPlacaPorIdMoto(String placaPorIdMoto) {
        this.placaPorIdMoto = placaPorIdMoto;
    }

    public String getDueñoṔorIdMoto() {
        return dueñoṔorIdMoto;
    }

    public void setDueñoṔorIdMoto(String dueñoṔorIdMoto) {
        this.dueñoṔorIdMoto = dueñoṔorIdMoto;
    }

    
    
    public int getIdDiagnostico() {
        return idDiagnostico;
    }

    public void setIdDiagnostico(int idDiagnostico) {
        this.idDiagnostico = idDiagnostico;
    }

    public int getIdMoto() {
        return idMoto;
    }

    public void setIdMoto(int idMoto) {
        this.idMoto = idMoto;
    }

    public int getIdTecnico() {
        return idTecnico;
    }

    public void setIdTecnico(int idTecnico) {
        this.idTecnico = idTecnico;
    }

    public String getResultadoDiagnostico() {
        return resultadoDiagnostico;
    }

    public void setResultadoDiagnostico(String resultadoDiagnostico) {
        this.resultadoDiagnostico = resultadoDiagnostico;
    }

}
