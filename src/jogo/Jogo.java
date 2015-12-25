package jogo;
import java.applet.Applet;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;



public class Jogo  implements Runnable  {
	
	private static Arena arena = new Arena();
	private static JFrame janela = new JFrame("Raquel Natali e Alec Monteiro - Jogo dos Tanques");

	
	public static void main(String[] args) throws LineUnavailableException, UnsupportedAudioFileException, IOException {
		arena.start();	
		arena.janelaX = new JanelaX(janela, arena);
		janela.setSize(600, 400);
		janela.setResizable(false);
		janela.getContentPane().add(arena);
		janela.setSize(arena.getPreferredSize());
		janela.pack();
		janela.setVisible(true);
		janela.setDefaultCloseOperation(3);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
	
	}

}
