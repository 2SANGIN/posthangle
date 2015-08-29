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
      setLayout(new GridBagLayout());

      // setPreferredSize(new Dimension(app.getWindow().getWidth(), 100));

      GridBagConstraints constraints = new GridBagConstraints();
      constraints.insets = new Insets(5, 5, 5, 5);
      constraints.anchor = GridBagConstraints.LINE_START;

      /* set icon */
      try {
         image = ImageIO.read(new File(fpPath, "Open.png")).getScaledInstance(12, 12,
               Image.SCALE_AREA_AVERAGING);
         buttonOpen.setIcon(new ImageIcon(image));

         image = ImageIO.read(new File(fpPath, "Play.gif")).getScaledInstance(12, 12,
               Image.SCALE_AREA_AVERAGING);
         buttonPlay.setIcon(new ImageIcon(image));

         image = ImageIO.read(new File(fpPath, "Pause.png")).getScaledInstance(12, 12,
               Image.SCALE_AREA_AVERAGING);
         buttonPause.setIcon(new ImageIcon(image));
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }


      buttonOpen.setFont(new Font("Sans", Font.BOLD, 14));

      buttonPlay.setFont(new Font("Sans", Font.BOLD, 14));
      buttonPlay.setEnabled(false);

      buttonPause.setFont(new Font("Sans", Font.BOLD, 14));
      buttonPause.setEnabled(false);

      labelTimeCounter.setFont(new Font("Sans", Font.BOLD, 12));
      labelDuration.setFont(new Font("Sans", Font.BOLD, 12));

      sliderTime.setPreferredSize(new Dimension(400, 20));
      sliderTime.setEnabled(false);
      sliderTime.setValue(0);

      JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
      panelButtons.add(buttonOpen);
      panelButtons.add(buttonPlay);
      panelButtons.add(buttonPause);

      constraints.gridwidth = 3;
      constraints.gridx = 3;
      add(panelButtons, constraints);

      constraints.gridx = 0;
      constraints.gridy = 0;
      constraints.gridwidth = 3;
      add(labelFileName, constraints);

      constraints.anchor = GridBagConstraints.CENTER;
      constraints.gridy = 1;
      constraints.gridwidth = 1;
      add(labelTimeCounter, constraints);

      constraints.gridx = 1;
      add(sliderTime, constraints);

      constraints.gridx = 2;
      add(labelDuration, constraints);


      buttonOpen.addActionListener(this);
      buttonPlay.addActionListener(this);
      buttonPause.addActionListener(this);
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      // TODO Auto-generated method stub
      Object source = e.getSource();

      if (source instanceof JButton) {
         JButton button = (JButton) source;
         if (button == buttonOpen) {
            openFile();
         } else if (button == buttonPlay) {
            if (!isPlaying) {
               playBack();
            } else {
               stopPlaying();
            }
         } else if (button == buttonPause) {
            if (!isPause) {
               pausePlaying();
            } else {
               resumePlaying();
            }
         }
      }

   }

   private void openFile() {
      JFileChooser fileChooser = null;

      if (lastOpenPath != null && !lastOpenPath.equals("")) {
         fileChooser = new JFileChooser(lastOpenPath);
      } else {
         fileChooser = new JFileChooser();
      }

      FileFilter wavFilter = new FileFilter() {
         @Override
         public String getDescription() {
            return "Sound file (*.WAV)";
         }

         @Override
         public boolean accept(File file) {
            if (file.isDirectory()) {
               return true;
            } else {
               return file.getName().toLowerCase().endsWith(".wav");
            }
         }
      };


      fileChooser.setFileFilter(wavFilter);
      fileChooser.setDialogTitle("Open Audio File");
      fileChooser.setAcceptAllFileFilterUsed(false);

      int userChoice = fileChooser.showOpenDialog(this);
      if (userChoice == JFileChooser.APPROVE_OPTION) {
         audioFilePath = fileChooser.getSelectedFile().getAbsolutePath();
         lastOpenPath = fileChooser.getSelectedFile().getParent();
         if (isPlaying || isPause) {
            stopPlaying();
            while (player.getAudioClip().isRunning()) {
               try {
                  Thread.sleep(100);
               } catch (InterruptedException ex) {
                  ex.printStackTrace();
               }
            }
         }
         playBack();
      }
   }

   /**
    * Start playing back the sound.
    */
   private void playBack() {
      timer = new PlayingTimer(labelTimeCounter, sliderTime);
      timer.start();
      isPlaying = true;
      try {
         image = ImageIO.read(new File(fpPath, "Stop.gif")).getScaledInstance(12, 12,
               Image.SCALE_AREA_AVERAGING);
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      playbackThread = new Thread(new Runnable() {

         @Override
         public void run() {
            try {

               buttonPlay.setText("Stop");
               buttonPlay.setIcon(new ImageIcon(image));
               buttonPlay.setEnabled(true);

               buttonPause.setText("Pause");
               buttonPause.setEnabled(true);

               player.load(audioFilePath);
               timer.setAudioClip(player.getAudioClip());
               labelFileName.setText("Playing File: " + audioFilePath);
               sliderTime.setMaximum((int) player.getClipSecondLength());

               labelDuration.setText(player.getClipLengthString());
               player.play();

               resetControls();

            } catch (UnsupportedAudioFileException ex) {
               JOptionPane.showMessageDialog(Player.this, "The audio format is unsupported!",
                     "Error", JOptionPane.ERROR_MESSAGE);
               resetControls();
               ex.printStackTrace();
            } catch (LineUnavailableException ex) {
               JOptionPane.showMessageDialog(Player.this,
                     "Could not play the audio file because line is unavailable!", "Error",
                     JOptionPane.ERROR_MESSAGE);
               resetControls();
               ex.printStackTrace();
            } catch (IOException ex) {
               JOptionPane.showMessageDialog(Player.this, "I/O error while playing the audio file!",
                     "Error", JOptionPane.ERROR_MESSAGE);
               resetControls();
               ex.printStackTrace();
            }

         }
      });

      playbackThread.start();
   }

   private void stopPlaying() {
      isPause = false;
      buttonPause.setText("Pause");
      buttonPause.setEnabled(false);
      timer.reset();
      timer.interrupt();
      player.stop();
      playbackThread.interrupt();
   }

   private void pausePlaying() {
      buttonPause.setText("Resume");
      isPause = true;
      player.pause();
      timer.pauseTimer();
      playbackThread.interrupt();
   }

   private void resumePlaying() {
      buttonPause.setText("Pause");
      isPause = false;
      player.resume();
      timer.resumeTimer();
      playbackThread.interrupt();
   }

   private void resetControls() {
      timer.reset();
      timer.interrupt();
      try {
         image = ImageIO.read(new File(fpPath, "Play.png")).getScaledInstance(12, 12,
               Image.SCALE_AREA_AVERAGING);
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      buttonPlay.setText("Play");
      buttonPlay.setIcon(new ImageIcon(image));

      buttonPause.setEnabled(false);

      isPlaying = false;
   }
}
