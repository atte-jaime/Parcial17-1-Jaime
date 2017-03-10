
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import processing.core.PApplet;

public class Logica implements Observer {

	PApplet app;
	private Comunicacion com;
	private String groupAddress;

	private int id;
	private boolean start, iniciar, pintarByC;
	private float r, g, b;
	private float[] col;

	private ArrayList<Bola> bolitas;
	private ArrayList<Cuadrado> cuadraditos;

	public Logica(PApplet app) {
		this.app = app;
		com = new Comunicacion();
		new Thread(com).start();
		com.addObserver(this);
		id = com.getId();
		groupAddress = com.getGROUP_ADDRESS();

		col = new float[3];

		r = app.random(100, 200);
		g = app.random(100, 200);
		b = app.random(100, 200);

		col[0] = r;
		col[1] = g;
		col[2] = b;

		bolitas = new ArrayList<Bola>();
		cuadraditos = new ArrayList<Cuadrado>();
		
		if (id==1) {
			pintarByC=true;
		}

		if (id == 2) {
			try {
				com.enviar(new Movimiento(id, "comenzar", col), groupAddress);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void update(Observable obs, Object o) {
		if (o instanceof Movimiento) {
			Movimiento mov = (Movimiento) o;
			if (id < 3) {
				if (mov.getEmisor() == 0) {
						if (mov.getContenido().contains(":")) {
							String[] pos = mov.getContenido().split(":");
							int xTemp = Integer.parseInt(pos[0]);
							int yTemp = Integer.parseInt(pos[1]);
							bolitas.add(new Bola(app, xTemp, yTemp, mov.getColor()));
						}
				}

				if (mov.getEmisor() == 1) {
					if (mov.getContenido().contains("agregar")) {
						pintarByC=true;
					}
				}

				if (mov.getEmisor() == 2) {
						if (mov.getContenido().contains(":")) {
							String[] pos = mov.getContenido().split(":");
							int xTemp = Integer.parseInt(pos[0]);
							int yTemp = Integer.parseInt(pos[1]);
							cuadraditos.add(new Cuadrado(app, xTemp, yTemp, mov.getColor()));
						}
					if (mov.getContenido().contains("comenzar")) {
						iniciar = true;
					}

				}
			}
		}
	}

	public void pintar() {
		if (app.frameCount % 12 == 0) {
			start = true;
		}

		app.stroke(col[0], col[1], col[2]);
		app.rectMode(PApplet.CENTER);
		app.noFill();
		app.rect(app.width / 2, app.height - 30, 30, 30);
		app.textAlign(PApplet.CENTER, PApplet.CENTER);
		app.fill(col[0], col[1], col[2]);
		app.text(id, app.width / 2, app.height - 30);

		if (id >= 3) {
			app.fill(0);
			app.text("Jugadores completos, sorry", app.width / 2, app.height / 2);
		}
		
		if (pintarByC) {
			for (int i = 0; i < bolitas.size(); i++) {
				bolitas.get(i).pintar();
			}

			for (int i = 0; i < cuadraditos.size(); i++) {
				cuadraditos.get(i).pintar();
			}
		}
		

		if (iniciar) {
			if (app.mousePressed) {
				if (id == 0) {
					try {
						com.enviar(new Movimiento(id, app.mouseX + ":" + app.mouseY, col), groupAddress);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (id == 1) {
					try {
						com.enviar(new Movimiento(id, "agregar", col), groupAddress);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (id == 2) {
					try {
						com.enviar(new Movimiento(id, app.mouseX + ":" + app.mouseY, col), groupAddress);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				start = false;
			}
		} else if (!iniciar && id < 3) {
			app.fill(0);
			app.text("Esperando jugadores", app.width / 2, app.height / 2);
		}
	}

}
