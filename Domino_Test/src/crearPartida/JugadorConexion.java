package crearPartida;

import Observer.Observable;
import Observer.Observer;

import dominio_domino.Jugador;
import dominio_domino.Partida;
import dominio_dominodto.JugadorDTO;
import dominio_dominodto.PartidaDTO;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 *
 * @author 
 */
public class JugadorConexion extends Thread implements Observable{
    public List<Observer> listaObservable;
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream clientOutput;
    private Object objecto;
//    private Jframe frame;

    public JugadorConexion(Socket socket) {
        listaObservable = new ArrayList<>();
        this.clientSocket = socket;
        
//        this.frame = frame;

        try {
            in = new ObjectInputStream(clientSocket.getInputStream());
            clientOutput= new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized void enviarAlServidor(Object mensaje) throws IOException {      
        clientOutput.writeObject(mensaje);
        clientOutput.flush();    
    }

    public void run() {
        try {
            while (true) {
                objecto = in.readObject();

                if (objecto instanceof PartidaDTO) {

                    PartidaDTO partida = (PartidaDTO) objecto;
                    notificarObservers(partida);
//                    Partida p = new Partida(partida.getNumero());
//                    if (partida.getJugador().isEmpty()) {
//                        //aqui se regresa al frame Unirse
//                    } else {
//                        List<Jugador> jugadores = new ArrayList<>();
//                        for (JugadorDTO s : partida.getJugador()) {
//                            Jugador j = new Jugador(s.getNombre(), s.getAvatar());
//                            jugadores.add(j);
//                        }
//                        p.setJugadores(jugadores);
//                         //aqui se regresa al frame Partida
//                    }
                }
                

            }
        } catch (IOException | ClassNotFoundException e) {
            // Manejar excepciones
        }
    }

    public void agregarObserver(Observer o) {
        listaObservable.add(o);
    }

    public void eliminarObserver(Observer o) {
        listaObservable.remove(o);
    }

    public void notificarObservers(Object o) {
        for (Observer observer : listaObservable) {
            observer.update(o);
        }
    }

}
