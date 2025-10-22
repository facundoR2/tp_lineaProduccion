package fabrica.lineaDemo.DTOS;

import java.util.List;

public class ProcesoYpasoDTO {
    private Long id;
    private String nombre;

    private List<PasoDTO> pasos;


    public List<PasoDTO> getPasos() {
        return pasos;
    }

    public void setPasos(List<PasoDTO> pasos) {
        this.pasos = pasos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
