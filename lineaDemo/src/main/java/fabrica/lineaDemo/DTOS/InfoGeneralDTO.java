package fabrica.lineaDemo.DTOS;

public class InfoGeneralDTO {

    private String ordenProduccion;
    private String versionFormula;
    private String codigoProducto; //el producto a fabricar( no el padre).

    private Integer cantMaxComponentes;
    private Integer cantMaxcompPuesto;

    public InfoGeneralDTO(){}
    public InfoGeneralDTO (Integer cantMaxcompPuesto,Integer cantMaxComponentes,String ordenProduccion,String versionFormula,String codigoProducto){
        this.codigoProducto = codigoProducto;
        this.versionFormula = versionFormula;
        this.ordenProduccion = ordenProduccion;
        this.cantMaxComponentes = cantMaxComponentes;
        this.cantMaxcompPuesto = cantMaxcompPuesto;
    }


    public void setOrdenProduccion(String ordenProduccion) {
        this.ordenProduccion = ordenProduccion;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public String getOrdenProduccion() {
        return ordenProduccion;
    }

    public String getVersionFormula() {
        return versionFormula;
    }

    public void setVersionFormula(String versionFormula) {
        this.versionFormula = versionFormula;
    }

    public Integer getCantMaxComponentes() {
        return cantMaxComponentes;
    }

    public void setCantMaxComponentes(Integer cantMaxComponentes) {
        this.cantMaxComponentes = cantMaxComponentes;
    }

    public Integer getCantMaxcompPuesto() {
        return cantMaxcompPuesto;
    }

    public void setCantMaxcompPuesto(Integer cantMaxcompPuesto) {
        this.cantMaxcompPuesto = cantMaxcompPuesto;
    }
}
