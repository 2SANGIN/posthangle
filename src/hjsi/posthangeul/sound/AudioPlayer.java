package hjsi.posthangeul.sound;

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
   /**
    * 오디오 클립 (실제 진행 시간을 가지고 있음)
    */
   private Clip audioClip;

   /**
    * 재생 / 정지 상태를 판별
    */
   private boolean isPlaying = false;
   /**
    * 일시정지 상태를 판별
    */
   private boolean isPaused = false;

   /**
    * 오디오 클립을 닫는다.
    */
   public void close() {
      this.isPlaying = false;
      this.isPaused = false;
      this.audioClip.close();
   }

   /**
    * 오디오 클립을 받는다.
    *
    * @return 오디오 클립 객체
    */
   public Clip getAudioClip() {
      return this.audioClip;
   }

   /**
    * 오디오 클립의 길이를 초 단위로 환산한다.
    *
    * @return 오디오 클립의 길이 (초 단위)
    */
   public int getClipSecondLength() {
      return (int) (this.audioClip.getMicrosecondLength() / 1_000_000L);
   }

   /**
    * 오디오 클립의 프레임 위치를 초 단위로 가져온다.
    *
    * @return 오디오 클립의 프레임 위치 (초 단위)
    */
   public int getClipSecondPosition() {
      return (int) (this.audioClip.getMicrosecondPosition() / 1_000_000L);
   }

   /**
    * @return 일시정지 중이라면 true, 아니면 false
    */
   public boolean isPaused() {
      return this.isPaused;
   }

   /**
    * @return 일시정지인가에 상관없이 오디오 클립의 재생이 시작되고 나서 완전히 정지된 적이 없으면 계속 true를 반환한다. 재생이 끝났거나 stop()을 호출했다면
    *         false를 반환한다.
    */
   public boolean isPlaying() {
      return this.isPlaying;
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

      if (this.audioClip != null)
         this.close();

      this.audioClip = (Clip) AudioSystem.getLine(info);
      this.audioClip.addLineListener(this);
      this.audioClip.open(audioStream);
      audioStream.close();
   }

   /**
    * pause audio.
    */
   public void pause() {
      if (this.isPlaying) {
         this.isPaused = true;
         this.audioClip.stop();
      }
   }

   /**
    * Play a given audio file.
    */
   public void play() {
      this.isPlaying = true;
      this.audioClip.start();
   }

   /**
    * resume audio.
    */
   public void resume() {
      if (this.isPlaying) {
         this.isPaused = false;
         this.audioClip.start();
      }
   }

   /**
    * Stop playing back.
    */
   public void stop() {
      if (this.isPaused) {
         this.audioClip.setFramePosition(0);
      }
      this.isPaused = false;
      this.isPlaying = false;
      this.audioClip.stop();
   }

   /**
    * Listens to the audio line events to know when the playback completes.
    */
   @Override
   public void update(LineEvent event) {
      LineEvent.Type type = event.getType();
      if (type == LineEvent.Type.STOP) {
         System.out.println("AUDIOCLIP IS STOPPED!");
         System.out.println(
               "callback: isPaused: " + this.isPaused() + ", isPlaying: " + this.isPlaying());
         if (!this.isPlaying || (!this.isPaused && this.isPlaying)) {
            this.audioClip.setFramePosition(0);

         }
      }
   }
}
