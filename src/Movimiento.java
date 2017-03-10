

import java.io.Serializable;

public class Movimiento implements Serializable {

	public int emisor;
	public String data;
	public float[] col;

	public Movimiento(int emisor, String data,float[] col) {
		this.emisor=emisor;
		this.data = data;
		this.col=col;
	}

	public int getEmisor() {
		return emisor;
	}

	public void setEmisor(int emisor) {
		this.emisor = emisor;
	}

	public String getContenido() {
		return data;
	}

	public void setContenido(String data) {
		this.data = data;
	}
	
	public float[] getColor() {
		return col;
	}
}
