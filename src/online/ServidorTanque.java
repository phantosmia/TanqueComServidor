package online;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import jogo.Arena;
import jogo.Packet;
import jogo.Packet.PacketTypes;
import jogo.Packet00Login;
import jogo.Packet01Desconectar;
import jogo.Packet02Move;
import jogo.Packet03Atirar;
import jogo.Tiro;
public class ServidorTanque extends Thread{
private DatagramSocket socket;
private Arena arena;
private List<Tiro> tirosNaTela = new ArrayList<Tiro>();
private List<TanqueMP> tanquesConectados = Collections.synchronizedList(new ArrayList<TanqueMP>());
public ServidorTanque(Arena arena) throws UnknownHostException{
	this.arena = arena;
	try{
		this.socket = new DatagramSocket(1331); // especificando uma porta qualquer acima de 1024, pois abaixo provavelmente ja estao sendo usadas por outras apps.
	}
	catch(SocketException e){
		e.printStackTrace();
	}
}
public void run(){
	while(true){
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try{
			socket.receive(packet);
		}
		catch(IOException e){
			e.printStackTrace();
		}
		try {
			this.parsePacket(packet.getData(),packet.getAddress(), packet.getPort());
		} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
//checa que tipo de pacote esta sendo enviado 
private void parsePacket(byte[] data, InetAddress endereco, int porta) throws LineUnavailableException, UnsupportedAudioFileException, IOException{
	String mensagem = new String(data).trim();
	PacketTypes type =  Packet.lookupPacket(mensagem.substring(0, 2));
	Packet packet = null;
	switch(type){
	default:
	case INVALIDO:
		break;
	case LOGIN:
		packet = new Packet00Login(data);
		System.out.println("[" + endereco.getHostAddress() + ":" + porta + "] " + ((Packet00Login) packet).getUsuario() + " acabou de se conectar...");
		TanqueMP tanque= new TanqueMP(false,100,200,0,((Packet00Login)packet).getColor(),((Packet00Login)packet).getUsuario(), endereco, porta);
		;
		this.addConnection(tanque, ((Packet00Login)packet));
		break;
	case MOVER:
		packet = new Packet02Move(data);
		this.handleMove(((Packet02Move)packet));
		break;
	case ATIRAR:
		packet = new Packet03Atirar(data);
		this.handleTiro((Packet03Atirar)packet);
		break;
	case DESCONECTAR:
		packet = new Packet01Desconectar(data);
		this.removerConexao((Packet01Desconectar)packet);
		break;
	}
	//enviarDataParaTodosOsClientes(data);
}
public void addTiroTela(Tiro tiro, Packet03Atirar packet){
	this.tirosNaTela.add(tiro);
	packet.writeData(this);
}
public void addConnection(TanqueMP tanque, Packet00Login packet){
	boolean jaConectou = false;
	for(TanqueMP t: this.tanquesConectados){
		if(tanque.getUsuario().equalsIgnoreCase(t.getUsuario())){
			if(t.enderecoIp == null){
				t.enderecoIp = tanque.enderecoIp;
			}
			if(t.porta == -1){
				t.porta = tanque.porta;
			}
			jaConectou = true;
		}
		else{
			enviarData(packet.getData(), t.enderecoIp, t.porta);
			Packet00Login packetNew = new Packet00Login(t.getUsuario(),t.getX(),t.getX(),t.getAngulo(),false,t.getCor());
			
			//packet = new Packet00Login(t.getUsuario(),t.getX(),t.getX(),t.getAngulo());
			enviarData(packetNew.getData(), tanque.enderecoIp, tanque.porta);
			// envia data do servidor para o cliente
		}
	}
	if(!jaConectou){
		this.tanquesConectados.add(tanque);
		
	}
	
}
public void removerConexao(Packet01Desconectar packet){
	this.tanquesConectados.remove(getTanqueMPIndex(packet.getUsuario()));
	packet.writeData(this);
}

public TanqueMP getTanqueMP(String usuario){
	for(TanqueMP tanque : this.tanquesConectados){
		if(tanque.getUsuario().equals(usuario)){
			return tanque;
		}
	}
	return null;
}
public int getTanqueMPIndex(String usuario){
	int index = 0;
	for(TanqueMP tanque: this.tanquesConectados){
		if(tanque.getUsuario().equals(usuario)){
			break;
		}
		index++;
	}
	return index;
}
public void enviarData(byte[] data, InetAddress enderecoIp, int porta){
	
		DatagramPacket packet = new DatagramPacket(data, data.length, enderecoIp, porta);
		try{
			this.socket.send(packet);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	
}
public void enviarDataParaTodosOsClientes(byte[] data){
	for(TanqueMP t: tanquesConectados){
		enviarData(data,t.enderecoIp, t.porta);
	} //enviar todas as modificacoes para todos os clientes
}
private void handleMove(Packet02Move packet){
	if(getTanqueMP(packet.getUsuario()) != null){
		int index = getTanqueMPIndex(packet.getUsuario());
		TanqueMP tanque = this.tanquesConectados.get(index);
		tanque.setX(packet.getX());
		tanque.setY(packet.getY());
		tanque.setAngulo(packet.getAngulo());
		tanque.setEstaAtivo(packet.isEstaAtivo());
		packet.writeData(this);
	}
}
private void handleTiro(Packet03Atirar packet){
	if(getTanqueMP(packet.getUsuario())!= null){
		int index = getTanqueMPIndex(packet.getUsuario());
		TanqueMP tanque = this.tanquesConectados.get(index);
		if(tanque.isEstaAtivo() == false){
		packet.setAngulo(tanque.getAngulo());
		packet.setUsuario(tanque.getUsuario());
		packet.setVelocidade(20);
		if(tanque.getAngulo() == 180){
		packet.setX(tanque.getX()-6);
		packet.setY(tanque.getY()+25);
		}
		if(tanque.getAngulo() == 180){
			packet.setX(tanque.getX()-6);
			packet.setY(tanque.getY()+25);
			}
		
		else if (tanque.getAngulo() > 180 && tanque.getAngulo() < 360)
		{	
			packet.setX(tanque.getX()-16);
			packet.setY(tanque.getY()+25);
		}
		else if (tanque.getAngulo() > 0 && tanque.getAngulo() <= 45)
		{
			packet.setX(tanque.getX());
			packet.setY(tanque.getY()+20);
		//	packet.setVelocidade(20);
		}
		else if (tanque.getAngulo() > 45  && tanque.getAngulo() <= 90)
		{
			packet.setX(tanque.getX()+20);
			packet.setY(tanque.getY()+20);
			//packet.setVelocidade(20);
		}
			
	 	else if (tanque.getAngulo() >= 90  && tanque.getAngulo() < 180){
	 		packet.setX(tanque.getX()-20);
			packet.setY(tanque.getY()+20);
	 	}
	 		
		else if(tanque.getAngulo() == 0){
			packet.setX(tanque.getX()-6);
			packet.setY(tanque.getY());
		} 
		Tiro tiro = new Tiro(packet.getUsuario(), packet.getX(), packet.getY(), packet.getAngulo(), packet.getVelocidade());
		this.addTiroTela(tiro, packet);
			
		
	}
	}
	
}
	

}
