package Logica;

import javax.swing.ImageIcon;

public abstract class Entidad {
	protected ImageIcon grafico;
	protected String[] imagenes;

	/**
	 * Permite actualizar el valor de un grafico.
	 * @param indice valor del grafico.
	 */
	public void actualizar(int indice) {
		if (indice < this.imagenes.length) {
			ImageIcon imageIcon = new ImageIcon(this.getClass().getResource(this.imagenes[indice]));
			this.grafico.setImage(imageIcon.getImage());
		}
	}

	/**
	 * Permite obtener la imagen de un grafico.
	 * @return imagen de un grafico.
	 */
	public ImageIcon getGrafico() {
		return this.grafico;
	}

	/**
	 * Permite establecer la imagen a un grafico.
	 * @param grafico imagen de un grafico.
	 */
	public void setGrafico(ImageIcon grafico) {
		this.grafico = grafico;
	}

	public String[] getImagenes() {
		return this.imagenes;
	}
}
