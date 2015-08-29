package hjsi.posthangeul.window;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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

import hjsi.posthangeul.player.AudioPlayer;
import hjsi.posthangeul.player.PlayingTimer;


public class Player extends JPanel implements ActionListener {
   private AudioPlayer player = new AudioPlayer();
   private Thread playbackThread;
   private PlayingTimer timer;

   private boolean isPlaying = false;
   private boolean isPause = false;

   private String audioFilePath;
   private String lastOpenPath;

   private JLabel labelFileName = new JLabel("Playing File:");
   private JLabel labelTimeCounter = new JLabel("00:00:00");
   private JLabel labelDuration = new JLabel("00:00:00");

   private JButton buttonOpen = new JButton("Open");
   private JButton buttonPlay = new JButton("Play");
   private JButton buttonPause = new JButton("Pause");

   private JSlider sliderTime = new JSlider();

   private Image image = null;
   private File fpPath = new File("resources");

   public Player(PostHangeulApp app) {
      this.setLayout(new GridBagLayout());

      // setPreferredSize(new Dimension(app.getWindow().getWidth(), 100));

      GridBagConstraints constraints = new GridBagConstraints();
      constraints.insets = new Insets(5, 5, 5, 5);
      constraints.anchor = GridBagConstraints.LINE_START;

      /* set icon */
      try {
         this.image = ImageIO.read(new File(this.fpPath, "Open.png")).getScaledInstance(12, 12,
               Image.SCALE_AREA_AVERAGING);
         this.buttonOpen.setIcon(new ImageIcon(this.image));

         this.image = ImageIO.read(new File(this.fpPath, "Play.gif")).getScaledInstance(12, 12,
               Image.SCALE_AREA_AVERAGING);
         this.buttonPlay.setIcon(new ImageIcon(this.image));

         this.image = ImageIO.read(new File(this.fpPath, "Pause.png")).getScaledInstance(12, 12,
               Image.SCALE_AREA_AVERAGING);
         this.buttonPause.setIcon(new ImageIcon(this.image));
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }


      this.buttonOpen.setFont(new Font("Sans", Font.BOLD, 14));

      this.buttonPlay.setFont(new Font("Sans", Font.BOLD, 14));
      this.buttonPlay.setEnabled(false);

      this.buttonPause.setFont(new Font("Sans", Font.BOLD, 14));
      this.buttonPause.setEnabled(false);

      this.labelTimeCounter.setFont(new Font("Sans", Font.BOLD, 12));
      this.labelDuration.setFont(new Font("Sans", Font.BOLD, 12));

      this.sliderTime.setPreferredSize(new Dimension(400, 20));
      this.sliderTime.setEnabled(false);
      this.sliderTime.setValue(0);

      JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
      panelButtons.add(this.buttonOpen);
      panelButtons.add(this.buttonPlay);
      panelButtons.add(this.buttonPause);

      constraints.gridwidth = 3;
      constraints.gridx = 3;
      this.add(panelButtons, constraints);

      constraints.gridx = 0;
      constraints.gridy = 0;
      constraints.gridwidth = 3;
      this.add(this.labelFileName, constraints);

      constraints.anchor = GridBagConstraints.CENTER;
      constraints.gridy = 1;
      constraints.gridwidth = 1;
      this.add(this.labelTimeCounter, constraints);

      constraints.gridx = 1;
      this.add(this.sliderTime, constraints);

      constraints.gridx = 2;
      this.add(this.labelDuration, constraints);


      this.buttonOpen.addActionListener(this);
      this.buttonPlay.addActionListener(this);
      this.buttonPause.addActionListener(this);
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      // TODO Auto-generated method stub
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
         fileChooser = new JFileChooser();
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
      this.timer = new PlayingTimer(this.labelTimeCounter, this.sliderTime);
      this.timer.start();
      this.isPlaying = true;
      try {
         this.image = ImageIO.read(new File(this.fpPath, "Stop.gif")).getScaledInstance(12, 12,
               Image.SCALE_AREA_AVERAGING);
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      this.playbackThread = new Thread(() -> {
         try {

            Player.this.buttonPlay.setText("Stop");
            Player.this.buttonPlay.setIcon(new ImageIcon(Player.this.image));
            Player.this.buttonPlay.setEnabled(true);

            Player.this.buttonPause.setText("Pause");
            Player.this.buttonPause.setEnabled(true);

            Player.this.player.load(Player.this.audioFilePath);
            Player.this.timer.setAudioClip(Player.this.player.getAudioClip());
            Player.this.labelFileName.setText("Playing File: " + Player.this.audioFilePath);
            Player.this.sliderTime.setMaximum((int) Player.this.player.getClipSecondLength());

            Player.this.labelDuration.setText(Player.this.player.getClipLengthString());
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
      try {
         this.image = ImageIO.read(new File(this.fpPath, "Play.gif")).getScaledInstance(12, 12,
               Image.SCALE_AREA_AVERAGING);
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      this.buttonPlay.setText("Play");
      this.buttonPlay.setIcon(new ImageIcon(this.image));

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
