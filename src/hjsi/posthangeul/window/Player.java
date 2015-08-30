package hjsi.posthangeul.window;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
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


/**
 * Player <br>
 * 2015. 8. 30.
 *
 * @author HYUNJIN
 */
public class Player extends JPanel implements ActionListener {
   @SuppressWarnings("javadoc")
   private static final long serialVersionUID = 8049094210188913162L;

   private AudioPlayer player = new AudioPlayer();
   private Thread playbackThread;

   private PlayingTimer timer;
   private boolean isPlaying = false;

   private boolean isPause = false;
   private String audioFilePath;

   private String lastOpenPath;

   private JLabel labelFileName = new JLabel("Playing File:");
   private JLabel labelTimeElapsed = new JLabel("00:00:00");
   private JLabel labelTimeDuration = new JLabel("00:00:00");

   private JButton buttonOpen = new JButton("Open");
   private JButton buttonPlay = new JButton("Play");
   private JButton buttonPause = new JButton("Pause");

   private JSlider sliderTimeProgress = new JSlider();

   private File fpPath = new File("resources");

   public Player(PostHangeulApp app) {
      this.setLayout(new GridBagLayout());

      GridBagConstraints constraints = new GridBagConstraints();
      constraints.insets = new Insets(5, 5, 5, 5);
      constraints.anchor = GridBagConstraints.LINE_START;

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

      this.buttonOpen.setIcon(iconOpen);
      this.buttonOpen.setFont(AppFont.fontSans.deriveFont(14f));

      this.buttonPlay.setIcon(iconPlay);
      this.buttonPlay.setFont(AppFont.fontSans.deriveFont(14f));
      this.buttonPlay.setEnabled(false);

      this.buttonPause.setIcon(iconPause);
      this.buttonPause.setFont(AppFont.fontSans.deriveFont(14f));
      this.buttonPause.setEnabled(false);

      this.labelTimeElapsed.setFont(AppFont.fontSans.deriveFont(12f));
      this.labelTimeDuration.setFont(AppFont.fontSans.deriveFont(12f));

      this.sliderTimeProgress.setPreferredSize(new Dimension(400, 20));
      this.sliderTimeProgress.setValue(0);
      this.sliderTimeProgress.setEnabled(false);

      JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
      panelButtons.setBackground(Color.cyan);
      panelButtons.add(this.buttonOpen);
      panelButtons.add(this.buttonPlay);
      panelButtons.add(this.buttonPause);

      JPanel panelSlider = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
      panelSlider.setBackground(Color.darkGray);
      panelSlider.add(this.labelTimeElapsed);
      panelSlider.add(this.sliderTimeProgress);
      panelSlider.add(this.labelTimeDuration);

      this.add(panelButtons);
      this.add(panelButtons);
      this.add(this.labelFileName);

      this.buttonOpen.addActionListener(this);
      this.buttonPlay.addActionListener(this);
      this.buttonPause.addActionListener(this);
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      Object source = e.getSource();

      if (source instanceof JButton) {
         JButton button = (JButton) source;
         if (button == this.buttonOpen) {
            this.openFile();
         } else if (button == this.buttonPlay) {
            if (!this.isPlaying) {
               this.playBack();
            } else {
               this.stopPlaying();
            }
         } else if (button == this.buttonPause) {
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
            } else {
               return file.getName().toLowerCase().endsWith(".wav");
            }
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
      this.buttonPause.setText("Resume");
      this.isPause = true;
      this.player.pause();
      this.timer.pauseTimer();
      this.playbackThread.interrupt();
   }

   /**
    * Start playing back the sound.
    */
   private void playBack() {
      this.timer = new PlayingTimer(this.labelTimeElapsed, this.sliderTimeProgress);
      this.timer.start();
      this.isPlaying = true;
      Image image = null;
      try {
         image = ImageIO.read(new File(this.fpPath, "Stop.gif")).getScaledInstance(12, 12,
               Image.SCALE_AREA_AVERAGING);
      } catch (IOException e) {
         e.printStackTrace();
      }

      this.buttonPlay.setText("Stop");
      this.buttonPlay.setIcon(new ImageIcon(image));
      this.buttonPlay.setEnabled(true);

      this.buttonPause.setText("Pause");
      this.buttonPause.setEnabled(true);

      this.playbackThread = new Thread(() -> {
         try {


            Player.this.player.load(Player.this.audioFilePath);
            Player.this.timer.setAudioClip(Player.this.player.getAudioClip());
            Player.this.labelFileName.setText("Playing File: " + Player.this.audioFilePath);
            Player.this.sliderTimeProgress
                  .setMaximum((int) Player.this.player.getClipSecondLength());

            Player.this.labelTimeDuration.setText(Player.this.player.getClipLengthString());
            Player.this.player.play();

            Player.this.resetControls();

         } catch (UnsupportedAudioFileException ex1) {
            JOptionPane.showMessageDialog(Player.this, "The audio format is unsupported!", "Error",
                  JOptionPane.ERROR_MESSAGE);
            Player.this.resetControls();
            ex1.printStackTrace();
         } catch (LineUnavailableException ex2) {
            JOptionPane.showMessageDialog(Player.this,
                  "Could not play the audio file because line is unavailable!", "Error",
                  JOptionPane.ERROR_MESSAGE);
            Player.this.resetControls();
            ex2.printStackTrace();
         } catch (IOException ex3) {
            JOptionPane.showMessageDialog(Player.this, "I/O error while playing the audio file!",
                  "Error", JOptionPane.ERROR_MESSAGE);
            Player.this.resetControls();
            ex3.printStackTrace();
         }

      });

      this.playbackThread.start();
   }

   private void resetControls() {
      this.timer.reset();
      this.timer.interrupt();
      Image image = null;
      try {
         image = ImageIO.read(new File(this.fpPath, "Play.gif")).getScaledInstance(12, 12,
               Image.SCALE_AREA_AVERAGING);
      } catch (IOException e) {
         e.printStackTrace();
      }

      this.buttonPlay.setText("Play");
      this.buttonPlay.setIcon(new ImageIcon(image));

      this.buttonPause.setEnabled(false);

      this.isPlaying = false;
   }

   private void resumePlaying() {
      this.buttonPause.setText("Pause");
      this.isPause = false;
      this.player.resume();
      this.timer.resumeTimer();
      this.playbackThread.interrupt();
   }

   private void stopPlaying() {
      this.isPause = false;
      this.buttonPause.setText("Pause");
      this.buttonPause.setEnabled(false);
      this.timer.reset();
      this.timer.interrupt();
      this.player.stop();
      this.playbackThread.interrupt();
   }
}
