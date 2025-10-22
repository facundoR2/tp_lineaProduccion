package fabrica.lineaDemo.Controllers;

import fabrica.lineaDemo.DTOS.*;
import fabrica.lineaDemo.Models.ValeProduccionDetalle;
import fabrica.lineaDemo.Services.OrdenProduccionService;
import fabrica.lineaDemo.Services.ProduccionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/produccion")
public class ProduccionController {

    private final ProduccionService produccionService;
    private final OrdenProduccionService ordenProduccionService;

    public ProduccionController(ProduccionService produccionService,
                                OrdenProduccionService ordenProduccionService){

        this.produccionService = produccionService;
        this.ordenProduccionService = ordenProduccionService;
    }


    @PostMapping("/cerrar/{idValeProduccion}")
    public ResponseEntity<String> cerrarProduccion(@PathVariable Integer idValeProduccion){
        produccionService.cerrarProducto(idValeProduccion);
        return ResponseEntity.ok("Produccion cerrada correctamente y stock actualizado");
    }

    @PostMapping("/crear-Vale")
    public ResponseEntity<?> iniciarProduccion(@RequestBody UsuarioDTO usuarioDTO){
        //buscamos el codigo del producto a crear de la Orden de Produccion.
        try {
            String producto = ordenProduccionService.BuscarProducto();
            ValeLectura valenuevo = produccionService.iniciarValeLectura(usuarioDTO,producto);
            return ResponseEntity.ok().body(valenuevo);
            
        }catch (IllegalArgumentException a){
            return ResponseEntity.badRequest().body("No se puede generar un vale si no es el primer puesto.");

        }
        
        //generamos un valeLectura para el usuario ( debe ser el primer puesto)
        
        //agregar validacion para solo en caso de ser el primer puesto de la linea se genere el vale lectura.
        

        //retornamos el vale generado.

    }
    @PostMapping("/info-general")
    public ResponseEntity<InfoGeneralDTO>InfoRequest(@RequestBody Integer IdpuestoActual){
        //devolvemos la informacion general.
        return ResponseEntity.ok(ordenProduccionService.darInfoGeneral(IdpuestoActual));
    }

    @PostMapping("/registrar-componente")
    public ResponseEntity<?> registrarEscaneo(
            //cambiar a un CargaRegistroDTO
            @RequestBody CargaRegistroDTO cargaRegistroDTO
            ){
        try{
            produccionService.registrarComponente(cargaRegistroDTO);
            return ResponseEntity.ok().body("se ha registrado correctamente el ValeProduccionDetalle:");
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }
    @PostMapping("/consolidar")
    public ResponseEntity<String> consolidar(@RequestParam String codigoProducto){
        produccionService.consolidar(codigoProducto);
        return ResponseEntity.ok("Vale consolidado para el producto:"+ codigoProducto);
    }

    @PostMapping("/siguientePuesto")
    public ResponseEntity<Object> pasarAlSiguientePuesto(@RequestBody PasarPuestoDTO pasarPuestoDTO){
        try{
            //validar que no sea el ultimo puesto.

            boolean validar = produccionService.pasarSiguientePuesto(pasarPuestoDTO.getCodigoProducto(),pasarPuestoDTO.getPuestoActual());
            if (validar){
                boolean noesUltimo = produccionService.noEsUltimoPuesto(pasarPuestoDTO.getCodigoProducto(),pasarPuestoDTO.getPuestoActual());
                if (!noesUltimo){
                    //si da false, es porque el puestoactual es mayor,osea el ultimo puesto.
                    produccionService.consolidar(pasarPuestoDTO.getCodigoProducto());
                    return ResponseEntity.ok().body("se ha registrado el ultimo componente de la formula en este ultimo puesto. PRODUCTO COMPLETADO.");

                }
                return ResponseEntity.ok("Producto:"+ pasarPuestoDTO.getCodigoProducto() +"paso al puesto"+(pasarPuestoDTO.getPuestoActual()+1));
            }

        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("no se pudo pasar el producto al siguiente puesto");

    }





   @PostMapping("/pendientes")
   public ResponseEntity<?> ConsultarPendientes(@RequestBody ConsultarPendientesDTO consultarPendientesDTO) {
       ValeLectura valePendiente = null;
       try {
           // verifica que exista un Valedetalle del anterior puesto para el producto a realizar.
           boolean ultimoPuestoCompleado = produccionService.verificarUltimoComponentePrevioAgregado(consultarPendientesDTO.getIdPuestoActual());
           if (ultimoPuestoCompleado = false){
               return  ResponseEntity.badRequest().body("no se ha completado el  puesto anterior aun");

           } else if (ultimoPuestoCompleado = true) {
              try {
                  //consultamos el servicio para revisar el trazado actual.
                  valePendiente = produccionService.ConsultarPendientes(consultarPendientesDTO.getCodigoProducto(),consultarPendientesDTO.getIdPuestoActual(),consultarPendientesDTO.getUsuario());
              }catch (NoSuchFieldException a){
                  return ResponseEntity.badRequest().body("no se ha encontrado un valeProduccion,ERROR: "+a.getMessage());
              }
           }
       } catch (IllegalArgumentException b) {
           return ResponseEntity.badRequest().body("error al consultar pendientes:"+b.getMessage());

       }

       return  ResponseEntity.ok().body(valePendiente);

   }

}
