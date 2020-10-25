package Logica;

import java.util.Timer;

public class Tiempo {
	private Timer tiempo;
	private int hora, min, seg;

	public Tiempo() {
		tiempo = new Timer();
		hora = min = seg = 0;
	}

	public Timer getTimer() {
		return tiempo;
	}

	public void actualizar_tiempo(int cant) {
		int resto_horas = 0;

		if (cant >= 3600) {
			hora = cant / 3600;
			resto_horas = cant % 3600;
			if (resto_horas >= 60) {
				min = resto_horas / 60;
				seg = resto_horas % 60;
			} else {
				seg = resto_horas;
				min = 0;
			}
		} else {
			if (cant >= 60) {
				hora = 0;
				min = cant / 60;
				seg = cant % 60;
			} else {
				if (cant < 60) {
					hora = 0;
					min = 0;
					seg = cant;
				}
			}
		}
	}

	public int get_hora() {
		return hora;
	}

	public int get_minutos() {
		return min;
	}

	public int get_segundos() {
		return seg;
	}

}