

import java.io.Serializable;

import processing.core.PApplet;

public class Cuadrado {

	private PApplet app;
	private int x, y;
	private float[] col;

	public Cuadrado(PApplet app, int x, int y, float[] col) {
		this.app = app;
		this.x = x;
		this.y = y;
		this.col=col;
	}

	public void pintar() {
		app.fill(255);
		app.strokeWeight(2);
		app.stroke(col[0], col[1], col[2]);
		app.rectMode(PApplet.CENTER);
		app.rect(x, y, 30, 30);
	}
}
