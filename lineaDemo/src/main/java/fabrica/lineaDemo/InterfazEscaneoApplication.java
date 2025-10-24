package fabrica.lineaDemo;

import fabrica.lineaDemo.utils.NetworkUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class InterfazEscaneoApplication {

	public static void main(String[] args) {
		SpringApplication.run(InterfazEscaneoApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void mostrarInfoServidor(){
		String ip = NetworkUtils.obtenerIPLocal();
		String url = "http://"+ ip +":9090";
		System.out.println("====================================================");
		System.out.println("💻 Servidor disponible en la red local:");
		System.out.println("👉  " + url);
		System.out.println("====================================================");
	}

}
