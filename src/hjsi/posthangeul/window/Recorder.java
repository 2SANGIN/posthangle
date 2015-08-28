package hjsi.posthangeul.window;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
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
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.Border;

import recorder.SoundRecordingUtil;

// 녹음기 개발
public class Recorder extends JPanel {
   JToolBar toolbar;
   JButton start;
   JButton stop;
   JTextField msg;
   
   private int sec = 0;
   private int min = 0;
   private int mil = 0;

   public Recorder(PostHangeulApp app, int btnSize) {

      setLayout(new FlowLayout(FlowLayout.LEFT));

      toolbar = new JToolBar();
      toolbar.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
      start = new JButton("start");
      stop = new JButton("stop");
      msg = new JTextField("Recording is ready");
      
      start.setSize(btnSize, btnSize);
      stop.setSize(btnSize, btnSize);
      msg.setPreferredSize(new Dimension(200, btnSize));
      msg.setEditable(false);
      msg.setBackground(getBackground());
      msg.setBorder(null);

      toolbar.add(start);
      toolbar.add(stop);
      toolbar.add(msg);
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
   
   private void count(){
          long now = System.currentTimeMillis();
          while(true){
              if(System.currentTimeMillis()-now>=100){
                  now=System.currentTimeMillis();
                  String strSec = Integer.toString(sec);
                  String strMin = Integer.toString(min);
                  String strMil = Integer.toString(mil);
                  update(strSec,sec,strMin,strMil,mil);
                  mil++;
                  if(mil>9){
                      mil=0;
                      sec++;
                      if(sec>=60){
                          sec=1;
                          min++;
                      }
                  }
          }
      }
  }
   
   private void update(String sec,int s, String min,String mil,int m){
      if (s<=10){
          sec="0"+sec;
      }
      System.out.println(min+":"+sec+","+mil);
      msg.setText(min+":"+sec+","+mil);

  }
}
