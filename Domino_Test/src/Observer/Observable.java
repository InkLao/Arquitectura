/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Observer;

/**
 *
 * @author diego
 */
public interface Observable {
    public void agregarObserver(Observer o);
    public void eliminarObserver(Observer o);
    public void notificarObservers(Object o);
}
