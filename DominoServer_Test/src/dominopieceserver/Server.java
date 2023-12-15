package dominopieceserver;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author eduar
 */
public class Server {
   private List<ObjectOutputStream> clientOutputStreams = new ArrayList<>();
    private List<JugadorThread> clienteJugadores = new ArrayList<>();
    private static int connectionCount = 0;

    public void addClientOutputStream(ObjectOutputStream outputStream) {
        clientOutputStreams.add(outputStream);

    }

    public void addClienteJugadores(JugadorThread jugadorHilo) {
        clienteJugadores.add(jugadorHilo);
    }

    public void enviarJugadores() {
        for (JugadorThread jugadorThread : clienteJugadores) {
            jugadorThread.enviarJugador();
        }
    }

    public void enviarPartida() {
        for (JugadorThread jugadorThread : clienteJugadores) {
            jugadorThread.enviarPartidaActual();
        }
    }

    public void sendToAll(Object obj) {
        for (ObjectOutputStream out : clientOutputStreams) {
            try {
                out.writeObject(obj);
                out.flush();
            } catch (IOException e) {
                // Manejar excepciones
            }
        }
    }

    public void borrarJugadores() {
        for (JugadorThread jugadorThread : clienteJugadores) {
            jugadorThread.setJugador(null);
        }
    }

    public void sendToOne(Object obj, ObjectOutputStream outPut) {
        try {
            outPut.writeObject(obj);
            outPut.flush();
        } catch (IOException e) {
            // Manejar excepciones
        }
    }

    public synchronized void desconectarClliente(ObjectOutputStream out) {
        connectionCount--; // Decrementa el contador de conexiones
        clientOutputStreams.remove(out); // Elimina el flujo de salida del cliente
    }

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(1234);

        System.out.println("Servidor inicializado en el puerto 1234");

        Server server = new Server();

        while (true) {

            Socket s = ss.accept();

            System.out.println("Nueva conexión por parte de cliente: " + s);

            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());

            server.addClientOutputStream(out);

            JugadorThread client = new JugadorThread(s, out, server);
            server.addClienteJugadores(client);
            client.start();
            connectionCount++;
        }

    }
}
