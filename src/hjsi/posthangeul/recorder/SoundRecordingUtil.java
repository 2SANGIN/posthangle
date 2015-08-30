package hjsi.posthangeul.recorder;

import java.awt.Color;
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
import javax.swing.JLabel;

/**
 * A utility class provides general functions for recording sound.
 *
 * @author www.codejava.net
 *
 */
public class SoundRecordingUtil implements Runnable {
   /**
    * 시간 표시에 사용할 포맷
    */
   private static final String timeFormat = "%02d:%02d:%02d";

   /**
    * 한 번에 읽어들일 버퍼 크기
    */
   private static final int BUFFER_SIZE = 4096;

   /**
    * Defines a default audio format used to record
    *
    * @return a default audio format
    */
   private static AudioFormat getAudioFormat() {
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
    * 녹음 쓰레드의 일시정지에 사용되는 변수
    */
   private boolean isPaused = false;

   /**
    * 녹음할 사운드 파일 포맷
    */
   private final AudioFormat format;

   /**
    * 사운드 파일의 바이너리 출력 스트림
    */
   private ByteArrayOutputStream recordBytes;

   /**
    * 오디오라인
    */
   private TargetDataLine audioLine;

   /**
    * 시간을 표시할 외부 위젯
    */
   private JLabel timeIndicator;

   /**
    * 녹음 유틸리티를 생성한다.
    *
    * @param indicator 시간을 표시하기 위한 외부 라벨 객체
    */
   public SoundRecordingUtil(JLabel indicator) {
      this.timeIndicator = indicator;
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

   /**
    * 현재 일시중지 되어있는지 상태를 가져온다.
    *
    * @return 일시중지 중이라면 true, 녹음 중/재개 상태라면 false
    */
   public boolean isPaused() {
      return this.isPaused;
   }

   /**
    * 녹음을 일시중지한다.
    *
    * @throws IllegalStateException 녹음 중이 아닐 때 혹은 이미 멈춰있을 때 발생
    */
   public void pauseRecording() throws IllegalStateException {
      if (this.isRunning) {
         if (this.isPaused)
            throw new IllegalStateException("이미 일시중지 상태임");

         this.isPaused = true;
         if (this.audioLine != null)
            this.audioLine.stop();
      } else
         throw new IllegalStateException("녹음 중이 아니라서 일시정지 할 수 없음.");
   }

   /**
    * 녹음을 재개한다.
    *
    * @throws IllegalStateException 녹음 중이 아닐 때 혹은 이미 재개되었을 때 발생
    */
   public void resumeRecording() throws IllegalStateException {
      if (this.isRunning) {
         if (!this.isPaused)
            throw new IllegalStateException("이미 재개된 상태임");

         this.isPaused = false;
         if (this.audioLine != null)
            this.audioLine.start();
      } else
         throw new IllegalStateException("녹음 중이 아니라서 재개 할 수 없음.");
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
            if (this.isPaused) {
               Thread.yield();
            } else {
               bytesRead = this.audioLine.read(buffer, 0, buffer.length);
               this.recordBytes.write(buffer, 0, bytesRead);
               this.updateTimeIndicator(this.audioLine.getMicrosecondPosition());
            }
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
    * Save recorded sound data into a .wav file format by <b>thread.</b>
    *
    * @param wavFileName The file name to be saved. The name is exclusive extension.
    * @param binaries The sound binaries to be saved.
    */
   public void save(String wavFileName, byte[] binaries) {
      Thread saveEmployee = new Thread(() -> {
         if (binaries != null) {
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
            ByteArrayInputStream bais = new ByteArrayInputStream(binaries);

            AudioInputStream audioInputStream = null;
            try {
               audioInputStream = new AudioInputStream(bais, this.format,
                     binaries.length / this.format.getFrameSize());
               AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, wavFile);
               System.out.println(wavFile.toString() + " is saved!");
            } catch (IOException e) {
               e.printStackTrace();
            } finally {
               try {
                  if (audioInputStream != null)
                     audioInputStream.close();
               } catch (Exception e) {
                  e.printStackTrace();
               }
            }
         }
         this.timeIndicator.setForeground(Color.BLACK);
         this.timeIndicator.setText("File saved successfully!");
         try {
            Thread.sleep(2000);
         } catch (Exception e) {
            e.printStackTrace();
         }
         this.timeIndicator.setText("Recording is ready");
      });
      saveEmployee.start();
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
    */
   public byte[] stopRecording() {
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
      try {
         if (this.recordBytes != null) {
            soundBinaries = this.recordBytes.toByteArray();
            this.recordBytes.close();
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
      return soundBinaries;
   }

   /**
    * 주어진 시간으로 시간 표시창에 표시한다.
    *
    * @param microSec 갱신할 시간, 마이크로초 기준
    */
   public void updateTimeIndicator(long microSec) {
      int milliSec = (int) (microSec / 1000L);
      int sec = milliSec / 1000;
      int min = sec / 60;
      int hour = min / 60;
      min %= 60;
      sec %= 60;
      milliSec /= 100;
      this.timeIndicator.setText(String.format(timeFormat, hour, min, sec));
   }
}
