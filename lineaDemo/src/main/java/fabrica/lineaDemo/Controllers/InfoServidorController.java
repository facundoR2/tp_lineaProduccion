package fabrica.lineaDemo.Controllers;

import fabrica.lineaDemo.utils.NetworkUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class InfoServidorController {

    @GetMapping("/api/ip")
    public Map<String,String> obtenerIPServidor(){
        Map<String,String> info = new HashMap<>();
        String ip = NetworkUtils.obtenerIPLocal();
        info.put("ip",ip);
        info.put("url","http://" + ip + ":9090");
        return info;
    }
}
