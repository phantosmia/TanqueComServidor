package jogo;
import java.io.IOException;


import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public enum EfeitoSonoro {
   EXPLODIR("explosion.wav"),  
   FUNDO("newAllegiance.wav"),         
   TIRO("tiro.wav"),
   SELECIONADO("yesSir.wav");      
   
   public static enum Volume {
      MUTE, LOW, MEDIUM, HIGH
   }
   
   public static Volume volume = Volume.LOW;
   
   
   private Clip clip;
   
   
   EfeitoSonoro(String soundFileName) {
      try {
  
         java.net.URL url = this.getClass().getClassLoader().getResource(soundFileName);
         AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
         clip = AudioSystem.getClip();
         clip.open(audioInputStream);
      } catch (UnsupportedAudioFileException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (LineUnavailableException e) {
         e.printStackTrace();
      }
   }
  
   public void play() {
      if (volume != Volume.MUTE) {
         if (clip.isRunning())
            clip.stop();   
         clip.setFramePosition(0); 
         clip.start();
      }
   }
   public void background() {
	      if (volume != Volume.MUTE) {
	         clip.loop(Clip.LOOP_CONTINUOUSLY);
	      }
	   }
   

  public static void init() {
      values(); 
   }
}