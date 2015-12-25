package jogo;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
public class Tanque  {
private Input input;
private ArrayList<Tiro> balas = new ArrayList<Tiro>();
private double x, y;
private String usuario;
public String getUsuario() {
	return usuario;
}
public void setUsuario(String usuario) {
	this.usuario = usuario;
}
private boolean flag = false;
public boolean isFlag() {
	return flag;
}
public void setFlag(boolean flag) {
	this.flag = flag;
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
public double getVelocidade() {
	return velocidade;
}
public Color getCor() {
	return cor;
}
public boolean isEstaAtivo() {
	return estaAtivo;
}
private double angulo;
public void setX(double x) {
	this.x = (int) x;
}
public void setY(double y) {
	this.y = (int) y;
}
public void setAngulo(double angulo) {
	this.angulo = angulo;
}
public void setVelocidade(double velocidade) {
	this.velocidade = velocidade;
}
public void setCor(Color cor) {
	this.cor = cor;
}
public Tanque(){
}
private double velocidade;
private Color cor;
private int corInt;
public int getCorInt() {
	return corInt;
}
public void setCorInt(int corInt) {
	this.corInt = corInt;
}
private boolean estaAtivo;
public Tanque(boolean estarAtivo,double d, double e, double f, Color cor, String usuario, Input input){
	this.x = d;
	this.y = e;
	this.input = input;
	this.usuario = usuario;
	this.angulo = f;
	this.cor = cor;
	corInt = cor.getRGB();
	velocidade = 0;
	this.estaAtivo = estarAtivo;
	balas = new ArrayList();
}

public Tanque(boolean estarAtivo,double d, double e, double f, Color cor2, String usuario2) {
	// TODO Auto-generated constructor stub
	this.x = d;
	this.y = e;
	this.angulo = f;
	this.cor = cor2;
	this.usuario = usuario2;
	velocidade = 0;
	this.estaAtivo = estarAtivo;
	balas = new ArrayList();
}
public void tick(){
	if(input != null){
		if (input.up.isPressed()) {
			aumentarVelocidade();
		}
		if (input.down.isPressed()) {
			diminuirVelocidade();
		}
		if (input.left.isPressed()) {
			girarAntiHorario(3);
		}
		if (input.right.isPressed()) {
			girarHorario(3);
		}
		if(input.space.isPressed()){
			
			atirar();
		}
		Packet02Move packet = new Packet02Move(this.getUsuario(), this.getX(), this.getY(), this.getAngulo(),this.isEstaAtivo());
		packet.writeData(Arena.arena.socketClient);
	}
}
public void diminuirVelocidade(){
	setVelocidade(0);
}
public void aumentarVelocidade(){
	if(velocidade < 30)
		velocidade++; // nao quero que o jogado extrapole na velocidade de seu tanque.
}
public void girarHorario(int a){
	if(angulo < 360)
	angulo += a;
	if(angulo == 360)
		angulo = 360;
	if(angulo >= 360)
		angulo = 0;
}
public void girarAntiHorario(int a){
	if(angulo > 0)
	angulo -= a;
	if(angulo <= 0)
		angulo = 360;
	//nao quero angulos menores que 0. meus angulos vao de 0 a 360.
}
public void mover(){
	//evitando que o x e o y sejam negativos para ajudar na colisao.
	x =  Math.abs(x + Math.cos(Math.toRadians(angulo+270)) * velocidade);
	y =  Math.abs(y + Math.sin(Math.toRadians(angulo+270)) * velocidade);
}
public void setEstaAtivo(boolean estaAtivo){
	this.estaAtivo = estaAtivo;
}
public void draw(Graphics2D g2d){
	if(this.estaAtivo == false){
	AffineTransform antes = g2d.getTransform();
	AffineTransform depois = new AffineTransform();
	depois.translate(x,y);
	depois.rotate(Math.toRadians(angulo));
	g2d.transform(depois);
	g2d.setColor(cor);
	g2d.fillRect(-10, -12, 20, 24);
	for(int i = -12; i <= 8; i += 4){
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.fillRect(-15, i, 5, 4);
		g2d.fillRect(10, i, 5, 4);
		g2d.setColor(Color.BLACK);
		g2d.fillRect(-15, i, 5, 4);
		g2d.fillRect(10, i, 5, 4);
	}//canhao
	g2d.setColor(Color.LIGHT_GRAY);
	g2d.fillRect(-3, -25, 6, 25);
	g2d.setColor(cor);
	g2d.drawRect(-3, -25, 6, 25);
	//
	if(estaAtivo){
		g2d.setColor(new Color(120,120,120));
		Stroke linha = g2d.getStroke();
		g2d.setStroke(new BasicStroke(1f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,0, new float[]{8},0));
		g2d.drawRect(-24, -32, 48, 55);
		g2d.setStroke(linha);
		}
	g2d.setTransform(antes);
	}
}
public Shape getRectEnvolvente(){
	AffineTransform at = new AffineTransform();
	at.translate(x, y);
	at.rotate(Math.toRadians(angulo));
	Rectangle rect = new Rectangle(-24,-32,48,55);
	return at.createTransformedShape(rect);
}
public Rectangle getBordasTanque(){
	return new Rectangle((int)x-10,(int)y-10,20,24); //colisao
}
public ArrayList<Tiro> getBalas(){
	return balas;
}
public void adicionarBalas(Tiro t) {
	this.getBalas().add(t);
}

public void removerTiro(String usuario){
	int index = 0;
	for(Tiro t: getBalas()){
		if(t instanceof Tiro && ((Tiro)t).getUsuario().equals(usuario)){
			break;
		}
		index++;
	}
	this.getBalas().remove(index);
}
public int getTanqueMPIndex(String usuario){
	int index = 0;
	for(Tiro t: getBalas()){
		if(t instanceof Tiro && ((Tiro)t).getUsuario().equals(usuario)){
			break;
		}
		index++;
	}
	return index;
}
public void moverTiro(String usuario, double x, double y, double a){
	int index = getTanqueMPIndex(usuario);
	Tiro tanque = (Tiro)this.getBalas().get(index);
	tanque.setX(x);
	tanque.setY(y);
	tanque.setAngulo(a);
}
public void atirar() {
	Tiro t;
	if(isEstaAtivo() == false){
 //procurando da melhor forma possivel, fazer o tiro sair do canhao do TANQUE, de acordo com a rotacao.
  	if(getAngulo() == 180)
		 t = new Tiro(getUsuario(),getX()-6,getY()+25,getAngulo(),20);
	else if (getAngulo() > 180 && getAngulo() < 360)
		t = new Tiro(getUsuario(),getX()-16,getY()+25,getAngulo(),20);
	else if (getAngulo() > 0 && getAngulo() <= 45)
		t = new Tiro(getUsuario(),getX(),getY()+20,getAngulo(),20);
	else if (getAngulo() > 45  && getAngulo() <= 90)
		t = new Tiro(getUsuario(),getX()+20,getY()+20,getAngulo(),20);
 	else if (getAngulo() >= 90  && getAngulo() < 180)
 		t = new Tiro(getUsuario(),getX()-20,getY()+20,getAngulo(),20);
	else if(getAngulo() == 0)
		 t = new Tiro(getUsuario(),getX()-6,getY(),getAngulo(),20);
	else
		 t = new Tiro(getUsuario(),getX(),getY()+25,getAngulo(),20);
	if(getBalas().size() <= 3){
	balas.add(t);
	Packet03Atirar packet = new Packet03Atirar(getUsuario(),getX(),getY(),getAngulo());
    Arena.arena.socketClient.addTiroTela(t,packet);
    packet.writeData(Arena.arena.socketClient);
	}
	}
  
}


}
