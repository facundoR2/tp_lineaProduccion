package fabrica.lineaDemo.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
public class NetworkUtils {

    public static String obtenerIPLocal() {
        try{
            Enumeration<NetworkInterface> interafaces = NetworkInterface.getNetworkInterfaces();
            while (interafaces.hasMoreElements()){
                NetworkInterface ni = interafaces.nextElement();
                if(!ni.isUp() || ni.isLoopback() || ni.getDisplayName().toLowerCase().contains("virtual"))
                    continue;
                Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
                while (inetAddresses.hasMoreElements()){
                    InetAddress ip = inetAddresses.nextElement();
                    if (!ip.isLoopbackAddress() && ip.isSiteLocalAddress()){
                        return ip.getHostAddress();
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "No se pudo determinar la IP local";
    }
}
