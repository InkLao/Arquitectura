package dominopieceserver;

import Evento.BuscarPartidaPF;
import Evento.CrearPartidaPF;
import Evento.IniciarVotacionPF;
import Evento.JugadorPF;
import Evento.MovimientoPF;
import Evento.PasarTurnoPF;
import Evento.RespuestaVotacionPF;
import Evento.RobarPozoPF;
import Evento.SalirPartidaPF;
import Evento.TerminarVotacionPF;
import Evento.VerificarAvatarPF;
import dominio_dominodto.Acciones;
import dominio_dominodto.FichaTableroDTO;
import dominio_dominodto.JugadorDTO;
import dominio_dominodto.MovimientoDTO;
import dominio_dominodto.PartidaDTO;
import dominio_dominodto.RespuestaDTO;
import dominio_dominodto.RobarFichaDTO;
import dominio_dominodto.TerminarDTO;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author eduar
 */
public class JugadorThread extends Thread{
    
    private Socket clientSocket;
    private ObjectOutputStream out;
    private Server server;
    private Object objecto;
    private Sink sink;
    private JugadorDTO jugador;
    private Votacion vota;

    public JugadorThread(Socket socket, ObjectOutputStream out, Server server) {
        this.clientSocket = socket;
        this.out = out;
        this.server = server;
        this.sink = Sink.getInstance();
    }

    public void enviarPartidaActual() {
        if (jugador != null) {
            PartidaDTO partidaActual = sink.getPartidaDTO();
            server.sendToAll(partidaActual);
        }

    }

    public void enviarJugador() {
        if (jugador != null) {
            jugador = sink.getJugadorDTO(jugador.getId());
            server.sendToOne(jugador, out);
        }

    }

    public void enviarTodos(Object o) {
        server.sendToAll(o);
    }

    public void enviarAUno(Object o) {
        server.sendToOne(o, out);
    }

    public synchronized void enviarRespuesta(boolean respuesta) {
        Votacion.getInstance().respuestaVotacion(respuesta);
        this.notifyAll();
    }

    public JugadorDTO getJugador() {
        return jugador;
    }

    public void setJugador(JugadorDTO jugador) {
        this.jugador = jugador;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            while (true) {

                objecto = in.readObject();//
                //Acciones de Crear Partida
                if (objecto instanceof CrearPartidaPF) {
                    CrearPartidaPF pf = (CrearPartidaPF) objecto;
                    PartidaDTO p = (PartidaDTO) pf.getData();
                    sink.CrearPartida(p);
                }
                //Acciones de Crear Jugador
                if (objecto instanceof JugadorPF) {
                    JugadorPF pf = (JugadorPF) objecto;
                    JugadorDTO j = (JugadorDTO) pf.getData();
                    jugador = j;
                    sink.agregarJugador(j);
                    enviarJugador();
                    enviarPartidaActual();

                    if (sink.getPartidaDTO().getJugadores().size() >= 4) {
                        System.out.println("Son: " + sink.getPartidaDTO().getJugadores().size());
                        for (JugadorDTO jugadore : sink.getPartidaDTO().getJugadores()) {
                            System.out.println(jugadore.getId());
                        }
                        sink.iniciarPartida();
                        server.enviarJugadores();
                        server.enviarPartida();
                        enviarTodos(Acciones.INICIAR_PARTIDA);

                    }
                }
                //Acciones de Validar avatar
                if (objecto instanceof VerificarAvatarPF) {
                    VerificarAvatarPF pf = (VerificarAvatarPF) objecto;
                    JugadorDTO jA = (JugadorDTO) pf.getData();
                    if (!sink.verificarPartida(jA.getAvatar())) {
                        enviarAUno(Acciones.AVATAR_SIESTA);
                    } else {
                        enviarAUno(Acciones.AVATAR_NOESTA);
                    }
                }

                //Acciones de RespuestaVotacion
                if (objecto instanceof RespuestaVotacionPF) {
                    RespuestaVotacionPF pf = (RespuestaVotacionPF) objecto;
                    RespuestaDTO r = (RespuestaDTO) pf.getData();
                    Votacion.getInstance().respuestaVotacion(r.isRespuestas());

                }
                //Acciones  de IniciarVotacion
                if (objecto instanceof IniciarVotacionPF) {
                    IniciarVotacionPF pf = (IniciarVotacionPF) objecto;
                    Acciones a = (Acciones) pf.getData();
                    if (a == Acciones.INICIAR_VOTACION) {
                        vota = new Votacion(sink.getPartidaDTO().getJugadores().size(), server, false);
                        Votacion.setInstance(vota);
                        Votacion.getInstance().start();

                        enviarTodos(Acciones.INICIAR_VOTACION);

                    }
                }
                if (objecto instanceof MovimientoPF) {
                    MovimientoPF pf = (MovimientoPF) objecto;
                    MovimientoDTO m = (MovimientoDTO) pf.getData();
                    FichaTableroDTO fichaMovimiento = sink.validarMovimiento(m.getFichaTablero(), m.getZona());
                    if (fichaMovimiento != null) {
                        m.setFichaTablero(fichaMovimiento);
                        m.setValido(true);
                        enviarTodos(m);
                        sink.pasarTurno();
                        enviarPartidaActual();
                        enviarJugador();
                        if (jugador.getFichasJugador().isEmpty()) {
                            System.out.println("Terminada por que gano alguien");
                            TerminarDTO terminar = new TerminarDTO(sink.getPuntuaciones(), Acciones.TERMINAR_PARTIDA_VOTACION);
                            server.sendToAll(terminar);
                        }
                    } else {
                        m.setValido(false);
                        enviarAUno(m);
                    }

                }
                if (objecto instanceof RobarPozoPF) {
                    RobarPozoPF roboPf = (RobarPozoPF) objecto;
                    RobarFichaDTO f = (RobarFichaDTO) roboPf.getData();
                    JugadorDTO juga = f.getJugador();
                    sink.robarFicha(juga);
                    enviarPartidaActual();
                    enviarJugador();
                }
                if (objecto instanceof PasarTurnoPF) {

                    PasarTurnoPF p = (PasarTurnoPF) objecto;
                    sink.pasarTurno(p.getJugadorDTO());
                    enviarPartidaActual();
                    enviarJugador();
                    if (sink.comprobarPartidaCerrada()) {
                        TerminarDTO terminar = new TerminarDTO(sink.getPuntuaciones(), Acciones.TERMINAR_PARTIDA_VOTACION);
                        server.sendToAll(terminar);
                        sink.setPartida(null);
                        jugador = null;
                    }
                }
                if (objecto instanceof BuscarPartidaPF) {

                    BuscarPartidaPF p = (BuscarPartidaPF) objecto;
                    if (sink.getPartida() == null) {

                        enviarAUno(Acciones.NO_HAY_PARTIDA);
                    } else {
                        if (sink.getPartida().getTablero() != null) {
                            enviarAUno(Acciones.NO_HAY_PARTIDA);
                        } else {
                            enviarAUno(Acciones.SI_HAY_PARTIDA);
                        }

                    }

                }
                if (objecto instanceof TerminarVotacionPF) {
                    TerminarVotacionPF pf = (TerminarVotacionPF) objecto;
                    Acciones a = (Acciones) pf.getData();
                    if (a == Acciones.INICIAR_VOTACION_TERMINAR) {

                        vota = new Votacion(sink.getPartidaDTO().getJugadores().size(), server, true);
                        Votacion.setInstance(vota);
                        Votacion.getInstance().start();

                        enviarTodos(Acciones.INICIAR_VOTACION);

                    }
                }
                if (objecto instanceof SalirPartidaPF) {
                    SalirPartidaPF pf = (SalirPartidaPF) objecto;
                    sink.eliminarJugador(jugador);
                    enviarPartidaActual();
                    jugador = null;
                    if (sink.getPartida().getJugadores().isEmpty()) {
                        sink.setPartida(null);
                        jugador = null;
                    } else if (sink.getPartida().getTablero() != null) {
                        if (sink.getPartida().getJugadores().size() != 1) {
                            if (Votacion.getInstance().isAlive()) {
                                Votacion.getInstance().respuestaVotacion(false);
                            }
                        }
                        if (sink.getPartida().getJugadores().size() == 1) {
                            if (Votacion.getInstance().isAlive()) {
                                Votacion.getInstance().respuestaVotacion(false);
                            }
                            TerminarDTO terminar = new TerminarDTO(sink.getPuntuaciones(), Acciones.TERMINAR_PARTIDA_VOTACION);
                            server.sendToAll(terminar);
                            sink.setPartida(null);
                            jugador = null;
                        }
                    }
                    System.out.println(sink.getPartida());
                }

            }
        } catch (IOException | ClassNotFoundException e) {
            if (jugador != null && sink.getPartida()!=null) {
                sink.eliminarJugador(jugador);
                server.desconectarClliente(out);
                enviarPartidaActual();
                if (sink.getPartida().getJugadores().isEmpty()) {
                    sink.setPartida(null);
                    jugador = null;
                } else if (sink.getPartida().getTablero() != null) {
                    if (sink.getPartida().getJugadores().size() != 1) {
                        if (Votacion.getInstance().isAlive()) {
                            Votacion.getInstance().respuestaVotacion(false);
                        }
                    }
                    if (sink.getPartida().getJugadores().size() == 1) {
                        if (Votacion.getInstance().isAlive()) {
                            Votacion.getInstance().respuestaVotacion(false);
                        }
                        if (true) {

                        }
                        TerminarDTO terminar = new TerminarDTO(sink.getPuntuaciones(), Acciones.TERMINAR_PARTIDA_VOTACION);
                        server.sendToAll(terminar);
                        sink.setPartida(null);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
