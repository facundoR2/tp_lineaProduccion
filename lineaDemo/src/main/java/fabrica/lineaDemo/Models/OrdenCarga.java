package fabrica.lineaDemo.Models;

public class OrdenCarga {

    private OrdenProduccionDetalles ordenProduccionDetalles;
    private Producto componente;

    private Integer cantNecesaria;
    private Integer cantStock;


    public OrdenCarga(Producto componente, Integer cantNecesaria){
        this.componente = componente;
        this.cantNecesaria = cantNecesaria;
        this.cantStock = componente.getCantidad();
    }

    //validar stock suficiente
    public boolean validarStock(){
        return cantStock != null && cantStock >= cantNecesaria;
    }

    //validar adicional: validar disponibilidad componentes.
    public boolean validarDisponibilidad(){
        return componente != null && validarStock();
    }


    public Integer getCantStock() {
        return cantStock;
    }

    public void setCantStock(Integer cantStock) {
        this.cantStock = cantStock;
    }

    public void setCantNecesaria(Integer cantNecesaria) {
        this.cantNecesaria = cantNecesaria;
    }

    public Integer getCantNecesaria() {
        return cantNecesaria;
    }

    public void setComponente(Producto componente) {
        this.componente = componente;
    }

    public Producto getComponente() {
        return componente;
    }

    public void setOrdenProduccionDetalles(OrdenProduccionDetalles ordenProduccionDetalles) {
        this.ordenProduccionDetalles = ordenProduccionDetalles;
    }

    public OrdenProduccionDetalles getOrdenProduccionDetalles() {
        return ordenProduccionDetalles;
    }
}
