package online;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
public class ClienteTanque extends Thread{
	private InetAddress enderecoIP;
	private DatagramSocket socket;
	private Arena arena;
	private List<Tiro> tirosNaTela = new ArrayList<Tiro>();
	public ClienteTanque(Arena arena, String enderecoIP){
		this.arena = arena;
		
		try {
			this.socket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.enderecoIP = InetAddress.getByName(enderecoIP);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	public void run(){
		while(true){
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data,data.length); //enviando os dados e o tamanho dos dados pelo pacote
			try {
				socket.receive(packet); // recebendo os dados
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				this.parsePacket(packet.getData(), packet.getAddress(),packet.getPort());
			} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void parsePacket(byte[] data, InetAddress endereco, int porta) throws LineUnavailableException, UnsupportedAudioFileException, IOException{
		String mensagem = new String(data).trim();
		PacketTypes type = Packet.lookupPacket(mensagem.substring(0, 2));
		Packet packet = null;
		switch(type){
		default:
		case INVALIDO:
			break;
		case ATIRAR:
			packet = new Packet03Atirar(data);
			handleTiro((Packet03Atirar)packet);
			break;
		case LOGIN:
			packet = new Packet00Login(data);
			handleLogin((Packet00Login) packet, endereco, porta);
			break;
		case DESCONECTAR: 
			packet = new Packet01Desconectar(data);
			System.out.println("[" + endereco.getHostAddress() + ":" + porta + "]" + ((Packet01Desconectar)packet).getUsuario()+ " saiu do jogo.");
			arena.removerTanqueMP(((Packet01Desconectar)packet).getUsuario());
			break;
		case MOVER:
			packet = new Packet02Move(data);
			handleMove(((Packet02Move) packet));
			break;
		} 
	}
	public void addTiroTela(Tiro tiro, Packet03Atirar packet){
		this.tirosNaTela.add(tiro);
	}
	public void enviarData(byte[] data){
	
			DatagramPacket packet = new DatagramPacket(data,data.length, enderecoIP, 1331);
			try{
				socket.send(packet);
			}
			catch(IOException e){
				e.printStackTrace();
			}
			
		
	}
	private void handleTiro(Packet03Atirar packet){
		Tiro tiro = new Tiro(packet.getUsuario(), packet.getX(), packet.getY(), packet.getAngulo(),20);
		arena.criarTiroTanque(packet.getUsuario(), packet.getX(), packet.getY(), packet.getAngulo(), tiro);
		
	}
	private void handleLogin(Packet00Login packet, InetAddress endereco, int porta){
		System.out.println("[" + endereco.getHostAddress() + ":" + porta + "] " + packet.getUsuario() + " acabou de logar...");
		TanqueMP tanque = new TanqueMP(false,packet.getX(), packet.getY(), packet.getAngulo(),packet.getColor(),packet.getUsuario(),endereco,porta);
		arena.adicionaTanque(tanque);
	
	}
	private void handleMove(Packet02Move packet){
		this.arena.moverTanque(packet.getUsuario(), packet.getX(), packet.getY(), packet.getAngulo(),packet.isEstaAtivo());
		
	}
	
}
