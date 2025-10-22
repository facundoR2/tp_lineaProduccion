package fabrica.lineaDemo.DTOS;

import java.util.List;

public class ProcesoInfoDTO {
    private String codigoProducto;

    private List<ComponenteDTO> componentes;

    private Integer orden;


    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public void setComponentes(List<ComponenteDTO> componentes) {
        this.componentes = componentes;
    }

    public List<ComponenteDTO> getComponentes() {
        return componentes;
    }
}
