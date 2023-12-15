/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dominopieceserver;

import dominio_dominodto.Acciones;
import dominio_dominodto.JugadorDTO;
import dominio_dominodto.TerminarDTO;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author eduar
 */
public class Votacion extends Thread{
    private List<Boolean> respuestasVotacion;
    private int cantidadNecesaria;
    private Server server;
    private volatile static Votacion instance;
    private boolean terminarPartida;

    public Votacion() {
    }

    public Votacion(int cantidadNecesaria, Server server, boolean terminarPartida) {
        respuestasVotacion = new ArrayList<>();
        this.cantidadNecesaria = cantidadNecesaria;
        this.server = server;
        this.terminarPartida = terminarPartida;
        System.out.println(terminarPartida + "la votacion");

    }

    public static void setInstance(Votacion instance) {
        Votacion.instance = instance;
    }

    public synchronized void respuestaVotacion(boolean respuesta) {
        respuestasVotacion.add(respuesta);
        this.notifyAll();

    }

    public static synchronized Votacion getInstance() {

        if (instance == null) {

            instance = new Votacion();
        }
        return instance;
    }

    public void run() {

        boolean verificar = true;
        respuestasVotacion = new ArrayList<>();
        while (verificar) {
            int votaciones = 0;
            try {
                System.out.printf("");
                for (int i = 0; i < respuestasVotacion.size(); i++) {
                    votaciones++;

                    if (respuestasVotacion.get(i) == false) {
                        verificar = false;
                        server.sendToAll(Acciones.NO_INICIAR);
                        if (terminarPartida) {
                            server.sendToAll(Acciones.NO_TERMINAR);
                        }

                    } else if (votaciones == cantidadNecesaria) {
                        if (!terminarPartida) {
                            verificar = false;
                            Sink.getInstance().iniciarPartida();
                            server.enviarJugadores();
                            server.enviarPartida();
                            server.sendToAll(Acciones.INICIAR_PARTIDA);
                        } else {
                            verificar = false;
                            server.enviarJugadores();
                            server.enviarPartida();

                            List<JugadorDTO> listaPuntaciones = Sink.getInstance().getPuntuaciones();

                            TerminarDTO terminar = new TerminarDTO(listaPuntaciones, Acciones.TERMINAR_PARTIDA_VOTACION);
                            server.sendToAll(terminar);
                            Sink.getInstance().setPartida(null);
                            server.borrarJugadores();
                        }

                    }

                }
            } catch (Exception e) {

            }

        }
        System.out.println("Fin de la votacion");

    }
}
