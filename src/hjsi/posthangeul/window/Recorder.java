package hjsi.posthangeul.window;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import recorder.SoundRecordingUtil;

// 녹음기 개발
public class Recorder extends JPanel {
   JToolBar toolbar;
   JButton start;
   JButton stop;

   public Recorder(PostHangeulApp app, int btnSize) {

      setLayout(new FlowLayout(FlowLayout.LEFT));

      toolbar = new JToolBar();
      toolbar.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
      start = new JButton("start");
      start.setSize(btnSize, btnSize);
      stop = new JButton("stop");
      stop.setSize(btnSize, btnSize);

      toolbar.add(start);
      toolbar.add(stop);
      toolbar.setFloatable(false);

      add(toolbar);

      File wavFile = new File("records/" + getName() + ".wav");


      final SoundRecordingUtil recorder = new SoundRecordingUtil();

      Thread recordThread = new Thread(new Runnable() {
         @Override
         public void run() {
            try {
               System.out.println("Start recording...");
               recorder.start();
            } catch (LineUnavailableException ex) {
               ex.printStackTrace();
               System.exit(-1);
            }
         }
      });
      start.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            recordThread.start();
         }
      });

      stop.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            try {
               recorder.stop();
               recorder.save(wavFile);
            } catch (IOException e1) {
               // TODO Auto-generated catch block
               e1.printStackTrace();
            }
            System.out.println("STOPPED");
         }
      });

      System.out.println("DONE");
   }

   public String getName() {
      DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmm");
      Date date = new Date();
      return dateFormat.format(date);
   }
}
