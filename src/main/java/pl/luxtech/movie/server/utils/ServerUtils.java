package pl.luxtech.movie.server.utils;

import java.io.IOException;
import java.net.Socket;

public class ServerUtils {

    public static boolean checkAvailablePort(int port) {
        Socket s = null;
        try {
            s = new Socket("localhost", port);
            return false;
        
        } catch (IOException e) {
            return true;
       
        } finally {
            if( s != null){
                try {
                    s.close();
                } catch (IOException e) {
                    throw new RuntimeException("You should handle this error." , e);
                }
            }
        }
    }
}
