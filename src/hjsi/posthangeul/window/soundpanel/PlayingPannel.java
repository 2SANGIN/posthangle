
package hjsi.posthangeul.window.soundpanel;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.filechooser.FileFilter;

import hjsi.posthangeul.editor.autocomplete.AppFont;
import hjsi.posthangeul.player.AudioPlayer;
import hjsi.posthangeul.player.PlayingTimer;
import hjsi.posthangeul.window.PostHangeulApp;


/**
 * Player <br>
 * 2015. 8. 30.
 *
 * @author HYUNJIN
 */
public class PlayingPannel extends JPanel implements ActionListener {
   @SuppressWarnings("javadoc")
   private static final long serialVersionUID = 8049094210188913162L;

   /**
    * 녹음 객체
    */
   private RecordingPannel recorder = new RecordingPannel(36);

   /**
    *
    */
   private AudioPlayer player = new AudioPlayer();

   /**
    *
    */
   private Thread playbackThread;

   private PlayingTimer timer;
   private boolean isPlaying = false;

   private boolean isPause = false;
   private String audioFilePath;

   private String lastOpenPath;

   private JLabel labelFileName = new JLabel("Playing File:");
   private JLabel labelTimeElapsed = new JLabel("00:00:00");
   private JLabel labelTimeDuration = new JLabel("00:00:00");

   private JButton btnOpen = new JButton("Open");
   private JButton btnPlay = new JButton("Play");
   private JButton btnPause = new JButton("Pause");

   private JSlider sliderTimeProgress = new JSlider();

   private File fpPath = new File("resources");

   public PlayingPannel(PostHangeulApp app) {

      Image image = null;
      ImageIcon iconOpen = null;
      ImageIcon iconPlay = null;
      ImageIcon iconPause = null;

      /* set icon */
      try {
         image = ImageIO.read(new File(this.fpPath, "Open.png")).getScaledInstance(12, 12,
               Image.SCALE_AREA_AVERAGING);
         iconOpen = new ImageIcon(image);

         image = ImageIO.read(new File(this.fpPath, "Play.gif")).getScaledInstance(12, 12,
               Image.SCALE_AREA_AVERAGING);
         iconPlay = new ImageIcon(image);

         image = ImageIO.read(new File(this.fpPath, "Pause.png")).getScaledInstance(12, 12,
               Image.SCALE_AREA_AVERAGING);
         iconPause = new ImageIcon(image);
      } catch (IOException e) {
         e.printStackTrace();
      }

      this.btnOpen.setIcon(iconOpen);
      this.btnOpen.setFont(AppFont.fontSans.deriveFont(14f));

      this.btnPlay.setIcon(iconPlay);
      this.btnPlay.setFont(AppFont.fontSans.deriveFont(14f));
      this.btnPlay.setEnabled(false);

      this.btnPause.setIcon(iconPause);
      this.btnPause.setFont(AppFont.fontSans.deriveFont(14f));
      this.btnPause.setEnabled(false);

      this.labelTimeElapsed.setFont(AppFont.fontSans.deriveFont(12f));
      this.labelTimeDuration.setFont(AppFont.fontSans.deriveFont(12f));

      this.sliderTimeProgress.setPreferredSize(new Dimension(400, 20));
      this.sliderTimeProgress.setValue(0);
      this.sliderTimeProgress.setEnabled(false);

      JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
      panelButtons.add(this.btnOpen);
      panelButtons.add(Box.createHorizontalStrut(5));
      panelButtons.add(this.btnPlay);
      panelButtons.add(Box.createHorizontalStrut(5));
      panelButtons.add(this.btnPause);
      panelButtons.add(Box.createHorizontalStrut(5));
      panelButtons.add(this.labelFileName);

      JPanel panelSlider = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
      panelSlider.add(this.labelTimeElapsed);
      panelSlider.add(this.sliderTimeProgress);
      panelSlider.add(this.labelTimeDuration);

      JPanel panelPlayer = new JPanel();
      panelPlayer.setLayout(new BoxLayout(panelPlayer, BoxLayout.Y_AXIS));
      panelPlayer.setAlignmentX(0.0f);
      panelPlayer.add(Box.createVerticalStrut(2));
      panelPlayer.add(panelButtons);
      panelPlayer.add(Box.createVerticalStrut(2));
      panelPlayer.add(panelSlider);
      panelPlayer.add(Box.createVerticalStrut(2));

      this.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
      this.add(this.recorder);
      this.add(panelPlayer);
      this.setBorder(BorderFactory.createEmptyBorder());

      this.btnOpen.addActionListener(this);
      this.btnPlay.addActionListener(this);
      this.btnPause.addActionListener(this);
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      Object source = e.getSource();

      if (source instanceof JButton) {
         JButton button = (JButton) source;
         if (button == this.btnOpen) {
            this.openFile();
         } else if (button == this.btnPlay) {
            if (!this.isPlaying) {
               this.playBack();
            } else {
               this.labelTimeElapsed.setText("00:00:00");
               this.stopPlaying();
            }
         } else if (button == this.btnPause) {
            if (!this.isPause) {
               this.pausePlaying();
            } else {
               this.resumePlaying();
            }
         }
      }
   }

   private void openFile() {
      JFileChooser fileChooser = null;

      if (this.lastOpenPath != null && !this.lastOpenPath.equals("")) {
         fileChooser = new JFileChooser(this.lastOpenPath);
      } else {
         fileChooser = new JFileChooser(System.getProperty("user.dir") + "/records");
      }

      FileFilter wavFilter = new FileFilter() {
         @Override
         public boolean accept(File file) {
            if (file.isDirectory()) {
               return true;
            }
            return file.getName().toLowerCase().endsWith(".wav");
         }

         @Override
         public String getDescription() {
            return "Sound file (*.WAV)";
         }
      };


      fileChooser.setFileFilter(wavFilter);
      fileChooser.setDialogTitle("Open Audio File");
      fileChooser.setAcceptAllFileFilterUsed(false);

      int userChoice = fileChooser.showOpenDialog(this);
      if (userChoice == JFileChooser.APPROVE_OPTION) {
         this.audioFilePath = fileChooser.getSelectedFile().getAbsolutePath();
         this.lastOpenPath = fileChooser.getSelectedFile().getParent();
         if (this.isPlaying || this.isPause) {
            this.stopPlaying();
            while (this.player.getAudioClip().isRunning()) {
               try {
                  Thread.sleep(100);
               } catch (InterruptedException ex) {
                  ex.printStackTrace();
               }
            }
         }
         this.playBack();
      }
   }

   private void pausePlaying() {
      this.btnPause.setText("Resume");
      this.isPause = true;
      this.player.pause();
      this.playbackThread.interrupt();
   }

   /**
    * Start playing back the sound.
    */
   private void playBack() {
      this.timer = new PlayingTimer(this.player, this.labelTimeElapsed, this.sliderTimeProgress);
      this.timer.start();
      this.isPlaying = true;
      Image image = null;
      try {
         image = ImageIO.read(new File(this.fpPath, "Stop.gif")).getScaledInstance(12, 12,
               Image.SCALE_AREA_AVERAGING);
      } catch (IOException e) {
         e.printStackTrace();
      }

      this.btnPlay.setText("Stop");
      this.btnPlay.setIcon(new ImageIcon(image));
      this.btnPlay.setEnabled(true);

      this.btnPause.setText("Pause");
      this.btnPause.setEnabled(true);

      this.playbackThread = new Thread(() -> {
         try {
            PlayingPannel.this.player.load(PlayingPannel.this.audioFilePath);
            PlayingPannel.this.labelFileName
                  .setText("Playing File: " + PlayingPannel.this.audioFilePath);
            PlayingPannel.this.sliderTimeProgress
                  .setMaximum((int) PlayingPannel.this.player.getClipSecondLength());

            PlayingPannel.this.labelTimeDuration
                  .setText(PlayingPannel.this.player.getClipLengthString());
            PlayingPannel.this.player.play();

            PlayingPannel.this.resetControls();

         } catch (UnsupportedAudioFileException ex1) {
            JOptionPane.showMessageDialog(PlayingPannel.this, "The audio format is unsupported!",
                  "Error", JOptionPane.ERROR_MESSAGE);
            PlayingPannel.this.resetControls();
            ex1.printStackTrace();
         } catch (LineUnavailableException ex2) {
            JOptionPane.showMessageDialog(PlayingPannel.this,
                  "Could not play the audio file because line is unavailable!", "Error",
                  JOptionPane.ERROR_MESSAGE);
            PlayingPannel.this.resetControls();
            ex2.printStackTrace();
         } catch (IOException ex3) {
            JOptionPane.showMessageDialog(PlayingPannel.this,
                  "I/O error while playing the audio file!", "Error", JOptionPane.ERROR_MESSAGE);
            PlayingPannel.this.resetControls();
            ex3.printStackTrace();
         }

      });

      this.playbackThread.start();
   }

   private void resetControls() {
      this.timer.interrupt();
      Image image = null;
      try {
         image = ImageIO.read(new File(this.fpPath, "Play.gif")).getScaledInstance(12, 12,
               Image.SCALE_AREA_AVERAGING);
      } catch (IOException e) {
         e.printStackTrace();
      }

      this.btnPlay.setText("Play");
      this.btnPlay.setIcon(new ImageIcon(image));

      this.btnPause.setEnabled(false);

      this.isPlaying = false;
   }

   private void resumePlaying() {
      this.btnPause.setText("Pause");
      this.isPause = false;
      this.player.resume();
      this.playbackThread.interrupt();
   }

   private void stopPlaying() {
      this.isPause = false;
      this.btnPause.setText("Pause");
      this.btnPause.setEnabled(false);
      this.timer.interrupt();
      this.player.stop();
      this.playbackThread.interrupt();
   }
}
