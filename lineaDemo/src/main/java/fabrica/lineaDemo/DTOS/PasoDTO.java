package fabrica.lineaDemo.DTOS;

public class PasoDTO {

    private Integer IdPaso;

    private String nombre;

    private String descripcion;

    private Integer ordenPaso;


    public Integer getOrdenPaso() {
        return ordenPaso;
    }

    public void setOrdenPaso(Integer ordenPaso) {
        this.ordenPaso = ordenPaso;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }




    public void setIdPaso(Integer idPaso) {
        IdPaso = idPaso;
    }

    public Integer getIdPaso() {
        return IdPaso;
    }
}
