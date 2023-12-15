/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package crearPartida;

import dominio_domino.Partida;
import unirsePartida.IPresentadorUnirse;
import unirsePartida.PresentadorUnirse;

/**
 *
 * @author eduar
 */
public class PresentadorCrear implements IPresentadorCrear{
    
    private modelCrear modelCrear;
    private IPresentadorUnirse presentadorUnirse;
    private VistaCrear pantallaCrear;

    @Override
    public void abrePantalla() {
        modelCrear = new modelCrear();
        presentadorUnirse = new PresentadorUnirse();
        pantallaCrear = new VistaCrear(this);
    }

    @Override
    public void crearPartida(Partida partida) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void mostrarPantallaUnirse() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
