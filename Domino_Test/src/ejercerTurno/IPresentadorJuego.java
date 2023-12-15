/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ejercerTurno;

/**
 *
 * @author eduar
 */
public interface IPresentadorJuego {
    public void determinarTurno();
    public void muestraTurnos();
    public void validaMovimientos();
    public void regresaFicha();
    public void colocaFicha();
    public void eliminarFicha();
    public void mostrarNuevoTurno();
    public void robarFicha();
    public void msgPozoVacio();
    public void dibujarFichas();
}
