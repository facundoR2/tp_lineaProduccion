package fabrica.lineaDemo.Services;

import fabrica.lineaDemo.DTOS.InfoGeneralDTO;
import fabrica.lineaDemo.DTOS.ValeLectura;
import fabrica.lineaDemo.Models.*;
import fabrica.lineaDemo.Repositorys.FormulaDetalleRepository;
import fabrica.lineaDemo.Repositorys.FormulaPuestosRepository;
import fabrica.lineaDemo.Repositorys.OrdenProduccionRepository;
import fabrica.lineaDemo.Repositorys.ValeProduccionDetalleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrdenProduccionService {
    private final OrdenProduccionRepository ordenProdRepo;
    private final ValeProduccionDetalleRepository valeDetalleRepo;

    private final FormulaDetalleRepository fdRepository;

    private final FormulaPuestosRepository formulaPuestosRepository;



    public OrdenProduccionService(FormulaPuestosRepository formulaPuestosRepository,FormulaDetalleRepository fdRepository,OrdenProduccionRepository ordenProdRepo, ValeProduccionDetalleRepository valeDetalleRepo){
        this.ordenProdRepo = ordenProdRepo;
        this.valeDetalleRepo = valeDetalleRepo;
        this.fdRepository = fdRepository;
        this.formulaPuestosRepository = formulaPuestosRepository;

    }


    public String BuscarProducto (){
        OrdenProduccion orden =  ordenProdRepo.getReferenceById(1); // por ahora solo va a haber una orden.
        //la idea es que busque por dia (hoy).
        String producto = orden.getProducto().getCodigo();
        return producto;


    }

    public InfoGeneralDTO darInfoGeneral(Integer IdpuestoActual){
        InfoGeneralDTO dto = new InfoGeneralDTO();
        OrdenProduccion orden = new OrdenProduccion();
        //conseguimos el nro de OrdenProduccion (en este caso sera el 1 nomas)
        Optional<OrdenProduccion> primera = ordenProdRepo.findFirstByOrderByIdAsc();
        if (primera.isPresent()){
             orden = primera.get();
        }

        //conseguimos el codigo de formula.
        String codigoFormula = orden.getFormula().getCodigoFormula();
        //el codigo del producto terminado.
        String codigoProducto = orden.getProducto().getCodigo();

        String codigoOrdenProduccion = String.format("%d-%s-%s",orden.getId(),codigoFormula,codigoProducto);

        //cargamos el serial (seria por ej: 1-NBK001-100_1ntk_1)
        dto.setOrdenProduccion(codigoOrdenProduccion);
        dto.setVersionFormula(orden.getFormula().getVersion());
        dto.setCodigoProducto(codigoProducto);


        //buscamos la cantidad maxima de componentes  segun la formula.

        //buscamos todos los componentes de la formula.
        List<FormulaDetalle> componentes = fdRepository.findByFormula_codigoFormula(codigoFormula);

        Integer cantMaximaComponentes = 0;
        for (FormulaDetalle fd :componentes) {
            if (fd.getCantidad()>1){
                cantMaximaComponentes += fd.getCantidad();
            }else {
                cantMaximaComponentes = cantMaximaComponentes + 1;
            }

        }
        dto.setCantMaxComponentes(cantMaximaComponentes);

        //buscamos la cantidad maxima de componentes para el puesto  segun la formula.

        List<FormulaPuesto> lista = formulaPuestosRepository.findByPuesto_IdP(IdpuestoActual);
        Integer cantMaxDelPuesto = 0;
        Integer numeroOrden= 0;
        for (FormulaPuesto fp : lista){
            //revisar para que no cuente el mismo componente.

            if(fp.getOrden().equals( numeroOrden)){

            }else {
                Integer cantCOmp = fp.getFormulaDetalle().getCantidad();
                cantMaxDelPuesto = cantMaxDelPuesto + cantCOmp;
                cantCOmp = 0;
                numeroOrden = fp.getOrden();
            }
            System.out.println("numero de componentes para el puesto:"+cantMaxDelPuesto);
        }
        dto.setCantMaxcompPuesto(cantMaxDelPuesto);

        return dto;
    }



}
