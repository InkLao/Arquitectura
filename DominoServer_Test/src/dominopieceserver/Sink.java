package dominopieceserver;

import dominio_domino.FichaJugador;
import dominio_domino.FichaPozo;
import dominio_domino.FichaTablero;
import dominio_domino.Jugador;
import dominio_domino.Partida;
import dominio_domino.Pozo;
import dominio_domino.Tablero;
import dominio_dominodto.FichaDTO;
import dominio_dominodto.FichaTableroDTO;
import dominio_dominodto.JugadorDTO;
import dominio_dominodto.PartidaDTO;
import dominio_dominodto.PozoDTO;
import dominio_dominodto.TableroDTO;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author eduar
 */
public class Sink {
    
    private volatile static Sink instance;
    public Partida partida;
    public int turnosPasados=0;

    public static synchronized Sink getInstance() {
        if (instance == null) {

            instance = new Sink();

        }
        return instance;
    }

    public Sink() {

    }

    public void CrearPartida(PartidaDTO p) {
        partida = new Partida(p.getNumeroFichas());

    }

    public Partida getPartida() {
        return partida;
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
    }

    public void agregarJugador(JugadorDTO j) {
        Jugador juga = new Jugador(j.getNombre(), j.getAvatar());
        juga.setId(j.getId());
        partida.addJugador(juga);
    }

    public void eliminarJugador(JugadorDTO j) {
        Jugador juga = getJugador(j.getId());
        if (juga==null) {
            return;
        }
        for (FichaJugador fichaJugador : juga.getFichasJugador()) {
            FichaPozo f = new FichaPozo(fichaJugador.getImagen(), fichaJugador.getPuntoAbajo(), fichaJugador.getPuntoArriba());
            partida.getPozo().addFichasPozo(f);
        }
        int indiceJugador = partida.getJugadores().indexOf(juga);
        partida.eliminarJugador(juga);

        if (indiceJugador != -1) {
            if (indiceJugador < partida.getTurno()) {
                // Si el jugador eliminado estaba antes del turno actual, ajustar el turno
                partida.setTurno((partida.getTurno() - 1));
            } else if (indiceJugador == partida.getTurno()) {
                // Si el jugador eliminado estaba en el turno actual, ajustar el turno

                if (partida.getTurno() >= partida.getJugadores().size()) {
                    // Ajustar el turno si es necesario para no exceder el índice máximo de la lista
                    partida.setTurno(0);
                }
            }
        }
        System.out.println(partida.getTurno());

    }
//    public int indiceJugador(juga){
//        for (int i = 0; i < 10; i++) {
//            
//        }
//    }

    public PartidaDTO getPartidaDTO() {

        PartidaDTO partidaDTO = new PartidaDTO();

        partidaDTO.setNumeroFichas(partida.getNumeroFichas());

        // Copia la información de los jugadores
        List<JugadorDTO> jugadoresDTO = new ArrayList<>();

        for (Jugador jugador : partida.getJugadores()) {
            JugadorDTO jugadorDTO = new JugadorDTO();
            // Copia los atributos relevantes de Jugador a JugadorDTO
            jugadorDTO.setNombre(jugador.getNombre());
            List<FichaDTO> fichasDTO = new ArrayList<>();

            for (FichaJugador f : jugador.getFichasJugador()) {
                FichaDTO fichaDTO = new FichaDTO(f.getImagen(), f.getPuntoAbajo(), f.getPuntoArriba());
                fichasDTO.add(fichaDTO);
            }

            jugadorDTO.setFichasJugador(fichasDTO);
            jugadorDTO.setAvatar(jugador.getAvatar());
            jugadorDTO.setId(jugador.getId());
            // Copia otros atributos necesarios
            jugadoresDTO.add(jugadorDTO);
        }
        partidaDTO.setJugadores(jugadoresDTO);

        if (partida.getTablero() == null) {
            return partidaDTO;
        }

        // Copia la información del tablero y el pozo
        List<FichaTableroDTO> fichasTableroDTO = new ArrayList<>();
        for (FichaTablero f : partida.getTablero().getFichasTablero()) {
            FichaTableroDTO fichaT = new FichaTableroDTO(f.getImagen(), f.getPuntoAbajo(), f.getPuntoArriba());
            fichaT.setConectarAbajo(f.isConectarAbajo());
            fichaT.setConectarAbajo(f.isConectarArriba());
            fichasTableroDTO.add(fichaT);
        }
        partidaDTO.setTablero(new TableroDTO(fichasTableroDTO));
        List<FichaDTO> fichasDTO = new ArrayList<>();

        for (FichaPozo f : partida.getPozo().getFichasPozo()) {
            FichaDTO fichaDTO = new FichaDTO(f.getImagen(), f.getPuntoAbajo(), f.getPuntoArriba());
            fichasDTO.add(fichaDTO);
        }

        partidaDTO.setPozo(new PozoDTO(fichasDTO));
        partidaDTO.setTurno(partida.getTurno());

        return partidaDTO;
    }

    public void iniciarPartida() {
        this.crearPozo();
        this.crearTablero();
        this.repartirFichas();
        this.determinarTurno();
    }

    public JugadorDTO getJugadorDTO(UUID id) {
        JugadorDTO jugador = null;
        List<JugadorDTO> jugadoresDTO = getListJugadoresDTO();
        for (int i = 0; i < jugadoresDTO.size(); i++) {
            if (id == jugadoresDTO.get(i).getId()) {
                jugador = jugadoresDTO.get(i);
            }
        }
        return jugador;
    }

    public List<JugadorDTO> getListJugadoresDTO() {

        List<JugadorDTO> jugadoresDTO = new ArrayList<>();

        for (Jugador jugador : partida.getJugadores()) {

            JugadorDTO jugadorDTO = new JugadorDTO();
            // Copia los atributos relevantes de Jugador a JugadorDTO
            jugadorDTO.setNombre(jugador.getNombre());

            List<FichaDTO> fichasDTO = new ArrayList<>();

            for (FichaJugador f : jugador.getFichasJugador()) {
                FichaDTO fichaDTO = new FichaDTO(f.getImagen(), f.getPuntoAbajo(), f.getPuntoArriba());
                fichasDTO.add(fichaDTO);
            }
            jugadorDTO.setFichasJugador(fichasDTO);
//            

            jugadorDTO.setAvatar(jugador.getAvatar());
            jugadorDTO.setId(jugador.getId());
            // Copia otros atributos necesarios
            jugadoresDTO.add(jugadorDTO);

        }
        return jugadoresDTO;
    }

    public void determinarTurno() {

        partida.determinarTurnos();

    }

    public void crearTablero() {
        partida.setTablero(new Tablero());
    }

    public void crearPozo() {
        partida.setPozo(new Pozo());
    }

    public void repartirFichas() {
        partida.reparteFichas();
    }

    public Jugador getJugador(UUID id) {
        for (Jugador jugadorList : partida.getJugadores()) {
            if (jugadorList.getId().equals(id)) {
                return jugadorList;
            }
        }
        return null;
    }

    public void pasarTurno() {
        partida.pasarTurno();
    }

    public boolean verificarPartida(String avatar) {
        PartidaDTO partida = getPartidaDTO();
        for (int i = 0; i < partida.getJugadores().size(); i++) {
            JugadorDTO jugadorLista = getPartidaDTO().getJugadores().get(i);
            if (jugadorLista.getAvatar().equalsIgnoreCase(avatar)) {
                return false;
            }
        }
        return true;
    }

    public boolean robarFicha(JugadorDTO jugadorDTO) {
        FichaPozo p = partida.getPozo().obtenerFichaAleatoria();

        if (p == null) {
            return false;
        }

        Jugador jugadorRoboFicha = this.getJugador(jugadorDTO.getId());
        FichaJugador f = new FichaJugador(p.getImagen(), p.getPuntoAbajo(), p.getPuntoArriba());
        jugadorRoboFicha.addFichasJugador(f);

        return true;
    }

    public boolean pasarTurno(JugadorDTO jugadorDTO) {
        if (jugadorDTO.getId().equals(this.partida.jugadorTurno().getId())) {
            partida.pasarTurno();
            turnosPasados++;
            return true;
        }
        return false;
    }
    public boolean comprobarPartidaCerrada(){
        if (turnosPasados>=4) {
            if (partida.getPozo().getFichasPozo().isEmpty()) {
                if (!revisarFichasJugadores()) {
                   return true; 
                }
            }
        }
        return false;
    }
    public boolean revisarFichasJugadores(){
        for (Jugador jugadore : partida.getJugadores()) {
            for (FichaJugador fichaJugador : jugadore.getFichasJugador()) {
                
                FichaTablero fichaNormal = new FichaTablero(fichaJugador.getImagen(), fichaJugador.getPuntoAbajo(), fichaJugador.getPuntoArriba());
                if (partida.getTablero().validaColocarFicha(fichaNormal)) {
                    return true;
                }
            }
        }
        return false;
    }
    public int getTurnosPasados() {
        return turnosPasados;
    }

    public void setTurnosPasados(int turnosPasados) {
        this.turnosPasados = turnosPasados;
    }

    public FichaTableroDTO validarMovimiento(FichaTableroDTO ficha, int zona) {
        turnosPasados=0;
        FichaTablero fichaNormal = new FichaTablero(ficha.getImagen(), ficha.getPuntoAbajo(), ficha.getPuntoArriba());
        boolean valida = false;

        if (zona == 1) {
            if (partida.getTablero().validaZonaInical(fichaNormal)) {
                valida = true;
            }
        } else if (zona == 2) {
            if (partida.getTablero().validaLadoDerecho(fichaNormal)) {
                valida = true;
            }
        } else if (zona == 3) {
            if (partida.getTablero().validaLadoIzquierdo(fichaNormal)) {
                valida = true;
            }
        }
        if (!valida) {
            return null;
        } else {

            FichaJugador fichaJugador = new FichaJugador(ficha.getImagen(), ficha.getPuntoAbajo(), ficha.getPuntoArriba());
            partida.jugadorTurno().removerFichaJugador(fichaJugador);
        }

        FichaTableroDTO fichaDto = new FichaTableroDTO(fichaNormal.getImagen(), fichaNormal.getPuntoAbajo(), fichaNormal.getPuntoArriba());
        fichaDto.setConectarAbajo(fichaNormal.isConectarAbajo());
        fichaDto.setConectarArriba(fichaNormal.isConectarArriba());

        return fichaDto;
    }

    public List<JugadorDTO> getPuntuaciones() {
        List<JugadorDTO> jugadores = this.getListJugadoresDTO();
        List<JugadorDTO> jugadoresPuntuaciones = new ArrayList();
        List<int[]> puntuaciones = new ArrayList<>();
        int i = 0;

        for (JugadorDTO jugadorDTO : jugadores) {

            int total = 0;
            for (FichaDTO fichaDTO : jugadorDTO.getFichasJugador()) {
                total += fichaDTO.getPuntoArriba();
                total += fichaDTO.getPuntoAbajo();
            }

            int[] jugadorPuntuacion = {i, total};
            i++;
            puntuaciones.add(jugadorPuntuacion);
        }

        Collections.sort(puntuaciones, Comparator.comparingInt(arr -> arr[1]));
        
        for (int j = 0; j < puntuaciones.size(); j++) {
            if (jugadores.get(puntuaciones.get(j)[0]).getFichasJugador().isEmpty()) {
                jugadoresPuntuaciones.add(0,jugadores.get(puntuaciones.get(j)[0]));
            }else{
                jugadoresPuntuaciones.add(jugadores.get(puntuaciones.get(j)[0]));
            }
        }
        

        return jugadoresPuntuaciones;
    }
}
