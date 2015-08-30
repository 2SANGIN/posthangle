package hjsi.posthangeul.player;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * An utility class for playing back audio files using Java Sound API.
 *
 */
public class AudioPlayer implements LineListener {
   private static final int SECONDS_IN_HOUR = 60 * 60;
   private static final int SECONDS_IN_MINUTE = 60;

   /**
    * this flag indicates whether the playback completes or not.
    */
   private boolean playCompleted;

   /**
    * this flag indicates whether the playback is stopped or not.
    */
   private boolean isStopped;

   private boolean isPaused;

   private Clip audioClip;

   public Clip getAudioClip() {
      return this.audioClip;
   }

   public String getClipLengthString() {
      String length = "";
      long hour = 0;
      long minute = 0;
      long seconds = this.audioClip.getMicrosecondLength() / 1_000_000;

      System.out.println(seconds);

      if (seconds >= SECONDS_IN_HOUR) {
         hour = seconds / SECONDS_IN_HOUR;
         length = String.format("%02d:", hour);
      } else {
         length += "00:";
      }

      minute = seconds - hour * SECONDS_IN_HOUR;
      if (minute >= SECONDS_IN_MINUTE) {
         minute = minute / SECONDS_IN_MINUTE;
         length += String.format("%02d:", minute);

      } else {
         minute = 0;
         length += "00:";
      }

      long second = seconds - hour * SECONDS_IN_HOUR - minute * SECONDS_IN_MINUTE;

      length += String.format("%02d", second);

      return length;
   }

   public long getClipSecondLength() {
      return this.audioClip.getMicrosecondLength() / 1_000_000;
   }

   public boolean isPaused() {
      return this.isPaused;
   }

   public boolean isRunning() {
      return !this.playCompleted;
   }

   /**
    * Load audio file before playing back
    *
    * @param audioFilePath Path of the audio file.
    * @throws IOException
    * @throws UnsupportedAudioFileException
    * @throws LineUnavailableException
    */
   public void load(String audioFilePath)
         throws UnsupportedAudioFileException, IOException, LineUnavailableException {
      File audioFile = new File(audioFilePath);

      AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

      AudioFormat format = audioStream.getFormat();

      DataLine.Info info = new DataLine.Info(Clip.class, format);

      this.audioClip = (Clip) AudioSystem.getLine(info);

      this.audioClip.addLineListener(this);

      this.audioClip.open(audioStream);
   }

   public void pause() {
      this.isPaused = true;
   }

   /**
    * Play a given audio file.
    *
    * @throws IOException
    * @throws UnsupportedAudioFileException
    * @throws LineUnavailableException
    */
   public void play() throws IOException {

      this.audioClip.start();

      this.playCompleted = false;
      this.isStopped = false;

      while (!this.playCompleted) {
         // wait for the playback completes
         try {
            Thread.sleep(1000);
         } catch (InterruptedException ex) {
            if (this.isStopped) {
               this.audioClip.stop();
               break;
            }
            if (this.isPaused) {
               this.audioClip.stop();
            } else {
               this.audioClip.start();
            }
         }
      }

      this.audioClip.close();
   }

   /**
    * Resume playing here.
    */
   public void resume() {
      this.isPaused = false;
   }

   /**
    * Stop playing back.
    */
   public void stop() {
      this.isStopped = true;
   }

   /**
    * Listens to the audio line events to know when the playback completes.
    */
   @Override
   public void update(LineEvent event) {
      LineEvent.Type type = event.getType();
      if (type == LineEvent.Type.STOP) {
         System.out.println("STOP EVENT");
         if (this.isStopped || !this.isPaused) {
            this.playCompleted = true;
         }
      }
   }
}
