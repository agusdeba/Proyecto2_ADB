package Logica;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class Sudoku_Juego {
	protected Celda[][] tablero;
	protected Celda[] arreglo_opciones;
	protected Integer[][] tablero_aux; //se usa para el metodo resolver();
	protected Celda ultima_celda;
	protected boolean estado_juego;
	protected int celdas_restantes;
	protected int cant_filas;

	/**
	 * Inicializa el juego.
	 * 
	 * @throws ArchivoInvalido si al momento de leer un archivo este no es numerico
	 *                         o tiene numeros de mas de un digito.
	 */
	public Sudoku_Juego() throws ArchivoInvalido {
		cant_filas = 9;
		estado_juego = true;
		ultima_celda = null;
		celdas_restantes = 81;
		tablero = new Celda[9][9];
		tablero_aux = new Integer[9][9];

		// inicializa los botones de las opciones
		arreglo_opciones = new Celda[9];
		for (int i = 0; i < arreglo_opciones.length; i++) {
			arreglo_opciones[i] = new Celda();
			arreglo_opciones[i].setValor(i);
		}
		comprobar_archivo("Archivo/archivo.txt");
	}

	public Celda get_ultimaCelda() {
		return ultima_celda;
	}

	public void set_ultimaCelda(Celda c) {
		ultima_celda = c;
	}

	public void comprobar_archivo(String nombre_archivo) throws ArchivoInvalido {
		BufferedReader buffer = null;
		String linea;
		String linea_arreglo[];
		Integer numero;
		int fila = 0;
		boolean archivo_valido = true;
		Random random;
		int valor;
		InputStream in = Sudoku_Juego.class.getClassLoader().getResourceAsStream(nombre_archivo);
		InputStreamReader inr = new InputStreamReader(in);
		buffer = new BufferedReader(inr);
		try {

			/*
			 * A medida que se lee el archivo, se insertan los valores en las dos matrices:
			 * -La matriz tablero se inicializa random, para poder generar "distintos"
			 * juegos. -La matriz tablero_aux, se llena completa de tal manera que cuando el
			 * cliente quiera resolver el juego, las celdas del tablero se actualicen de tal
			 * forma que sea un juego valido.
			 */
			while ((linea = buffer.readLine()) != null && archivo_valido) {
				linea_arreglo = linea.split(" ");
				if (linea_arreglo.length == 9) {
					for (int c = 0; c < linea_arreglo.length && fila < cant_filas; c++) {
						if (isNumeric(linea_arreglo[c])) {
							numero = Integer.valueOf(linea_arreglo[c]);
							if (numero > 0 && numero < 10) {
								numero -= 1;
								Celda celda = new Celda();
								tablero[fila][c] = celda;
								random = new Random();
								valor = random.nextInt(7);
								tablero_aux[fila][c] = numero;
								if (valor != 0) {
									celda.setValor(numero);
									celda.inicializo(true);
									celdas_restantes -= 1;
								}
								// la celda sabe su ubicacion en el tablero
								setear_cuadrante(celda, fila, c);
								celda.setFila(fila);
								celda.setColumna(c);
							} else {
								archivo_valido = false;
							}
						} else {
							archivo_valido = false;
						}
					}
				} else {
					archivo_valido = false;
				}
				archivo_valido = archivo_valido && controlar_fila(fila);
				fila += 1;
				if ((fila % 3) == 0) {
					archivo_valido = archivo_valido && controlar_cuadrante(fila - 1) && controlar_cuadrante(fila - 2)
							&& controlar_cuadrante(fila - 3);
				}
			}

			if (fila != cant_filas) {
				archivo_valido = false;
			}
			if (archivo_valido) {
				controlar_columnas();
			}

			if (!archivo_valido)
				throw new ArchivoInvalido("Archivo invalido.");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Resuelve el juego.
	 */
	public void resolver() {
		Celda celda;
		for (int f = 0; f < tablero.length; f++) {
			for (int c = 0; c < tablero[0].length; c++) {
				celda = tablero[f][c];
				celda.actualizar(tablero_aux[f][c]);
			}
		}
		celdas_restantes = 0;
	}

	/**
	 * Verifica si un string parametrizado es un numero.
	 * 
	 * @param cadena cadena a verificar.
	 * @return true en caso que la cadena sea un numero, false caso contrario.
	 */
	private static boolean isNumeric(String cadena) {
		boolean resultado;
		try {
			Integer.parseInt(cadena);
			resultado = true;
		} catch (NumberFormatException excepcion) {
			resultado = false;
		}
		return resultado;
	}

	/**
	 * Permite obtener la cantidad de filas del tablero.
	 * 
	 * @return retorna la cantidad de filas del tablero.
	 */
	public int cant_filas() {
		return cant_filas;
	}

	/**
	 * Permite actualizar el valor de una celda.
	 * 
	 * @param celda celda a modificar.
	 * @param valor valor nuevo de la celda.
	 */
	public void accionar(Celda celda, int valor) {
		celda.actualizar(valor);
	}

	/**
	 * Controla si en una fila hay o no numeros repetidos.
	 * 
	 * @param f fila de la matriz.
	 * @return true en caso de que no haya numeros repetidos en la fila, false caso
	 *         contrario.
	 */
	private boolean controlar_fila(int f) {
		boolean valida = true;
		Integer numero;
		int apariciones;
		for (int c = 0; c < tablero[f].length && valida; c++) {
			numero = tablero_aux[f][c];
			apariciones = 0;
			for (int i = 0; i < tablero[f].length && valida; i++) {
				if (numero == tablero_aux[f][i]) {
					apariciones += 1;
				}
				valida = apariciones <= 1;
			}
		}

		return valida;
	}

	/**
	 * Controla si en las columnas de una matriz hay numeros repetidos.
	 * 
	 * @return false si en alguna columna se repite algun numero, true caso
	 *         contrario.
	 */
	private boolean controlar_columnas() {
		boolean valida = true;
		Integer numero;
		int apariciones;

		for (int c = 0; c < cant_filas && valida; c++) {
			for (int f = 0; f < cant_filas && valida; f++) {
				numero = tablero_aux[f][c];
				apariciones = 0;
				for (int i = 0; i < cant_filas && valida; i++) {
					if (numero == tablero_aux[f][i]) {
						apariciones += 1;
					}
					valida = apariciones <= 1;
				}
			}
		}

		return valida;
	}

	/**
	 * Controla si un cuadrante tiene o no numeros repetidos.
	 * 
	 * @param fila    fila donde arranca el cuadrante.
	 * @param columna columna donde arranca el cuadrante.
	 * @return true si el cuadrante no posee numeros repetidos, false caso
	 *         contrario.
	 */
	private boolean controlar_cuadrante(int cuadrante) {
		boolean valido = true;
		Integer numero;
		int apariciones, fila_tope, col_tope, fila, columna;
		fila = (cuadrante / 3) * 3;
		columna = (cuadrante % 3) * 3;
		fila_tope = fila + 3;
		col_tope = columna + 3;

		for (int f = fila; f < fila_tope && valido; f++) {
			for (int c = columna; c < col_tope && valido; c++) {
				numero = tablero_aux[f][c];
				apariciones = 0;
				for (int f1 = fila; f1 < fila_tope && valido; f1++) {
					for (int c1 = columna; c1 < col_tope && valido; c1++) {
						if (numero == tablero_aux[f1][c1]) {
							apariciones += 1;
						}
						valido = apariciones <= 1;
					}
				}
			}
		}

		return valido;
	}

	/**
	 * Verifica si una fila es valida. Valida: no haya una celda con el valor
	 * parametrizado.
	 * 
	 * @param celda        celda presionada.
	 * @param valor_opcion valor de la opcion.
	 * @return true en caso de que la fila sea valida, false caso contrario.
	 */
	public boolean fila_valida(Celda celda, Integer valor_opcion) {
		boolean es_valido = true;
		int fil = celda.getFila();
		// no comparo con la ultima celda que se inserto
		for (int col = 0; col < tablero[0].length && es_valido; col++) {
			if (tablero[fil][col] != null && tablero[fil][col] != celda) {
				es_valido = tablero[fil][col].getValor() != valor_opcion;
			}
		}

		return es_valido;
	}

	/**
	 * Verifica si una columna es valida. Valida: no haya una celda con el valor
	 * parametrizado.
	 * 
	 * @param celda celda presionada.
	 * @param valor valor de la opcion.
	 * @return true en caso de que la columna sea valida, false caso contrario.
	 */
	public boolean columna_valida(Celda celda, Integer valor) {
		boolean es_valido = true;
		int col = celda.getColumna();
		// no comparo con la ultima celda que se inserto
		for (int f = 0; f < tablero.length && es_valido; f++) {
			if (tablero[f][col] != null && tablero[f][col] != celda) {
				es_valido = tablero[f][col].getValor() != valor;
			}
		}

		return es_valido;
	}

	/**
	 * Verifica si un cuadrante es valido. Valido: no haya una celda con el valor
	 * parametrizado.
	 * 
	 * @param celda ultima celda presionada.
	 * @param valor valor de una opcion.
	 * @return true si el cuadrante es valido, false caso contrario.
	 */
	public boolean cuadrante_valido(Celda celda, Integer valor) {
		boolean es_valido = true;
		int cuadrante, fila, col, fila_tope, col_tope;
		cuadrante = celda.getCuadrante() - 1;
		fila = (cuadrante / 3) * 3;
		col = (cuadrante % 3) * 3;
		fila_tope = fila + 3;
		col_tope = col + 3;
		// no comparo con la ultima celda que se inserto
		for (int f = fila; f < fila_tope && es_valido; f++) {
			for (int c = col; c < col_tope && es_valido; c++) {
				if (tablero[f][c] != null && celda != tablero[f][c]) {
					if (tablero[f][c].getValor() == valor)
						es_valido = false;
				}
			}
		}

		return es_valido;
	}

	/**
	 * Permite obtener el estado del juego.
	 * 
	 * @return true en caso de que el juego sea valido, false caso contrario.
	 */
	public boolean juego_valido() {
		return estado_juego;
	}

	/**
	 * Establece el estado del juego.
	 * 
	 * @param estado estado del juego.
	 */
	public void juego_valido(boolean estado) {
		estado_juego = estado;
	}

	/**
	 * Permite obtener la cantidad de celdas restantes para poder completar el
	 * juego.
	 * 
	 * @return la cantidad de celdas restantes.
	 */
	public int celdas_restantes() {
		return celdas_restantes;
	}

	/**
	 * Decrementa las celdas restantes para poder ganar.
	 */
	public void decrementar_celdas_restantes() {
		celdas_restantes -= 1;
	}

	/**
	 * Setea el cuadrante a la celda de forma que esta misma sepa a que cuadrante
	 * pertenece.
	 * 
	 * @param celda celda a setear cuadrante.
	 * @param fila  fila de la celda.
	 * @param col   columna de la celda.
	 */
	private void setear_cuadrante(Celda celda, int fila, int col) {
		if (col < 3) {
			if (fila < 3) {
				celda.setCuadrante(1);
			} else {
				if (fila < 6) {
					celda.setCuadrante(4);
				} else {
					celda.setCuadrante(7);
				}
			}
		} else {
			if (col < 6) {
				if (fila < 3) {
					celda.setCuadrante(2);
				} else {
					if (fila < 6) {
						celda.setCuadrante(5);
					} else {
						celda.setCuadrante(8);
					}
				}
			} else {
				if (fila < 3) {
					celda.setCuadrante(3);
				} else {
					if (fila < 6) {
						celda.setCuadrante(6);
					} else {
						celda.setCuadrante(9);
					}
				}
			}
		}
	}

	/**
	 * Permite obtener una celda especifica.
	 * 
	 * @param f fila de la celda.
	 * @param c columna de la celda.
	 * @return la celda espefica en caso de que los valores sean validos, null caso
	 *         contrario.
	 */
	public Celda getCelda(int f, int c) {
		Celda celda = null;

		if (f >= 0 && f < cant_filas && c >= 0 && c < cant_filas) {
			celda = tablero[f][c];
		}

		return celda;
	}

	/**
	 * Permite obtener una opcion especifica.
	 * 
	 * @param i indice de la opcion.
	 * @return la celda espeficia en caso de que el indice sea valido, null caso
	 *         contrario.
	 */
	public Celda get_opciones(int i) {
		Celda opcion = null;

		if (i >= 0 && i < arreglo_opciones.length) {
			opcion = arreglo_opciones[i];
		}

		return opcion;
	}

}