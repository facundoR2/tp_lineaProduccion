package fabrica.lineaDemo.Controllers;

import fabrica.lineaDemo.Services.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/producto")
public class ProductoController {


    private final ProductoService productoService;


    public ProductoController(ProductoService productoService){
        this.productoService = productoService;
    }


    //comprobar dar nombre de producto.
    @PostMapping("/nombre")
    public ResponseEntity<Map<String, String>> buscarNombre(@RequestBody String codigo){
        System.out.println("COdigo recibido:"+codigo);
        String nombre = productoService.buscarNombrePorCodigo(codigo);
        Map<String, String> response = new HashMap<>();
        response.put("nombre", nombre);
        return ResponseEntity.ok(response);
    }



}
