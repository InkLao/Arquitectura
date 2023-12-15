package crearPartida;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eduar
 */
public class SocketCliente {
    public static void main(String[] args) {
        Socket kkSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            kkSocket = new Socket("localhost", 9175);
            out = new PrintWriter(kkSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;
            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                if (fromServer.equals("Bye."))
                    break;
                
                fromUser = stdIn.readLine();
                if (fromUser != null) {
                    System.out.println("Client: " + fromUser);
                    out.println(fromUser);
                }
            }
            out.close();
            in.close();
            stdIn.close();
            kkSocket.close();            
        } catch (IOException ex) {
            Logger.getLogger(SocketCliente.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
