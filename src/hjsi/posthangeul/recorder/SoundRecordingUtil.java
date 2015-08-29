package hjsi.posthangeul.recorder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 * A utility class provides general functions for recording sound.
 *
 * @author www.codejava.net
 *
 */
public class SoundRecordingUtil implements Runnable {
   private static final int BUFFER_SIZE = 4096;

   /**
    * Defines a default audio format used to record
    *
    * @return a default audio format
    */
   static AudioFormat getAudioFormat() {
      float sampleRate = 16000;
      int sampleSizeInBits = 8;
      int channels = 2;
      boolean signed = true;
      boolean bigEndian = true;
      return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
   }

   /**
    * 실제로 녹음 명령을 수행할 쓰레드
    */
   private Thread workerThread;

   /**
    * 녹음 쓰레드 반복문 변수
    */
   private boolean isRunning = false;

   /**
    * 녹음할 사운드 파일 포맷
    */
   private final AudioFormat format;

   /**
    * 사운드 파일의 바이너리 출력 스트림
    */
   private ByteArrayOutputStream recordBytes;

   private TargetDataLine audioLine;

   private byte[] audioDatas;


   /**
    * 녹음 유틸리티를 생성한다.
    */
   public SoundRecordingUtil() {
      this.format = SoundRecordingUtil.getAudioFormat();
      DataLine.Info info = new DataLine.Info(TargetDataLine.class, this.format);

      // checks if system supports the data line
      if (!AudioSystem.isLineSupported(info)) {
         try {
            throw new LineUnavailableException("The system does not support the specified format.");
         } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(-1);
         }
      }
   }

   /*
    * (non-Javadoc)
    *
    * @see java.lang.Runnable#run()
    */
   @Override
   public void run() {
      byte[] buffer = new byte[BUFFER_SIZE];
      int bytesRead = 0;

      this.recordBytes = new ByteArrayOutputStream();

      try {
         this.audioLine = AudioSystem.getTargetDataLine(this.format);
         this.audioLine.open(this.format);
         this.audioLine.start();

         while (this.isRunning) {
            bytesRead = this.audioLine.read(buffer, 0, buffer.length);
            this.recordBytes.write(buffer, 0, bytesRead);
            // TODO 시간초 갱신
            long milliSec = this.audioLine.getLongFramePosition();
            int sec = (int) (milliSec / 10000L);
            milliSec %= 10000L;
            System.out.println(sec + "." + milliSec + " sec...");
         }

         this.audioLine.stop();
         this.audioLine.close();
      } catch (LineUnavailableException e) {
         e.printStackTrace();
         if (this.audioLine != null)
            this.audioLine.close();
      }
   }

   /**
    * Save recorded sound data into a .wav file format.
    *
    * @param wavFileName The file name to be saved. The name is exclusive extension.
    * @throws IOException if any I/O error occurs.
    */
   public void save(String wavFileName) throws IOException {
      if (this.audioDatas != null) {
         File path = new File("records");

         if (!path.exists()) {
            if (path.mkdir())
               System.out.println(path.toString() + " 디렉토리를 생성했습니다.");
            else {
               System.out.println(path.toString() + " 디렉토리 생성에 실패했습니다.");
               System.exit(-1);
            }
         }
         File wavFile = new File(path, wavFileName + ".wav");
         ByteArrayInputStream bais = new ByteArrayInputStream(this.audioDatas);

         AudioInputStream audioInputStream = null;
         try {
            audioInputStream = new AudioInputStream(bais, this.format,
                  this.audioDatas.length / this.format.getFrameSize());
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, wavFile);
         } catch (IOException e) {
            e.printStackTrace();
         } finally {
            if (audioInputStream != null)
               audioInputStream.close();
         }
      }
   }

   /**
    * Start recording sound.
    */
   public void startRecording() {
      this.isRunning = true;
      this.workerThread = new Thread(this);
      this.workerThread.start();
   }

   /**
    * Stop recording sound. and return sound binaries.
    *
    * @return 녹음한 사운드의 바이너리 데이터
    * @throws IOException if any I/O error occurs.
    */
   public byte[] stopRecording() throws IOException {
      this.isRunning = false;
      if (this.audioLine != null) {
         try {
            this.workerThread.join();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
         this.audioLine.drain();
         this.audioLine = null;
      }

      byte[] soundBinaries = null;
      if (this.recordBytes != null) {
         soundBinaries = this.recordBytes.toByteArray();
         this.recordBytes.close();
      }
      return soundBinaries;
   }
}
