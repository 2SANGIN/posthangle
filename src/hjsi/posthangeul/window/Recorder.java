package hjsi.posthangeul.window;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import hjsi.posthangeul.recorder.SoundRecordingUtil;

/**
 * Recorder <br>
 * 2015. 8. 29.
 *
 * @author HYUNJIN
 */
public class Recorder extends JPanel {
   @SuppressWarnings("javadoc")
   private static final long serialVersionUID = 1975693916367457094L;
   private JPanel recordMenu;
   private JButton btnRecord;
   private JLabel msg;
   private boolean isRecording = false;
   private boolean isPlaying = false;

   private SoundRecordingUtil recorder;

   /**
    * 녹음 제어 패널을 생성한다.
    *
    * @param btnSize
    */
   public Recorder(int btnSize) {
      this.setLayout(new FlowLayout(FlowLayout.LEFT));

      Image image = null;
      File fpPath = new File("resources");

      /* set image to each buttons */
      this.recordMenu = new JPanel();
      this.btnRecord = new JButton("Record");
      this.btnRecord.setFont(new Font("Sans", Font.BOLD, 14));
      this.msg = new JLabel("Recording is ready");

      this.recordMenu.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

      try {
         image = ImageIO.read(new File(fpPath, "Record.png")).getScaledInstance(btnSize, btnSize,
               Image.SCALE_AREA_AVERAGING);
         this.btnRecord.setIcon(new ImageIcon(image));
      } catch (IOException e2) {
         e2.printStackTrace();
      }

      this.msg.setPreferredSize(new Dimension(200, btnSize));
      this.msg.setBackground(this.getBackground());
      this.msg.setBorder(javax.swing.BorderFactory.createEmptyBorder());

      this.recordMenu.add(this.btnRecord);
      this.recordMenu.add(this.msg);

      this.add(this.recordMenu);

      this.recorder = new SoundRecordingUtil(this.msg);

      /* 녹음 시작/정지 버튼의 리스너를 등록한다 */
      this.btnRecord.addActionListener(e -> {
         /* 녹음 시작 */
         if (!Recorder.this.isRecording) {
            Recorder.this.isRecording = true;
            Recorder.this.btnRecord.setText("stop");

            System.out.println("Start recording...");
            this.recorder.startRecording();
         }

         /* 녹음 중지 */
         else if (Recorder.this.isRecording) {
            Recorder.this.isRecording = false;
            Recorder.this.btnRecord.setText("Record");

            try {
               byte[] soundBinaries = this.recorder.stopRecording();
               this.recorder.save(Recorder.this.getNowTime(), soundBinaries);
            } catch (IOException e1) {
               e1.printStackTrace();
            }
            Recorder.this.msg.setText("File saved successfully!");
            Recorder.this.msg.setForeground(Color.BLACK);
            System.out.println("STOPPED");

         }
      });

      System.out.println("DONE");
   }

   public String getNowTime() {
      DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd-HHmmss");
      Date date = new Date();
      return dateFormat.format(date);
   }
}
