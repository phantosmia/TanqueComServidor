package online;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.InetAddress;

import jogo.Arena;
import jogo.EfeitoSonoro;
import jogo.Input;
import jogo.Packet02Move;
import jogo.Tanque;

public class TanqueMP extends Tanque  {
public InetAddress enderecoIp;
public int porta;
	public TanqueMP(boolean estarAtivo, double d, double e, double f, Color cor, String usuario, Input input, InetAddress enderecoIP, int porta) {
		super(estarAtivo, d, e, f, cor, usuario, input);
		this.enderecoIp = enderecoIP;
		this.porta = porta;
	
		// TODO Auto-generated constructor stub
	}
	public TanqueMP(boolean estaAtivo,double d, double e, double f, Color cor, String usuario,  InetAddress enderecoIP, int porta) {
		super(estaAtivo,d, e, f, cor, usuario);
		this.enderecoIp = enderecoIP;
		this.porta = porta;
	
	
		// TODO Auto-generated constructor stub
	}
	@Override
	public void tick(){
		super.tick();
	}
	
	/*@Override
	 * *public void keyPressed(KeyEvent arg0) {
	 
		// TODO Auto-generated method stub
		int key = arg0.getKeyCode();
		if (key == KeyEvent.VK_LEFT) {
			
			girarAntiHorario(3);
			Packet02Move packet = new Packet02Move(this.getUsuario(), this.getX(), this.getY(), this.getAngulo());
			System.out.println("Qual user?"+this.getUsuario());
			packet.writeData(Arena.arena.socketClient);
			System.out.println(this.getUsuario());
			
			
		}

		if (key == KeyEvent.VK_RIGHT) {
			girarHorario(3);
			Packet02Move packet = new Packet02Move(this.getUsuario(), this.getX(), this.getY(), this.getAngulo());
			packet.writeData(Arena.arena.socketClient);
		
		}

		if (key == KeyEvent.VK_UP) {
			aumentarVelocidade();
			Packet02Move packet = new Packet02Move(this.getUsuario(), this.getX(), this.getY(), this.getAngulo());
			packet.writeData(Arena.arena.socketClient);
		
		}

		if (key == KeyEvent.VK_DOWN) {
			diminuirVelocidade();
			Packet02Move packet = new Packet02Move(this.getUsuario(), this.getX(), this.getY(), this.getAngulo());
			packet.writeData(Arena.arena.socketClient);
			
		}
		if (key == KeyEvent.VK_SPACE) {
			EfeitoSonoro.TIRO.play();
			atirar();
			
		}
		
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	*/

}
