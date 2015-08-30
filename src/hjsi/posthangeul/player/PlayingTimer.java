package hjsi.posthangeul.player;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;
import javax.swing.JSlider;

/**
 * This class counts playing time in the form of HH:mm:ss It also updates the time slider
 */
public class PlayingTimer extends Thread {
   private static final String TIME_FORMAT = "%02d:%02d:%02d";

   /**
    * 주어진 마이크로 초를 hh:mm:ss로 변환한다.
    *
    * @param microSec 갱신할 시간, 마이크로초 기준
    * @return hh:mm:ss 형식의 문자열
    */
   static String toTimeString(long microSec) {
      int milliSec = (int) (microSec / 1000L);
      int sec = milliSec / 1000;
      int min = sec / 60;
      int hour = min / 60;
      min %= 60;
      sec %= 60;
      milliSec /= 100;
      return String.format(TIME_FORMAT, hour, min, sec);
   }

   /**
    * 사운드 파일의 재생을 제어하는 객체
    */
   private AudioPlayer player;

   /**
    * 현재 재생된 시간을 표시하는 라벨
    */
   JLabel labelTimeElapsed;

   /**
    * 현재 재생된 시간을 표시하고 움직일 수 있는 슬라이더
    */
   JSlider sliderTimeProgress;

   /**
    * 현재 슬라이더를 마우스로 잡고 있는지 아닌지 여부를 나타낸다
    */
   boolean isSliderPressed = false;

   /**
    * 사운드 클립의 재생 상황을 보여주고, 재생 구간을 제어할 수 있는 객체를 생성한다.
    *
    * @param player 타이머가 시간을 가져올 대상 객체
    * @param label 재생된 시간을 표시할 라벨
    * @param slider 재생된 시간을 표시하고 조정할 슬라이더
    */
   public PlayingTimer(AudioPlayer player, JLabel label, JSlider slider) {
      this.labelTimeElapsed = label;
      this.sliderTimeProgress = slider;
      this.player = player;

      this.sliderTimeProgress.addMouseMotionListener(new MouseMotionListener() {
         @Override
         public void mouseDragged(MouseEvent e) {
            float percentage =
                  (float) e.getX() / (float) PlayingTimer.this.sliderTimeProgress.getWidth();
            int value = (int) (PlayingTimer.this.sliderTimeProgress.getMaximum() * percentage);
            value = Math.max(value, 0);
            value = Math.min(value, PlayingTimer.this.sliderTimeProgress.getMaximum());
            PlayingTimer.this.sliderTimeProgress.setValue(value);
            player.getAudioClip().setMicrosecondPosition(value * 1_000_000);
            PlayingTimer.this.labelTimeElapsed
                  .setText(PlayingTimer.toTimeString(value * 1_000_000));
         }

         @Override
         public void mouseMoved(MouseEvent e) {}
      });

      this.sliderTimeProgress.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            float percentage =
                  (float) e.getX() / (float) PlayingTimer.this.sliderTimeProgress.getWidth();
            int value = (int) (PlayingTimer.this.sliderTimeProgress.getMaximum() * percentage);
            PlayingTimer.this.sliderTimeProgress.setValue(value);
            player.getAudioClip().setMicrosecondPosition(value * 1_000_000);
         }

         @Override
         public void mousePressed(MouseEvent e) {
            player.pause();
            PlayingTimer.this.isSliderPressed = true;
            super.mousePressed(e);
         }

         @Override
         public void mouseReleased(MouseEvent e) {
            player.resume();
            PlayingTimer.this.isSliderPressed = false;
            super.mouseReleased(e);
         }
      });
   }

   @Override
   public void run() {
      Color fg = this.labelTimeElapsed.getForeground();
      Color bg = this.labelTimeElapsed.getBackground();

      while (this.player.isRunning()) {
         while (this.player.isPaused()) {
            try {
               Thread.sleep(500);
               if (this.labelTimeElapsed.getForeground() == bg)
                  this.labelTimeElapsed.setForeground(fg);
               else if (!this.isSliderPressed)
                  this.labelTimeElapsed.setForeground(bg);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
         this.labelTimeElapsed.setForeground(fg);

         if (this.player.getAudioClip() != null && this.player.getAudioClip().isRunning()) {
            long microSec = this.player.getAudioClip().getMicrosecondPosition();
            this.labelTimeElapsed.setText(PlayingTimer.toTimeString(microSec));
            this.sliderTimeProgress.setValue((int) microSec / 1_000_000);
         }
      }
   }
}
