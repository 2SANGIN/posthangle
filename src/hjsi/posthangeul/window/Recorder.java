package hjsi.posthangeul.window;

import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JLabel;
import javax.swing.JPanel;

import hjsi.posthangeul.recorder.SoundRecordingUtil;

// 녹음기 개발
public class Recorder extends JPanel {
   JPanel recordMenu;
   JButton btnRecord;
   JLabel msg;
   Thread rc;

   private boolean isRecording = false;
   private boolean isPlaying = false;

   private int sec = 0;
   private int min = 0;
   private int mil = 0;

   public Recorder(PostHangeulApp app, int btnSize) {

      setLayout(new FlowLayout(FlowLayout.LEFT));

      recordMenu = new JPanel();
      btnRecord = new JButton("record");
      msg = new JLabel("Recording is ready");

      recordMenu.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
      btnRecord.setSize(btnSize, btnSize);
      msg.setPreferredSize(new Dimension(200, btnSize));
      msg.setBackground(getBackground());
      msg.setBorder(javax.swing.BorderFactory.createEmptyBorder());

      recordMenu.add(btnRecord);
      recordMenu.add(msg);

      add(recordMenu);

      final SoundRecordingUtil recorder = new SoundRecordingUtil();

      btnRecord.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub

            if (!isRecording) {
               isRecording = true;
               btnRecord.setText("stop");
               Thread recordThread = new Thread(new Runnable() {
                  @Override
                  public void run() {
                     try {
                        System.out.println("Start recording...");
                        Thread timeStamp = new Thread(new Runnable() {
                           @Override
                           public void run() {
                              // TODO Auto-generated method stub
                              init();
                              count();
                           }
                        });
                        rc = timeStamp;
                        timeStamp.start();
                        recorder.start();
                     } catch (LineUnavailableException ex) {
                        ex.printStackTrace();
                        System.exit(-1);
                     }
                  }
               });
               recordThread.start();
            }

            else if (isRecording) {
               isRecording = false;
               btnRecord.setText("Record");
               // TODO Auto-generated method stub
               try {
                  File wavFile = new File("records/" + getName() + ".wav");
                  recorder.stop();
                  rc.stop();

                  recorder.save(wavFile);
                  msg.setText("File saved successfully!");
                  msg.setForeground(Color.BLACK);
               } catch (IOException e1) {
                  // TODO Auto-generated catch block
                  e1.printStackTrace();
               }
               System.out.println("STOPPED");

            }
         }
      });

      System.out.println("DONE");
   }

   public String getName() {
      DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
      Date date = new Date();
      return dateFormat.format(date);
   }

   private void count() {
      long now = System.currentTimeMillis();
      while (true) {
         if (System.currentTimeMillis() - now >= 100) {
            now = System.currentTimeMillis();
            String strSec = Integer.toString(sec);
            String strMin = Integer.toString(min);
            String strMil = Integer.toString(mil);
            update(strSec, sec, strMin, strMil, mil);
            mil++;
            if (mil > 9) {
               mil = 0;
               sec++;
               if (sec >= 60) {
                  sec = 1;
                  min++;
               }
            }
         }
      }
   }

   private void update(String sec, int s, String min, String mil, int m) {
      if (s <= 10) {
         sec = "0" + sec;
      }
      msg.setText("Recording... " + min + ":" + sec + ":" + mil);
      msg.setForeground(Color.RED);

   }

   private void init() {
      sec = 0;
      min = 0;
      mil = 0;
   }
}
