

import java.io.Serializable;

import processing.core.PApplet;

public class Bola {
	private PApplet app;
	private int x, y;
	private float[] col;

	public Bola(PApplet app, int x, int y,float[] col) {
		this.app = app;
		this.x = x;
		this.y = y;
		this.col=col;
	}

	public void pintar() {
		app.noStroke();
		app.fill(col[0], col[1], col[2]);
		app.ellipse(x, y, 30, 30);
	}
}

