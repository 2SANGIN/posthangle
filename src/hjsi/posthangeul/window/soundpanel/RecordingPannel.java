package hjsi.posthangeul.window.soundpanel;

import java.awt.FlowLayout;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import hjsi.posthangeul.recorder.SoundRecordingUtil;

/**
 * Recorder <br>
 * 2015. 8. 29.
 *
 * @author HYUNJIN
 */
public class RecordingPannel extends JPanel {
   @SuppressWarnings("javadoc")
   private static final long serialVersionUID = 1975693916367457094L;

   /**
    * 시간표시용 포맷
    */
   private static final String TIME_FORMAT = "%02d:%02d:%02d";

   /**
    * @param imgDir 버튼 이미지가 들어있는 경로
    * @param buttonName 버튼 이미지의 이름 (접미사 및 확장자 제외)
    * @param btnSize 버튼 이미지 크기
    * @return 이미지 버튼 객체
    * @throws IOException if an error occurs during reading.
    */
   public static JToggleButton createImageToggleButton(File imgDir, String buttonName, int btnSize)
         throws IOException {
      String[] postfix = {"", "_rollover", "_selected", "_rollover_selected"};
      String extension = ".png";

      JToggleButton imgButton = new JToggleButton();
      imgButton.setName(buttonName);
      imgButton.setContentAreaFilled(false);
      imgButton.setBorder(BorderFactory.createEmptyBorder());

      Image[] images = new Image[postfix.length];

      for (int i = 0; i < postfix.length; i++) {
         File imgPath = new File(imgDir, buttonName + postfix[i] + extension);
         if (imgPath.exists()) {
            images[i] = ImageIO.read(imgPath).getScaledInstance(btnSize, btnSize,
                  Image.SCALE_AREA_AVERAGING);
         } else {
            System.out.println(imgPath.toString() + " was not found!");
         }
      }

      if (images[0] != null)
         imgButton.setIcon(new ImageIcon(images[0]));
      if (images[1] != null)
         imgButton.setRolloverIcon(new ImageIcon(images[1]));
      if (images[2] != null)
         imgButton.setSelectedIcon(new ImageIcon(images[2]));
      if (images[3] != null)
         imgButton.setRolloverSelectedIcon(new ImageIcon(images[3]));

      return imgButton;
   }

   /**
    * 녹음 시작, 중지 버튼
    */
   private JToggleButton btnRecord;

   /**
    * 녹음 일시정지, 재개 버튼
    */
   private JButton btnPause;

   /**
    * 녹음 시간 표시창
    */
   private JLabel labelRecordTimer;

   /**
    * TODO 녹음 파일 용량 표시창
    */
   private JLabel labelRecordSize;

   /**
    * 현재 녹음 중인가에 대한 상태를 가진 변수
    */
   private boolean isRecording = false;

   /**
    * 실제 녹음을 진행하는 객체
    */
   private SoundRecordingUtil recorder;

   private ImageIcon iconPlay;
   private ImageIcon iconPlayRollover;
   private ImageIcon iconRecord;
   private ImageIcon iconRecordRollover;

   /**
    * 녹음 제어 패널을 생성한다.
    *
    * @param btnSize 버튼 이미지 크기
    */
   public RecordingPannel(int btnSize) {
      this.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));

      File resPath = new File("resources/button");
      Image image = null;
      /* 레코드 버튼 */
      try {
         this.btnRecord = createImageToggleButton(resPath, "record", btnSize);
         image = ImageIO.read(new File(resPath, "record.png")).getScaledInstance(btnSize, btnSize,
               Image.SCALE_AREA_AVERAGING);
         this.iconRecord = new ImageIcon(image);
         image = ImageIO.read(new File(resPath, "record_rollover.png")).getScaledInstance(btnSize,
               btnSize, Image.SCALE_AREA_AVERAGING);
         this.iconRecordRollover = new ImageIcon(image);
         image = ImageIO.read(new File(resPath, "play.png")).getScaledInstance(btnSize, btnSize,
               Image.SCALE_AREA_AVERAGING);
         this.iconPlay = new ImageIcon(image);
         image = ImageIO.read(new File(resPath, "play_rollover.png")).getScaledInstance(btnSize,
               btnSize, Image.SCALE_AREA_AVERAGING);
         this.iconPlayRollover = new ImageIcon(image);
      } catch (IOException e1) {
         e1.printStackTrace();
      }

      /* pause 버튼 */
      this.btnPause = new JButton();
      this.btnPause.setEnabled(false);
      this.btnPause.setContentAreaFilled(false);
      this.btnPause.setBorder(BorderFactory.createEmptyBorder());
      try {

         /* pause 버튼 */
         image = ImageIO.read(new File(resPath, "pause.png")).getScaledInstance(btnSize, btnSize,
               Image.SCALE_AREA_AVERAGING);
         this.btnPause.setIcon(new ImageIcon(image));
         image = ImageIO.read(new File(resPath, "pause_rollover.png")).getScaledInstance(btnSize,
               btnSize, Image.SCALE_AREA_AVERAGING);
         this.btnPause.setRolloverIcon(new ImageIcon(image));
         image = ImageIO.read(new File(resPath, "pause_disabled.png")).getScaledInstance(btnSize,
               btnSize, Image.SCALE_AREA_AVERAGING);
         this.btnPause.setDisabledIcon(new ImageIcon(image));
      } catch (IOException e2) {
         e2.printStackTrace();
      }

      this.labelRecordTimer = new JLabel("00:00:00");

      this.add(this.btnRecord);
      this.add(this.btnPause);
      this.add(this.labelRecordTimer);

      this.recorder = new SoundRecordingUtil(this.labelRecordTimer);

      /* 녹음 시작/정지 버튼의 리스너를 등록한다 */
      this.btnRecord.addActionListener(e -> {
         /* 녹음 중이 아니거나 일시정지 중일 때 */
         if (this.btnRecord.isSelected()) {
            /* 일시정지 상태 */
            if (this.recorder.isPaused()) {
               this.btnPause.setEnabled(true);
               this.btnRecord.setIcon(this.iconRecord);
               this.btnRecord.setRolloverIcon(this.iconRecordRollover);
               this.recorder.resumeRecording();
               System.out.println("RECORDING RESUMED!");
            }

            /* 녹음 시작하기 */
            else {
               this.btnPause.setEnabled(true);
               this.recorder.startRecording();
               System.out.println("RECORDING STARTED!");
            }
         }
         /* 녹음 중지 */
         else if (!this.btnRecord.isSelected()) {
            this.btnPause.setEnabled(false);
            byte[] soundBinaries = this.recorder.stopRecording(); // 자동으로 저장한다
            this.recorder.save(RecordingPannel.this.getNowTime(), soundBinaries);
            System.out.println("RECORDING STOPPED!");
         }
      });

      /* 녹음 일시중지/재개 버튼의 리스너를 등록한다. */
      this.btnPause.addActionListener(e -> {
         /* 녹음 일시중지 */
         this.btnPause.setEnabled(false);
         this.btnRecord.setSelected(false);
         this.btnRecord.setIcon(this.iconPlay);
         this.btnRecord.setRolloverIcon(this.iconPlayRollover);
         this.recorder.pauseRecording();
         System.out.println("RECORDING PAUSED!");
      });
   }

   public String getNowTime() {
      DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd-HHmmss");
      Date date = new Date();
      return dateFormat.format(date);
   }
}
