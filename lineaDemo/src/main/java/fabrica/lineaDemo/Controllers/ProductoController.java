package fabrica.lineaDemo.Controllers;

import fabrica.lineaDemo.Services.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/producto")
public class ProductoController {


    private final ProductoService productoService;


    public ProductoController(ProductoService productoService){
        this.productoService = productoService;
    }


    //comprobar dar nombre de producto.
    @GetMapping("/nombre")
    public ResponseEntity buscarNombre(@RequestParam String codigo){
        System.out.println("COdigo recibido:"+codigo);
        String nombre = productoService.buscarNombrePorCodigo(codigo);
        return ResponseEntity.ok().body(nombre);
    }



}
