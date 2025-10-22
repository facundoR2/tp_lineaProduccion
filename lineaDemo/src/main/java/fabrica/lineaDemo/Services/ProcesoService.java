package fabrica.lineaDemo.Services;

import fabrica.lineaDemo.DTOS.*;
import fabrica.lineaDemo.Models.*;
import fabrica.lineaDemo.Repositorys.FormulaPuestosRepository;
import fabrica.lineaDemo.Repositorys.FormulaRepository;
import fabrica.lineaDemo.Repositorys.ProcesoRepository;
import fabrica.lineaDemo.Repositorys.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProcesoService {

    private final ProductoRepository productoRepository;

    private final FormulaPuestosRepository fprepository;

    private final ProcesoRepository rep;

    private final FormulaRepository Frep;

    public ProcesoService(FormulaPuestosRepository fprepository, ProductoRepository productoRepository,ProcesoRepository rep, FormulaRepository Frep){
        this.rep = rep;
        this.Frep = Frep;
        this.productoRepository = productoRepository;
        this.fprepository = fprepository;
    }

    public List<Proceso> listar(){
        return rep.findAll();
    }

    public Proceso guardar(Proceso p){
        return rep.save(p);
    }

    public Formula buscarFormulaAsociada(String codFormula){
        return Frep.findByCodigoFormula(codFormula);
    }

    public ProcesoInfoDTO buildProcesoInfoDTO(InfoParaProcesoDTO payloadDTO) {
        //generamos una lista de todos los formulapuesto que tenga el id del puesto que consulta.
        List<FormulaPuesto> formulaPuestos = fprepository.findByPuesto_IdP(payloadDTO.getIdpuestoActual());
        ProcesoInfoDTO infoDTO = new ProcesoInfoDTO();
        // teniendo la lista formulaPuesto. agrupamos cada componente por elemento de la lista fp
        List<ComponenteDTO> componentes = new ArrayList<>();
        for(FormulaPuesto fp : formulaPuestos){
            ComponenteDTO dto = new ComponenteDTO();
            dto.setCodigoProducto(fp.getFormulaDetalle().getComponente().getCodigo());
            dto.setNombre(fp.getFormulaDetalle().getComponente().getNombre());
            dto.setCantidad(fp.getFormulaDetalle().getCantidad());
            componentes.add(dto);

        }
        infoDTO.setCodigoProducto(payloadDTO.getCodigoProducto());
        infoDTO.setComponentes(componentes);
        return infoDTO;
    }
}

