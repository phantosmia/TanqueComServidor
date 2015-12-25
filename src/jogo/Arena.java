package jogo;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import online.ClienteTanque;
import online.ServidorTanque;
import online.TanqueMP;
public class Arena extends JComponent implements ActionListener, Runnable {
	private int largura, altura;
	public boolean running;
	public static Tiro balas;
	public int tickCount = 0;
	public Input input;
	public JanelaX janelaX;

	public synchronized ArrayList<TanqueMP> getTanques() {
		return this.tanques;
	}

	private static final long serialVersionUID = 1L;
	private ArrayList<TanqueMP> tanques = new ArrayList<TanqueMP>();
	private Timer contador;
	EfeitoSonoro som;
	public TanqueMP tanque;
	public static Arena arena;
	public ClienteTanque socketClient;
	public ServidorTanque socketServer;
	private Thread thread;
	public JFrame frame;
	
	

	public Arena() {
	}

	public void init(int largura, int altura)
			throws LineUnavailableException, UnsupportedAudioFileException, IOException {
		this.largura = largura;
		this.altura = altura;
		requestFocus();
		setFocusable(true);
		contador = new Timer(100, this);
		contador.start();
		EfeitoSonoro.init();
		arena = this;
		input = new Input(this);
		Color c = JColorChooser.showDialog(this, "Escolha a cor do tanque", Color.black);
		String usuario = "";
		String usuarioTeste = "";
		System.out.println(tanques.size());
		while (usuario.equals("")) {
			boolean temUsuarioIgual = false;
			usuarioTeste = JOptionPane.showInputDialog(this, "Por favor, digite um nome de usuario");
			if (tanques.size() > 0) {
				for (TanqueMP t : tanques) {
					if (usuario.equals(t.getUsuario())) {
						temUsuarioIgual = true;
						
					}
				}
			}
			if(temUsuarioIgual == false){
				usuario = usuarioTeste;
				
			}
	
		}
		
		tanque = new TanqueMP(false, 100, 200, 0, c, usuario, input, null, -1);
		arena.adicionaTanque(tanque);
		
			Packet00Login loginPacket = new Packet00Login(tanque.getUsuario(), tanque.getX(), tanque.getY(),
					tanque.getAngulo(), tanque.isEstaAtivo(), tanque.getCor());
			if (socketServer != null) {
				System.out.println("hey man");
				socketServer.addConnection((TanqueMP) tanque, loginPacket);
			}
			loginPacket.writeData(socketClient);
		
		// System.out.println("fa"+socketServer);

	}

	public void adicionaTanque(TanqueMP tanqueMP) {
		this.getTanques().add(tanqueMP);
	}

	public void removerTanqueMP(String usuario) {
		int index = 0;
		for (TanqueMP t : getTanques()) {
			if (t instanceof TanqueMP && ((TanqueMP) t).getUsuario().equals(usuario)) {
				break;
			}
			index++;
		}
		this.getTanques().remove(index);
	}

	public int getTanqueMPIndex(String usuario) {
		int index = 0;
		for (TanqueMP t : getTanques()) {
			if (t instanceof TanqueMP && ((TanqueMP) t).getUsuario().equals(usuario)) {
				break;
			}
			index++;
		}
		return index;
	}

	public void moverTanque(String usuario, double x, double y, double a, boolean estaAtivo) {
		int index = getTanqueMPIndex(usuario);
		TanqueMP tanque = (TanqueMP) this.getTanques().get(index);
		tanque.setX(x);
		tanque.setY(y);
		tanque.setAngulo(a);
		tanque.setEstaAtivo(estaAtivo);
	}

	public void criarTiroTanque(String usuario, double x, double y, double a, Tiro t) {
		int index = getTanqueMPIndex(usuario);
		TanqueMP tanque = (TanqueMP) this.getTanques().get(index);
		tanque.adicionarBalas(t);
	}

	public synchronized void start() throws UnknownHostException {
		running = true;
		thread = new Thread(this);
		thread.start();
		
			if (JOptionPane.showConfirmDialog(this, "Vc deseja iniciar um servidor?") == 0) {
				socketServer = new ServidorTanque(this);
				socketServer.start();
			}
			socketClient = new ClienteTanque(this, "localhost");
			socketClient.start();
		System.out.println(socketClient);
		System.out.println(socketServer);
	}

	public synchronized void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	public Dimension getPreferredSize() {
		return new Dimension(largura, altura);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(new Color(245, 245, 255));
		g2d.fillRect(0, 0, largura, altura);
		g2d.setColor(new Color(220, 220, 220));
		for (int _largura = 0; _largura <= largura; _largura += 20)
			g2d.drawLine(0, _largura, largura, _largura);
		for (int _altura = 0; _altura <= largura; _altura += 20)
			g2d.drawLine(0, _altura, largura, _altura);
		for (TanqueMP t : tanques)
			if (t.isEstaAtivo() == false)
				t.draw(g2d);
		// desenhando os tiros
		for (TanqueMP t : tanques) {
			ArrayList balas = t.getBalas();
			for (int i = 0; i < balas.size(); i++) {
				Tiro tiro = (Tiro) balas.get(i);
				g.setColor(Color.YELLOW);
				g.fillRect((int) tiro.getX(), (int) tiro.getY(), 10, 5);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		System.out.println(tanques.size());
		// movimentacao dos tiros caso eles tenham sido acionados
		for (TanqueMP t : tanques) {
			ArrayList balas = t.getBalas();
			for (int i = 0; i < balas.size(); i++) {
				Tiro tiro = (Tiro) balas.get(i);
				if (tiro.isEstaAtivo() == true) {
					tiro.mover();
				} else {
					balas.remove(i);
				}
			}
		}
		// colisao de tiros com a parede
		for (TanqueMP t : tanques) {
			ArrayList balas = t.getBalas();
			for (int i = 0; i < balas.size(); i++) {
				Tiro tiro = (Tiro) balas.get(i);
				if (tiro.getX() < (getWidth()) && tiro.getY() < (getHeight()) && tiro.getX() >= 0 && tiro.getY() >= 0) {

				} else {
					balas.remove(i); // se chegar na parede, removemos o tiro da
										// lista
				}
			}
		}
		// colisao com as paredes.
		for (TanqueMP t : tanques) {
			if (t.getX() < (getWidth() - 20) && t.getY() < (getHeight() - 20) && t.getX() - 20 >= 0
					&& t.getY() - 20 >= 0) { // caso nao tenha colidido, apenas
												// segue normalmente
				t.setAngulo(t.getAngulo());

			} else {
				// se colidiu, setamos para a direcao mudar para o angulo
				// oposto.
				if (t.getX() > (getWidth() - 20))
					t.setX(t.getX() - 1);
				if (t.getY() > (getHeight() - 20))
					t.setY((t.getY() - 1));

				t.setAngulo((t.getAngulo() + 180) % 360);

			}
			if (tanques.size() > 0)
				t.mover();
			repaint();
		}
		
		// colisao tiro e tanque
		for (TanqueMP t : tanques) {
			ArrayList balas = t.getBalas();

			for (int i = 0; i < balas.size(); i++) {
				for (TanqueMP t1 : tanques) {
					Tiro tiro = (Tiro) balas.get(i);
					//System.out.println("A arena printa"+t1.isEstaAtivo());
					if (tiro.getBordasTiro().intersects(t1.getBordasTanque())
							&& (!tiro.getUsuario().equals(t1.getUsuario()))) {
						EfeitoSonoro.EXPLODIR.play();
						t1.setEstaAtivo(true);
						// tanques2.add(t1); //evitando a modification
						// exception, criei uma nova lista para armazenar os
						// tanques que serao removidos
						
						balas.remove(tiro);
						break;
					}

				}
			}
		}
		// tanques.removeAll(tanques2); //removo os tanques apenas quanto o loop
		// termina.
		// colisao tanques
		for (TanqueMP t : tanques) {
			for (TanqueMP t1 : tanques) {
				if (t.getBordasTanque().intersects(t1.getBordasTanque()) && t != t1) {
					// t1.setAngulo((t1.getAngulo() + 180) %360);
					// se colidiu, setamos para a direcao mudar para o angulo
					// oposto.
					/*
					 * int tempVelocidade;
					 * 
					 * t.setVelocidade(0); t.setAngulo((t.getAngulo() + 180) %
					 * 360); t.setX(t.getX()); t.setY((t.getY()));
					 * t.setFlag(true);
					 */
					// se colidiu, setamos para a direcao mudar para o angulo
					// oposto.

					break;
				}

			}
		}
	}

	public void tick() {
		for (int i = 0; i < getTanques().size(); i++) {
			Tanque currElement = getTanques().get(i);
			currElement.tick(); //chama o metodo tick do tanque que atualiza
			//posicoes, angulos e inputs recebidos do tanque.
		}
	}

	@Override
	public void run() {// o run executa de uma maneira extremamente rapida. para amenizar isso,
		//existe uma formula geral, em que vc seta o tempo que ele ira atualizar.
		//neste jogo, sera 60 atualizacoes por segundo.
		//pegamos a formula geral de um tutorial. note que o D significa double/
		long lastTime = System.nanoTime(); // nanoTime para sermos o mais precisos possivel
		double nsPerTick = 1000000000D / 60D; //nanosegundos por tick.
		double delta = 0; //delta eh quantos nanosegundos ja se passaram ate entao.
		try {
			init(600, 400);
		} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// TODO Auto-generated method stub
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;
			while (delta >= 1) {
				tick();
				delta -= 1;
			}
			try {
				Thread.sleep(2);
			} catch (Exception e) {
				e.printStackTrace();
			}

			
		}
	}

}
