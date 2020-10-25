package GUI;

import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.TimerTask;
import java.awt.Color;
import javax.swing.border.LineBorder;

import Logica.ArchivoInvalido;
import Logica.Celda;
import Logica.Entidad;
import Logica.EntidadReloj;
import Logica.Sudoku_Juego;
import Logica.Tiempo;

import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.Font;

public class Sudoku_GUI extends JFrame {
	private Sudoku_Juego juego;
	private JPanel contentPane;
	private JButton[][] botones_tablero;
	private JButton[] botones_opciones;
	private JButton ultimo_boton;
	private Color color_anterior, error;
	private JLabel horario[];
	private Entidad[] entidades;
	private boolean inicio;
	private JLabel estado_fila, estado_cuadrante, estado_columna;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Sudoku_GUI frame = new Sudoku_GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Sudoku_GUI() {
		try {
			juego = new Sudoku_Juego();
			inicio = false;
			setTitle("Sudoku");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setBounds(100, 100, 709, 573);

			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			contentPane.setBackground(Color.DARK_GRAY);

			botones_opciones = new JButton[9];
			botones_tablero = new JButton[juego.cant_filas()][juego.cant_filas()];
			error = new Color(150, 0, 0);

			JPanel panel_tablero = new JPanel();
			panel_tablero.setBorder(new LineBorder(Color.BLACK));
			panel_tablero.setBounds(0, 0, 520, 420);
			contentPane.add(panel_tablero);
			panel_tablero.setLayout(new GridLayout(juego.cant_filas(), juego.cant_filas(), 0, 0));

			JPanel panel_imagenes = new JPanel();
			panel_imagenes.setBounds(10, 458, 661, 76);
			contentPane.add(panel_imagenes);
			panel_imagenes.setLayout(new GridLayout(0, 9, 0, 0));

			JPanel panel_opciones = new JPanel();
			panel_opciones.setBackground(Color.DARK_GRAY);
			panel_opciones.setBounds(520, 0, 171, 420);
			contentPane.add(panel_opciones);
			panel_opciones.setLayout(null);

			JLabel tiempo_transcurrido = new JLabel("Tiempo transcurrido:");
			tiempo_transcurrido.setHorizontalAlignment(SwingConstants.CENTER);
			tiempo_transcurrido.setFont(new Font("Yu Gothic Medium", Font.BOLD, 15));
			tiempo_transcurrido.setForeground(Color.LIGHT_GRAY);
			tiempo_transcurrido.setBounds(10, 11, 151, 28);
			panel_opciones.add(tiempo_transcurrido);

			JLabel mensaje_error = new JLabel("");
			mensaje_error.setFont(new Font("DialogInput", Font.BOLD, 18));
			mensaje_error.setBounds(190, 431, 493, 25);
			contentPane.add(mensaje_error);
			mensaje_error.setForeground(Color.LIGHT_GRAY);

			JPanel panel_tiempo = new JPanel();
			panel_tiempo.setBounds(0, 38, 171, 42);
			panel_tiempo.setLayout(new GridLayout(0, 8, 0, 0));
			panel_opciones.add(panel_tiempo);
			panel_tiempo.setBackground(Color.DARK_GRAY);

			// ---------------------------------------------------
			// ----------Inicializa el reloj----------------------

			entidades = new EntidadReloj[8];
			horario = new JLabel[8];
			for (int i = 0; i < horario.length; i++) {
				Entidad entidad = new EntidadReloj();
				JLabel digito = new JLabel();
				ImageIcon imagen;

				if (i == 2 || i == 5) {
					entidad.actualizar(10);
					imagen = entidad.getGrafico();
				} else {
					entidad.actualizar(0);
					imagen = entidad.getGrafico();
				}
				entidades[i] = entidad;
				panel_tiempo.add(digito);
				horario[i] = digito;
				digito.addComponentListener(new ComponentAdapter() {
					@Override
					public void componentResized(ComponentEvent e) {
						redimensionar(digito, entidad.getGrafico());
						digito.setIcon(imagen);
					}
				});
			}

			// -------------------------------------

			JLabel label_opciones = new JLabel("Opciones:");
			label_opciones.setBounds(10, 434, 218, 25);
			contentPane.add(label_opciones);
			label_opciones.setFont(new Font("Yu Gothic Medium", Font.BOLD, 18));
			label_opciones.setForeground(Color.LIGHT_GRAY);

			// INICIALIZA LOS BOTONES DE OPCIONES

			for (int i = 0; i < botones_opciones.length ; i++) {
				Celda celda_opcion = juego.get_opciones(i);
				ImageIcon imagen = celda_opcion.getEntidadGrafica().getGrafico();
				JButton boton_opciones = new JButton();
				panel_imagenes.add(boton_opciones);
				botones_opciones[i] = boton_opciones;
				botones_opciones[i].setIcon((imagen));
				boton_opciones.addComponentListener(new ComponentAdapter() {
					@Override
					public void componentResized(ComponentEvent e) {
						boton_opciones.setEnabled(false);
						redimensionar(boton_opciones, imagen);
						boton_opciones.setIcon(imagen);
					}
				});

				boton_opciones.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent evento) {
						Celda ultima_celda = juego.get_ultimaCelda();
						if (ultima_celda != null && boton_opciones.isEnabled()) {
							ultimo_boton.setBackground(color_anterior);
							// obtengo los estados del juego
							boolean columna_valida = juego.columna_valida(ultima_celda, celda_opcion.getValor());
							boolean fila_valida = juego.fila_valida(ultima_celda, celda_opcion.getValor());
							boolean cuadrante_valido = juego.cuadrante_valido(ultima_celda, celda_opcion.getValor());
							boolean juego_valido = columna_valida && fila_valida && cuadrante_valido;

							if (juego_valido && ultima_celda.getValor() != celda_opcion.getValor()) {
								juego.decrementar_celdas_restantes();
							}

							juego.accionar(ultima_celda, celda_opcion.getValor());
							redimensionar(ultimo_boton, ultima_celda.getEntidadGrafica().getGrafico());

							juego.juego_valido(juego_valido);

							if (columna_valida) {
								estado_columna.setText("Columna valida");
							} else {
								estado_columna.setText("Columna invalida");
							}
							if (fila_valida) {
								estado_fila.setText("Fila valida");
							} else {
								estado_fila.setText("Fila invalida");
							}
							if (cuadrante_valido) {
								estado_cuadrante.setText("Cuadrante valido");
							} else {
								estado_cuadrante.setText("Cuadrante invalido");
							}

							if (juego_valido) {
								//desmarco la fila, el cuadrante y la columna
								estado_cuadrante(ultima_celda, null);
								estado_fila(ultima_celda, null);
								estado_columna(ultima_celda, null);
								mensaje_error.setText("");
							} else {
								//marco la fila, cuadrante y columna donde exista un error
								mensaje_error.setText("Corrija el error para poder seguir jugando.");
								estado_cuadrante(ultima_celda, error);
								estado_fila(ultima_celda, error);
								estado_columna(ultima_celda, error);
							}

							if (juego.juego_valido()) {
								ultimo_boton = null;
								juego.set_ultimaCelda(null);
								estado_opciones(false);
							} else {
								ultimo_boton.setBackground(Color.RED);
							}
						}
					}
				});
			}

			// -----------------------------------------------------------------------------
			// ---------------------------INICIALIZA EL TABLERO-----------------------------

			for (int f = 0; f < juego.cant_filas(); f++) {
				for (int c = 0; c < juego.cant_filas(); c++) {
					Celda celda = juego.getCelda(f, c);
					ImageIcon imagen = celda.getEntidadGrafica().getGrafico();
					JButton boton_tablero = new JButton();
					boton_tablero.setOpaque(true);
					botones_tablero[f][c] = boton_tablero;

					pintar_tablero(f, c, boton_tablero);

					boton_tablero.addComponentListener(new ComponentAdapter() {
						@Override
						public void componentResized(ComponentEvent e) {
							redimensionar(boton_tablero, imagen);
							boton_tablero.setIcon(imagen);
						}
					});

					boton_tablero.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent evento) {
							if (inicio) {
								if (juego.juego_valido()) {
									if (ultimo_boton != null) {
										ultimo_boton.setBackground(color_anterior);
									}
									if (celda.inicializo()) {
										estado_opciones(false);
									} else {
										estado_opciones(true);
										juego.set_ultimaCelda(celda);
										ultimo_boton = boton_tablero;
										color_anterior = boton_tablero.getBackground();
										boton_tablero.setBackground(Color.black);
									}
								} else {
									if (ultimo_boton == boton_tablero) {
										estado_opciones(true);
									} else {
										estado_opciones(false);
									}
								}
							}
						}
					});
					panel_tablero.add(boton_tablero);
				}
			}

			// -------------------------------------------------------
			// -------------------RELOJ-------------------------------

			Tiempo tiempo = new Tiempo();

			TimerTask accion = new TimerTask() {
				int cant_tiempo = 0;
				@Override
				public void run() {
					tiempo.actualizar_tiempo(cant_tiempo);
					Integer min1, min2, seg1, seg2, hora2, hora1;
					hora1 =  tiempo.get_hora();
					min1 = tiempo.get_minutos();
					seg1 = tiempo.get_segundos();
					// -------actualiza los segundos---------------------------------
					seg2 = seg1 % 10;
					seg1 = seg1 / 10;
					//primer digito de los segundos
					entidades[6].actualizar(seg1);
					horario[6].setIcon(entidades[6].getGrafico());
					redimensionar(horario[6], entidades[6].getGrafico());
					//segundo digito de los segundos
					entidades[7].actualizar(seg2);
					horario[7].setIcon(entidades[7].getGrafico());
					redimensionar(horario[7], entidades[7].getGrafico());
					
					// -----------actualiza los minutos------------------------------
					min2 = min1 % 10;
					min1 = min1 / 10;
					//primer digito de los minutos
					entidades[3].actualizar(min1);
					horario[3].setIcon(entidades[3].getGrafico());
					redimensionar(horario[3], entidades[3].getGrafico());
					//segundo digito de los minutos
					entidades[4].actualizar(min2);
					horario[4].setIcon(entidades[4].getGrafico());
					redimensionar(horario[4], entidades[4].getGrafico());

					// --------actualiza las horas----------------------------------
					hora2 = hora1 % 10;
					hora1 = hora1 / 10;
					//primer digito de las horas
					entidades[0].actualizar(hora1);
					horario[0].setIcon(entidades[0].getGrafico());
					redimensionar(horario[0], entidades[0].getGrafico());
					//segundo digito de las horas
					entidades[1].actualizar(hora2);
					horario[1].setIcon(entidades[1].getGrafico());
					redimensionar(horario[1], entidades[1].getGrafico());

					cant_tiempo += 1;
				}
			};

			// ---------------------------------------------------------------------------
			// ------------------------Estados del juego----------------------------------

			estado_fila = new JLabel("");
			estado_fila.setHorizontalAlignment(SwingConstants.CENTER);
			estado_fila.setBounds(10, 260, 151, 28);
			panel_opciones.add(estado_fila);
			estado_fila.setFont(new Font("Tahoma", Font.BOLD, 15));
			estado_fila.setForeground(Color.BLACK);
			estado_fila.setBorder(new LineBorder(Color.WHITE));

			estado_columna = new JLabel("");
			estado_columna.setHorizontalAlignment(SwingConstants.CENTER);
			estado_columna.setBounds(10, 289, 151, 28);
			panel_opciones.add(estado_columna);
			estado_columna.setFont(new Font("Tahoma", Font.BOLD, 15));
			estado_columna.setForeground(Color.BLACK);
			estado_columna.setBorder(new LineBorder(Color.WHITE));

			estado_cuadrante = new JLabel("");
			estado_cuadrante.setHorizontalAlignment(SwingConstants.CENTER);
			estado_cuadrante.setBounds(10, 318, 151, 28);
			panel_opciones.add(estado_cuadrante);
			estado_cuadrante.setFont(new Font("Tahoma", Font.BOLD, 15));
			estado_cuadrante.setForeground(Color.BLACK);
			estado_cuadrante.setBorder(new LineBorder(Color.WHITE));

			// ------------------------------------------------------------------------
			// ------------------Inicializa los botones--------------------------------

			JButton terminar = new JButton("Terminar intento");
			terminar.setBounds(10, 155, 151, 23);
			terminar.setFont(new Font("Times New Roman", Font.BOLD, 14));
			terminar.setForeground(Color.BLACK);
			panel_opciones.add(terminar);
			terminar.setEnabled(false);

			terminar.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evento) {
					if (inicio) {
						tiempo.getTimer().cancel();
						if (juego.celdas_restantes() == 0 && juego.juego_valido()) {
							JOptionPane.showMessageDialog(contentPane, "¡Ganaste!", "Felicitaciones",
									JOptionPane.INFORMATION_MESSAGE);
							System.exit(0);
						} else {
							JOptionPane.showMessageDialog(contentPane, "¡Perdiste!", "¡La proxima!",
									JOptionPane.INFORMATION_MESSAGE);
							System.exit(0);
						}
					}
				}
			});

			JButton boton_resolver = new JButton("Resolver intento");
			boton_resolver.setBounds(10, 205, 151, 23);
			panel_opciones.add(boton_resolver);
			boton_resolver.setFont(new Font("Times New Roman", Font.BOLD, 14));
			boton_resolver.setForeground(Color.BLACK);
			boton_resolver.setEnabled(false);

			boton_resolver.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evento) {
					if (inicio && juego.juego_valido()) {
						juego.resolver();
						for (int f = 0; f < juego.cant_filas(); f++) {
							for (int c = 0; c < juego.cant_filas() ; c++) {
								Celda celda = juego.getCelda(f, c);
								ImageIcon imagen = celda.getEntidadGrafica().getGrafico();
								juego.accionar(celda, celda.getValor());
								redimensionar(botones_tablero[f][c], imagen);
								botones_tablero[f][c].setIcon(imagen);
							}
						}
						tiempo.getTimer().cancel();
						JOptionPane.showMessageDialog(contentPane, "¡Ganaste, pero con trampa!", "Estudia la proxima",
								JOptionPane.INFORMATION_MESSAGE);
						System.exit(0);
					}
				}
			});

			JButton boton_iniciar = new JButton("Iniciar");
			boton_iniciar.setFont(new Font("Times New Roman", Font.BOLD, 14));
			boton_iniciar.setForeground(Color.BLACK);
			boton_iniciar.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evento) {
					if (!inicio) {
						inicio = true;
						runTimer(accion, tiempo);
						boton_iniciar.setEnabled(false);
						terminar.setEnabled(true);
						boton_resolver.setEnabled(true);
					}
				}
			});
			boton_iniciar.setBounds(10, 106, 151, 23);
			panel_opciones.add(boton_iniciar);

		} catch (ArchivoInvalido e) {
			JOptionPane.showMessageDialog(contentPane, "El juego no se pudo iniciar", "ERROR",
					JOptionPane.INFORMATION_MESSAGE);
			System.exit(0);
		}
	}

	private void runTimer(TimerTask task, Tiempo tiempo) {
		tiempo.getTimer().schedule(task, 0, 1000);
	}

	// --------------------------------------------------
	// ------------------MARCA-LOS-ESTADOS---------------
	
	/*
	 * -Si tiene error el juego, marca el fondo de las celdas que tienen error con
	 * un rojo mas oscuro, mientras que la ultima celda ingresada se marca con un
	 * rojo mas claro.
	 * 
	 * -Si el juego es valido, las celdas van a tener un fondo nulo.
	 * 
	 */
	
	/**
	 * Pinta de algun color especifico las celdas que tienen el mismo valor que una
	 * celda parametrizada.
	 * @param celda celda.
	 * @param color	color.
	 */
	private void estado_fila(Celda celda, Color color) {
		int fila = celda.getFila();
		Celda celda_aux;
		for (int c = 0; c < juego.cant_filas() ; c++) {
			celda_aux = juego.getCelda(fila, c);
			if (celda_aux.getValor() != null) {
				if (celda_aux.getValor() == celda.getValor()) {
					botones_tablero[fila][c].setBackground(color);
				} else {
					botones_tablero[fila][c].setBackground(null);
				}
			}
		}
	}

	/**
	 * Pinta de algun color especifico las celdas que tienen el mismo valor que una
	 * celda parametrizada.
	 * @param celda celda.
	 * @param color	color.
	 */
	private void estado_columna(Celda celda, Color color) {
		int col = celda.getColumna();
		Celda celda_aux;
		for (int f = 0; f < juego.cant_filas() ; f++) {
			celda_aux = juego.getCelda(f, col);
			if (celda_aux.getValor() != null) {
				if (celda_aux.getValor() == celda.getValor()) {
					botones_tablero[f][col].setBackground(color);
				} else {
					botones_tablero[f][col].setBackground(null);
				}
			}
		}
	}

	/**
	 * Pinta de algun color especifico las celdas que tienen el mismo valor que una
	 * celda parametrizada.
	 * 
	 * @param celda celda.
	 * @param color color.
	 */
	private void estado_cuadrante(Celda celda, Color color) {
		int fila, col;
		int cuadrante = celda.getCuadrante() - 1;
		fila = (cuadrante / 3) * 3;
		col = (cuadrante % 3) * 3;
		int fila_tope = fila + 3;
		int col_tope = col + 3;
		Celda celda_aux;

		for (int f = fila; f < fila_tope; f++) {
			for (int c = col; c < col_tope; c++) {
				celda_aux = juego.getCelda(f, c);
				if (celda_aux.getValor() != null && celda_aux.getValor().equals(celda.getValor())) {
					botones_tablero[f][c].setBackground(color);
				} else {
					botones_tablero[f][c].setBackground(null);
				}
			}
		}
	}

	/**
	 * Actualiza los estados de las opciones. Estan inhabilitadas si el juego tiene
	 * un error, caso contrario, van a estar habilitadas.
	 * 
	 * @param estado estado de las opciones.
	 */
	private void estado_opciones(boolean estado) {
		Celda celda;
		for (int i = 0; i < botones_opciones.length; i++) {
			botones_opciones[i].setEnabled(estado);
			celda = juego.get_opciones(i);
			ImageIcon imagen = celda.getEntidadGrafica().getGrafico();
			redimensionar(botones_opciones[i], imagen);
			botones_opciones[i].setIcon(imagen);
		}
	}

	// ------------------------------------------------------------------
	// -------------------Pinta el tablero-------------------------------
	/**
	 * Pinta el tablero de tal forma que queden bien separados los cuadrantes y las
	 * celdas.
	 * 
	 * @param f     fila del boton.
	 * @param c     columna del boton.
	 * @param boton boton.
	 */
	private void pintar_tablero(int f, int c, JButton boton) {
		if ((f % 3) == 0 && (c % 3) == 0) {
			boton.setBorder(BorderFactory.createMatteBorder(3, 3, 1, 1, Color.DARK_GRAY));
		} else {
			if ((f % 3) == 0) {
				boton.setBorder(BorderFactory.createMatteBorder(3, 1, 1, 1, Color.DARK_GRAY));
			} else {
				if ((c % 3) == 0) {
					boton.setBorder(BorderFactory.createMatteBorder(1, 3, 1, 1, Color.DARK_GRAY));
				} else {
					boton.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY));
				}
			}
		}
	}

	// -----------------------------------------------------------------
	// -----------------Redimensiona las imagenes-----------------------

	private void redimensionar(JLabel label, ImageIcon grafico) {
		Image imagen = grafico.getImage();
		if (imagen != null) {
			Image nueva = imagen.getScaledInstance(label.getWidth(), label.getHeight(), java.awt.Image.SCALE_SMOOTH);
			grafico.setImage(nueva);
			label.repaint();
		}
	}

	private void redimensionar(JButton boton, ImageIcon grafico) {
		Image imagen = grafico.getImage();
		if (imagen != null) {
			Image nueva = imagen.getScaledInstance(boton.getWidth(), boton.getHeight(), java.awt.Image.SCALE_SMOOTH);
			grafico.setImage(nueva);
			boton.repaint();
		}
	}
}
