
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Observable;

public class Comunicacion extends Observable implements Runnable {

	public MulticastSocket s;
	private final int PORT = 6000;
	private final String GROUP_ADDRESS = "225.5.6.7";
	private boolean life = true;
	private boolean identificado;
	private int id;

	public Comunicacion() {
		try {
			s = new MulticastSocket(PORT);
			InetAddress host = InetAddress.getByName(GROUP_ADDRESS);
			s.joinGroup(host);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			autoID();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void autoID() throws IOException {
		try {
			enviar(new MensajeID("Hola soy nuevo"), GROUP_ADDRESS);
			s.setSoTimeout(500);
			while (!identificado) {
				DatagramPacket dPacket = recibir();
				if (dPacket != null) {
					MensajeID msg = (MensajeID) deserialize(dPacket.getData());
					String contenido = msg.getContenido();

					if (contenido.contains("soy:")) {
						String[] division = contenido.split(":");
						int idLimite = Integer.parseInt(division[1]);
						if (idLimite >= id) {
							id = idLimite + 1;
						}
					}
				}
			}
		} catch (SocketTimeoutException e) {
			identificado = true;
			System.out.println("Mi id es:" + id);
			s.setSoTimeout(0);
		}
	}

	public void enviar(Object info, String ipAdrs) throws IOException {
		byte[] data = serialize(info);
		InetAddress host = InetAddress.getByName(ipAdrs);
		DatagramPacket dPacket = new DatagramPacket(data, data.length, host, PORT);

		s.send(dPacket);
	}

	private DatagramPacket recibir() throws IOException {
		byte[] data = new byte[1024];
		DatagramPacket dPacket = new DatagramPacket(data, data.length);
		s.receive(dPacket);
		return dPacket;
	}

	private byte[] serialize(Object o) {
		byte[] info = null;
		try {
			ByteArrayOutputStream baOut = new ByteArrayOutputStream();
			ObjectOutputStream oOut = new ObjectOutputStream(baOut);
			oOut.writeObject(o);
			info = baOut.toByteArray();

			oOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return info;
	}

	private Object deserialize(byte[] b) {
		Object data = null;
		try {
			ByteArrayInputStream baOut = new ByteArrayInputStream(b);
			ObjectInputStream oOut = new ObjectInputStream(baOut);
			data = oOut.readObject();

			oOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return data;
	}

	@Override
	public void run() {
		while (life) {
			if (s != null) {
				try {
					if (!s.isClosed()) {
						DatagramPacket paquete = recibir();
						if (paquete != null) {
							if (deserialize(paquete.getData()) instanceof MensajeID) {
								MensajeID msg = (MensajeID) deserialize(paquete.getData());
								String contenido = msg.getContenido();

								if (contenido.contains("soy nuevo")) {
									enviar(new MensajeID("soy:" + id), GROUP_ADDRESS);
								}
							}

							if (deserialize(paquete.getData()) instanceof Movimiento) {
								setChanged();
								notifyObservers((Movimiento) deserialize(paquete.getData()));
								clearChanged();
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String getGROUP_ADDRESS() {
		return GROUP_ADDRESS;
	}

	public int getId() {
		return id;
	}

}
