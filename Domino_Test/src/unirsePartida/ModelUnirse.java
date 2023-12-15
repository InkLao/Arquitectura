/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package unirsePartida;

import dominio_domino.Partida;
import dominio_domino.Pozo;
import dominio_domino.Tablero;
import dominio_dominodto.JugadorDTO;
import java.net.Socket;

/**
 *
 * @author eduar
 */
public class ModelUnirse {
    
    private Partida partida;
    
    public ModelUnirse() {
    }
    
    public void consultaPartida(Partida partida) {
        this.partida = partida;
    }
    
    public void muestraMensaje(String mensaje){
    
    }
    
    
}
