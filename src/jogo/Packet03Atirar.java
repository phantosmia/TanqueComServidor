package jogo;

import online.ClienteTanque;
import online.ServidorTanque;

public class Packet03Atirar extends Packet {
	private String usuario;
	private int velocidade;
	public int getVelocidade() {
		return velocidade;
	}
	public void setVelocidade(int velocidade) {
		this.velocidade = velocidade;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public void setX(double x) {
		this.x = x;
	}
	public void setY(double y) {
		this.y = y;
	}
	public void setAngulo(double angulo) {
		this.angulo = angulo;
	}
	public void setArena(Arena arena) {
		this.arena = arena;
	}

	private double x, y, angulo;
	private Arena arena = new Arena();
	public Packet03Atirar(byte[]data){
		super(03);
		String[] dataArray = readData(data).split(",");
		this.usuario = dataArray[0];
		this.x = Double.parseDouble(dataArray[1]);
		this.y = Double.parseDouble(dataArray[2]);
		this.angulo = Double.parseDouble(dataArray[3]);
	}
	public Packet03Atirar(String usuario2, double x2, double y2, double angulo2) {
		super(03);
		this.usuario = usuario2;
		this.x = x2;
		this.y = y2;
		this.angulo = angulo2;
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
	
	public void writeData(ClienteTanque cliente) {
		// TODO Auto-generated method stub
		
			cliente.enviarData(getData());
		
		
	}

	@Override
	public void writeData(ServidorTanque servidor) {
		// TODO Auto-generated method stub
		servidor.enviarDataParaTodosOsClientes(getData());
	}

	@Override
	public byte[] getData() {
		// TODO Auto-generated method stub
		return ("03" + this.usuario + "," + this.x +"," + this.y + "," + this.angulo).getBytes();
	}

}
