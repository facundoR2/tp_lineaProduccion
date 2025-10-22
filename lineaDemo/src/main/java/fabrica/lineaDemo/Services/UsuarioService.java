package fabrica.lineaDemo.Services;

import fabrica.lineaDemo.DTOS.UsuarioDTO;
import fabrica.lineaDemo.DTOS.UsuarioLoginDTO;
import fabrica.lineaDemo.Models.Puesto;
import fabrica.lineaDemo.Models.Usuario;
import fabrica.lineaDemo.Repositorys.PuestoRepository;
import fabrica.lineaDemo.Repositorys.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Service

public class UsuarioService {

    private final UsuarioRepository userRepo;

    private final PuestoRepository puestoRepository;



    public UsuarioService( PuestoRepository puestoRepository,
                           UsuarioRepository userRepo){
        this.userRepo = userRepo;
        this.puestoRepository = puestoRepository;
    }





    public Boolean loginRequest(UsuarioLoginDTO userR){
        //validamos que el usuario exista en la BD
        Usuario user = userRepo.findByEmailAndPassword(userR.getUsername(),userR.getPassword());
        if(user.getEmail().isEmpty() && user.getPassword().isEmpty()){
            return false;
        }
        return true;


    }
    public UsuarioDTO solicitarPuesto(UsuarioLoginDTO user){
        Usuario validar = userRepo.findByEmailAndPassword(user.getUsername(),user.getPassword());
        UsuarioDTO operario = new UsuarioDTO();
        //verificamos que el puesto existe.
        //si existe, se trae el la ID del puesto con el nombre puesto.
        operario.setId(puestoRepository.findByCodigo(user.getPuesto()).getIdP());
        operario.setNombre(validar.getNombre());
        operario.setUsername(validar.getEmail());


        return operario;


    }









}
