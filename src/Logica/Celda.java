package Logica;

public class Celda {
	private Integer valor;
	private EntidadGrafica entidadGrafica;
	private int cuadrante;
	private int fila,columna;
	private boolean inicializo;
	
	public Celda() {
		inicializo = false;
		fila = columna = 0;
		cuadrante = 0;
		this.valor = null;
		this.entidadGrafica = new EntidadGrafica();
	}

	public void actualizar(int valor) {
		this.valor = valor;
		entidadGrafica.actualizar(this.valor);
	}

	public void setCuadrante(int c) {
		cuadrante = c;
	}
	
	public void inicializo(boolean b) {
		inicializo = b;
	}
	
	public boolean inicializo() {
		return inicializo;
	}
	
	public int getCuadrante() {
		return cuadrante;
	}
	
	public void setFila(int f) {
		fila = f;
	}
	
	public void setColumna(int c) {
		columna = c;
	}
	
	public int getFila() {
		return fila;
	}
	
	public int getColumna() {
		return columna;
	}
	
	public int getCantElementos() {
		return this.entidadGrafica.getImagenes().length;
	}

	public Integer getValor() {
		return this.valor;
	}

	public void setValor(Integer valor) {
		if (valor != null && valor < this.getCantElementos()) {
			this.valor = valor;
			this.entidadGrafica.actualizar(this.valor);
		} else {
			this.valor = null;
		}
	}

	public EntidadGrafica getEntidadGrafica() {
		return this.entidadGrafica;
	}

	public Celda getCelda() {
		return this;
	}
	public void setGrafica(EntidadGrafica g) {
		this.entidadGrafica = g;
	}
}
