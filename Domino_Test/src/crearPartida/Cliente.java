    /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package crearPartida;



import dominio_dominodto.JugadorDTO;

import dominio_dominodto.PartidaDTO;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author diego
 */
public class Cliente {

    private volatile static Cliente instance;
    private JugadorConexion j;

    private Cliente() {
    }

    public static synchronized Cliente getInstance() {

        if (instance == null) {
            Socket socket;
            try {
                socket = new Socket("localhost", 1234);
                if (true) {
                    
                }
                instance = new Cliente(socket);

            } catch (IOException  ex) {
                System.out.println("ssss");
                return null;
            }

        }
        return instance;
    }

    public Cliente(Socket socket) {

        j = new JugadorConexion(socket);
        j.start();
    }

    public boolean enviarAlServidor(Object objecto) {
        if (objecto instanceof PartidaDTO) {
            PartidaDTO p = (PartidaDTO) objecto;
            try {

                j.enviarAlServidor(p);
                return true;
            } catch (IOException ex) {
                return false;
            }
        }
        if (objecto instanceof JugadorDTO) {
            JugadorDTO p = (JugadorDTO) objecto;
            try {

                j.enviarAlServidor(p);

                return true;
            } catch (IOException ex) {
                System.out.println(ex);
                return false;
            }
        }
        return false;
    }

}
