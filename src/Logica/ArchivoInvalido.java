package Logica;

/**
 * 
 * @author agusd
 *	Modela una situacion de error durante la lectura de un archivo.
 */
public class ArchivoInvalido extends Exception{
	public ArchivoInvalido(String ms) {
		super(ms);
	}
}
