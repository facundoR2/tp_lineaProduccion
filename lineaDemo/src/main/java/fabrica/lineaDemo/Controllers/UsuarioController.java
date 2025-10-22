package fabrica.lineaDemo.Controllers;

import fabrica.lineaDemo.DTOS.UsuarioDTO;
import fabrica.lineaDemo.DTOS.UsuarioLoginDTO;
import fabrica.lineaDemo.Services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {


    private final UsuarioService userService;


    public UsuarioController(
            UsuarioService userService
    ){
        this.userService = userService;


    }


    //ENDPOINT PARA VALIDACION DE USUARIO.

    @PostMapping("/login")
    public ResponseEntity<UsuarioDTO>loginRequest(@RequestBody UsuarioLoginDTO user){

        if(userService.loginRequest(user) == false){
            throw new IllegalArgumentException("No se ha encontrado un usuario con este username o contraseña");
        }else {
            UsuarioDTO operarop = userService.solicitarPuesto(user);

            return ResponseEntity.ok().body(operarop);

        }

    }
    @PostMapping("/login_puesto")
    public ResponseEntity<UsuarioDTO> puestoRequest(UsuarioLoginDTO usuario){
        UsuarioDTO operario = userService.solicitarPuesto(usuario);

        return ResponseEntity.status(HttpStatus.OK).body(operario);
    }


}
