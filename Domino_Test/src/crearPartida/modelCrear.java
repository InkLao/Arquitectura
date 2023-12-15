/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package crearPartida;

import dominio_domino.Partida;
import java.nio.channels.Pipe;

/**
 * Completo
 * @author eduar
 */
public class modelCrear {
    
    private Cliente cliente;
    private Partida partida;
    
    public void crearPartida(int fichasIniciales) {
       
    }
    
    public Partida getPartida() {
        return partida;
    }
    
    public void setPartida(Partida partida) {
        this.partida = partida;
    }
    
    public Cliente crearConexion() {

        Cliente cliente = Cliente.getInstance();

        return cliente;
    }
}


