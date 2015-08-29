package hjsi.posthangeul.window;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import hjsi.posthangeul.player.AudioPlayer;
import hjsi.posthangeul.player.PlayingTimer;

public class Player extends JPanel implements ActionListener {
   JPanel playerMenu;

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

   public Player(PostHangeulApp app) {
      setLayout(new FlowLayout(FlowLayout.LEFT));

      GridBagConstraints constraints = new GridBagConstraints();
      constraints.insets = new Insets(5, 5, 5, 5);
      constraints.anchor = GridBagConstraints.WEST;

      playerMenu = new JPanel();

      playerMenu.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

      /* set icon */

      Image image = null;
      File fpPath = new File("resources");

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

      constraints.gridx = 0;
      constraints.gridy = 0;
      constraints.gridwidth = 3;
      playerMenu.add(labelFileName, constraints);

      constraints.gridy = 1;
      constraints.gridwidth = 1;
      playerMenu.add(labelTimeCounter, constraints);

      constraints.gridx = 1;
      playerMenu.add(sliderTime, constraints);

      constraints.gridx = 2;
      playerMenu.add(labelDuration, constraints);

      JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
      panelButtons.add(buttonOpen);
      panelButtons.add(buttonPlay);
      panelButtons.add(buttonPause);

      playerMenu.add(panelButtons, constraints);

      add(playerMenu);

   }

   @Override
   public void actionPerformed(ActionEvent e) {
      // TODO Auto-generated method stub

   }
}
