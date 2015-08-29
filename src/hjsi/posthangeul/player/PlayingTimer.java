package hjsi.posthangeul.player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.sound.sampled.Clip;
import javax.swing.JLabel;
import javax.swing.JSlider;

/**
 * This class counts playing time in the form of HH:mm:ss It also updates the time slider
 */
public class PlayingTimer extends Thread {
   private DateFormat dateFormater = new SimpleDateFormat("HH:mm:ss");
   private boolean isRunning = false;
   private boolean isPause = false;
   private boolean isReset = false;
   private long startTime;
   private long pauseTime;

   private JLabel labelRecordTime;
   private JSlider slider;
   private Clip audioClip;

   public PlayingTimer(JLabel labelRecordTime, JSlider slider) {
      this.labelRecordTime = labelRecordTime;
      this.slider = slider;
   }

   public void pauseTimer() {
      this.isPause = true;
   }

   /**
    * Reset counting to "00:00:00"
    */
   public void reset() {
      this.isReset = true;
      this.isRunning = false;
   }


   public void resumeTimer() {
      this.isPause = false;
   }

   @Override
   public void run() {
      this.isRunning = true;

      this.startTime = System.currentTimeMillis();


      while (this.isRunning) {
         // slider.setUI(new TrackLis {
         //
         // protected void scrollDueToClickInTrack(int direction) {
         // // this is the default behaviour, let's comment that out
         // // scrollByBlock(direction);
         //
         // int value = slider.getValue();
         //
         // if (slider.getOrientation() == JSlider.HORIZONTAL) {
         // value = this.valueForXPosition(slider.getMousePosition().x);
         // }
         // slider.setValue(value);
         // }
         // });
         try {
            Thread.sleep(100);
            if (!this.isPause) {
               if (this.audioClip != null && this.audioClip.isRunning()) {
                  this.labelRecordTime.setText(this.toTimeString());
                  int currentSecond = (int) this.audioClip.getMicrosecondPosition() / 1_000_000;
                  this.slider.setValue(currentSecond);
               }
            } else {
               this.pauseTime += 100;
            }
         } catch (

         InterruptedException ex)

         {
            ex.printStackTrace();
            if (this.isReset) {
               this.slider.setValue(0);
               this.labelRecordTime.setText("00:00:00");
               this.isRunning = false;
               break;
            }
         }

      }
   }

   public void setAudioClip(Clip audioClip) {
      this.audioClip = audioClip;
   }

   /**
    * Generate a String for time counter in the format of "HH:mm:ss"
    *
    * @return the time counter
    */
   private String toTimeString() {
      long now = System.currentTimeMillis();
      Date current = new Date(now - this.startTime - this.pauseTime);
      this.dateFormater.setTimeZone(TimeZone.getTimeZone("GMT"));
      String timeCounter = this.dateFormater.format(current);
      return timeCounter;
   }
}
