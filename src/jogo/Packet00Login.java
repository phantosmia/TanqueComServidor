package jogo;
import java.awt.Color;
import online.ClienteTanque;
import online.ServidorTanque;
public class Packet00Login extends Packet {
	private String usuario;
	private double x, y, angulo;
	private Color color;
	public Color getColor() {
		return color;
	}
	private boolean estaAtivo;
	public boolean isEstaAtivo() {
		return estaAtivo;
	}
	public Packet00Login(byte[] data){
		super(00);
		String[] dataArray = readData(data).split(",");
		this.usuario = dataArray[0];
		this.x = Double.parseDouble(dataArray[1]);
		this.y = Double.parseDouble(dataArray[2]);
		this.angulo = Double.parseDouble(dataArray[3]);
		this.estaAtivo = Boolean.parseBoolean(dataArray[4]);
		this.color = new Color(Integer.parseInt((dataArray[5])));
	} //recebendo dados do servidor
	public Packet00Login(String usuario, double d, double e, double f,boolean estaAtivo, Color cor){
		super(00);
		this.usuario = usuario;
		this.x = d;
		this.y = e;
		this.angulo = f;
		this.color = cor;
		this.estaAtivo = estaAtivo;
	} //enviando dados para o servidor
	@Override
	public void writeData(ClienteTanque cliente) {
		// TODO Auto-generated method stub
		cliente.enviarData(getData());
	}
	
	@Override
	public void writeData(ServidorTanque servidor) {
		// TODO Auto-generated method stub
		servidor.enviarDataParaTodosOsClientes(getData());
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
	public byte[] getData() {
		// TODO Auto-generated method stub
		return ("00"+ this.usuario + "," + getX() + "," + getY() + "," + getAngulo() +"," + isEstaAtivo()+"," + getColor().getRGB()).getBytes();
	}

}
