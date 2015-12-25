package jogo;

import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import jogo.Arena;
import online.ClienteTanque;

public class Packet02Move extends Packet {
	private String usuario;
	private boolean estaAtivo;
	public boolean isEstaAtivo() {
		return estaAtivo;
	}
	private double x, y, angulo;
	private Arena arena = new Arena();
	public Packet02Move(byte[]data){
		super(02);
		String[] dataArray = readData(data).split(",");
		this.usuario = dataArray[0];
		this.x = Double.parseDouble(dataArray[1]);
		this.y = Double.parseDouble(dataArray[2]);
		this.angulo = Double.parseDouble(dataArray[3]);
		this.estaAtivo = Boolean.parseBoolean(dataArray[4]);
	}
	public Packet02Move(String usuario2, double x2, double y2, double angulo2, boolean estaAtivo) {
		super(02);
		this.usuario = usuario2;
		this.x = x2;
		this.y = y2;
		this.angulo = angulo2;
		this.estaAtivo = estaAtivo;
	}

	public String getUsuario() {
		return usuario;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
	public double getAngulo() {
		return angulo;
	}
	@Override
	public void writeData(ClienteTanque cliente) {
		// TODO Auto-generated method stub
	
			cliente.enviarData(getData());
		
		
	}
	@Override
	public byte[] getData() {
		// TODO Auto-generated method stub
		return ("02" + this.usuario + "," + this.x +"," + this.y + "," + this.angulo + ","+this.estaAtivo).getBytes();
	}
	@Override
	public void writeData(online.ServidorTanque servidor) {
		// TODO Auto-generated method stub
		servidor.enviarDataParaTodosOsClientes(getData());
	}

}
