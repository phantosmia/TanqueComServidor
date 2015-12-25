package jogo;

import online.ClienteTanque;
import online.ServidorTanque;

public class Packet01Desconectar extends Packet {
    private String usuario;
    public Packet01Desconectar(byte[] data){
    	super(01);
    	this.usuario = readData(data);
    }
    public Packet01Desconectar(String usuario){
    	super(01);
    	this.usuario = usuario;
    }
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
	

	@Override
	public byte[] getData() {
		// TODO Auto-generated method stub
		return ("01"+ this.usuario).getBytes();
	}
	public String getUsuario(){
		return usuario;
	}

}
