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
import javax.swing.border.EmptyBorder;

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

   /**
    * 녹음 시작, 중지 버튼
    */
   private JButton btnRecord;
   /**
    * 녹음 일시정지, 재개 버튼
    */
   private JButton btnResume;

   private JLabel msg;
   private boolean isRecording = false;
   private boolean isPlaying = false;

   /**
    * 실제 녹음을 진행하는 객체
    */
   private SoundRecordingUtil recorder;

   /**
    * 녹음 제어 패널을 생성한다.
    *
    * @param btnSize
    */
   public Recorder(int btnSize) {
      this.setBackground(Color.LIGHT_GRAY);
      this.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
      this.setBorder(new EmptyBorder(5, 0, 5, 0));

      /* set default to buttons & label */
      Dimension preferredSize = new Dimension(120, 30);
      this.btnRecord = new JButton("Record");
      this.btnRecord.setFont(new Font("Sans", Font.BOLD, 14));
      this.btnRecord.setPreferredSize(preferredSize);
      this.btnResume = new JButton("pause");
      this.btnResume.setFont(new Font("Sans", Font.BOLD, 14));
      this.btnResume.setPreferredSize(preferredSize);
      this.btnResume.setEnabled(false);
      this.msg = new JLabel("Recording is ready");

      /* set image to each buttons */
      Image image = null;
      File fpPath = new File("resources");
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

      this.add(this.btnRecord);
      this.add(this.btnResume);
      this.add(this.msg);

      this.recorder = new SoundRecordingUtil(this.msg);

      /* 녹음 시작/정지 버튼의 리스너를 등록한다 */
      this.btnRecord.addActionListener(e -> {
         /* 녹음 시작 */
         if (!Recorder.this.isRecording) {
            Recorder.this.isRecording = true;
            Recorder.this.btnRecord.setText("stop");
            Recorder.this.btnResume.setEnabled(true);
            this.recorder.startRecording();
            System.out.println("RECORDING STARTED!");
         }

         /* 녹음 중지 */
         else if (Recorder.this.isRecording) {
            /* 재개 버튼 원상태로 돌리기 */
            this.btnResume.setText("pause");
            this.recorder.resumeRecording();

            Recorder.this.isRecording = false;
            Recorder.this.btnRecord.setText("Record");
            Recorder.this.btnResume.setEnabled(false);
            byte[] soundBinaries = this.recorder.stopRecording(); // 자동으로 저장한다
            this.recorder.save(Recorder.this.getNowTime(), soundBinaries);
            System.out.println("RECORDING STOPPED!");
         }
      });

      /* 녹음 일시중지/재개 버튼의 리스너를 등록한다. */
      this.btnResume.addActionListener(e -> {
         /* 녹음 일시중지 */
         if (!this.recorder.isPaused()) {
            this.btnResume.setText("resume");
            this.recorder.pauseRecording();
         }

         /* 녹음 재개 */
         else if (this.recorder.isPaused()) {
            this.btnResume.setText("pause");
            this.recorder.resumeRecording();
         }
      });
   }

   public String getNowTime() {
      DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd-HHmmss");
      Date date = new Date();
      return dateFormat.format(date);
   }
}
