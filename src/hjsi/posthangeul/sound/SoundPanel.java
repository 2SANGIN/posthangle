package hjsi.posthangeul.sound;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.MouseInputAdapter;
import javax.swing.filechooser.FileFilter;

import hjsi.posthangeul.AppFont;
import hjsi.posthangeul.IconLoader;

/**
 * SoundPanel <br>
 * 2015. 8. 29.
 *
 * @author HYUNJIN
 */
public class SoundPanel {
   /**
    * 1시간 >> 360초
    */
   private static final int SECONDS_IN_HOUR = 360;
   /**
    * 1분 >> 60초
    */
   private static final int SECONDS_IN_MINUTE = 60;
   /**
    * 버튼 이미지 접미사
    */
   public final static String[] POSTFIX = {"_disabled", "_pressed", "_rollover"};

   /**
    * wav 파일 필터
    */
   private static final FileFilter FILE_WAV_FILTER = new FileFilter() {
      @Override
      public boolean accept(File file) {
         if (file.isDirectory())
            return true;
         return file.getName().toLowerCase().endsWith(".wav");
      }

      @Override
      public String getDescription() {
         return "sound file (*.wav)";
      }
   };

   /**
    * 현재 날짜와 시간을 문자열로 반환한다.
    *
    * @return yy-MM-dd-HHmmss 형식의 시간 문자열
    */
   public static String getNowTime() {
      DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd-HHmmss");
      Date date = new Date();
      return dateFormat.format(date);
   }

   /**
    * 주어진 초를 HH:mm:ss로 변환한다.
    *
    * @param elapsedSec 변환할 시간 초.
    * @return HH:mm:ss 형식의 문자열
    */
   @SuppressWarnings("boxing")
   public static String toTimeString(int elapsedSec) {
      int sec = elapsedSec;
      int hour = sec / SECONDS_IN_HOUR;
      sec %= SECONDS_IN_HOUR;
      int min = sec / SECONDS_IN_MINUTE;
      sec %= SECONDS_IN_MINUTE;
      return String.format("%02d:%02d:%02d", hour, min, sec);
   }

   /**
    * TODO 나중에...
    *
    * @param container
    */
   private static void fadeOutRecursive(Container container) {
      for (Component comp : container.getComponents()) {

         Color bgColor = container.getBackground();

         for (int alpha = bgColor.getAlpha(); alpha >= 0; alpha--) {
            container.setBackground(
                  new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), alpha));
         }
      }
   }

   /**
    * 버튼의 아이콘을 종류별로 세팅한다. 추가적으로 테두리를 없애고, 버튼 영역을 채워그리지 않게 한다.
    *
    * <pre>
    * 0: 기본 아이콘
    * 1: disabled 아이콘
    * 2: pressed 아이콘
    * 3: rollover 아이콘
    * </pre>
    *
    * @param button 아이콘을 세팅할 버튼
    * @param icons 아이콘 배열
    */
   private static void setButtonIcons(AbstractButton button, Icon[] icons) {
      button.setContentAreaFilled(false);
      button.setBorder(BorderFactory.createEmptyBorder());
      button.setIcon(icons[0]);
      button.setDisabledIcon(icons[1]);
      button.setPressedIcon(icons[2]);
      button.setRolloverIcon(icons[3]);
   }

   /**
    * 현재 열려있는 녹음 파일
    */
   File fpAudio;

   /**
    * TODO 녹음 파일 용량 표시 예정
    */
   private JLabel labelRecordSize;


   /**
    * 실제 녹음을 진행하는 객체
    */
   private SoundRecordingUtil audioRecorder;

   /**
    * 녹음 파일을 재생하는 객체
    */
   protected AudioPlayer audioPlayer = new AudioPlayer();

   /**
    * 녹음 파일 열기 아이콘들
    */
   Icon[] iconsOpen;

   /**
    * 일시정지 아이콘들
    */
   Icon[] iconsPause;

   /**
    * 재생 아이콘들
    */
   Icon[] iconsPlay;

   /**
    * 녹음 아이콘들
    */
   Icon[] iconsRecord;

   /**
    * 정지 아이콘들
    */
   Icon[] iconsStop;

   /**
    * 주기적으로 정보를 갱신하는 스레드
    */
   ScheduledThreadPoolExecutor uiRefresher;

   /**
    * 사운드 패널의 부모 컨테이너
    */
   Container parent;

   /**
    * 현재 보여지는 패널
    */
   JPanel currentShowingPanel;

   /**
    * 슬라이더가 눌려있는지 여부를 나타냄
    */
   private boolean isSliderPressed = false;

   /**
    * 녹음 제어 패널을 생성한다.
    *
    * @param parent 사운드 패널의 부모 컨테이너
    * @param btnSize 버튼 이미지 크기
    */
   public SoundPanel(Container parent, int btnSize) {
      this.parent = parent;

      /*
       * 주기적으로 UI를 갱신할 스레드를 만들어둔다. 각 패널은 자신을 생성할 때 해당 스레드에 갱신이 필요한 객체를 전달한다.
       */
      this.uiRefresher = new ScheduledThreadPoolExecutor(1);

      /* 일단 프로그램을 빨리 띄우기 위해 이미지 로딩은 쓰레드로 돌린다. 이 이미지를 쓰는 녹음기는 로딩이 끝나면 추가된다. */
      new Thread(() -> {
         this.audioRecorder = new SoundRecordingUtil();
         try {
            /* 모든 버튼 이미지 준비 */
            File resPath = new File("resources/button");
            this.iconsRecord = IconLoader.loadIcons(resPath, "record", SoundPanel.POSTFIX, btnSize);
            this.iconsPause = IconLoader.loadIcons(resPath, "pause", SoundPanel.POSTFIX, btnSize);
            this.iconsOpen = IconLoader.loadIcons(resPath, "open", SoundPanel.POSTFIX, btnSize);
            this.iconsPlay = IconLoader.loadIcons(resPath, "play", SoundPanel.POSTFIX, btnSize);
            this.iconsStop = IconLoader.loadIcons(resPath, "stop", SoundPanel.POSTFIX, btnSize);
         } catch (Exception e) {
            e.printStackTrace();
         }
         this.currentShowingPanel = this.createRecordingPanel();
         this.parent.add(this.currentShowingPanel, 0);
         this.parent.revalidate();
      }).start();
   }

   /**
    * @return the isSliderPressed
    */
   public boolean isSliderPressed() {
      return this.isSliderPressed;
   }

   /**
    * @param isSliderPressed the isSliderPressed to set
    */
   public void setSliderPressed(boolean isSliderPressed) {
      this.isSliderPressed = isSliderPressed;
   }

   /**
    * 재생 패널을 생성해서 반환한다.
    *
    * @return 플레잉 패널, not null
    */
   private JPanel createPlayingPanel() {
      if (this.uiRefresher.getTaskCount() > 0)
         this.uiRefresher.purge();

      /*************************************
       * 버튼 패널에 들어갈 요소들
       *************************************/
      JButton btnOpen, btnPlayPause, btnStop;
      // 열기 버튼
      btnOpen = new JButton();
      setButtonIcons(btnOpen, this.iconsOpen);

      // 재생/일시정지 버튼
      btnPlayPause = new JButton();
      setButtonIcons(btnPlayPause, this.iconsPlay);

      // 정지/녹음 시작 버튼
      btnStop = new JButton();
      setButtonIcons(btnStop, this.iconsRecord);

      // 열기 버튼의 리스너를 등록한다.
      btnOpen.addActionListener(e -> {
         this.openSoundFile();
      });

      // 재생/일시정지 버튼의 리스너를 등록한다.
      btnPlayPause.addActionListener(e -> {
         if (this.audioPlayer.isPlaying() && !this.audioPlayer.isPaused()) {
            this.audioPlayer.pause();
         } else {
            if (this.audioPlayer.isPaused())
               this.audioPlayer.resume();
            else
               this.audioPlayer.play();
         }
      });

      // 녹음/정지 버튼의 리스너를 등록한다.
      btnStop.addActionListener(e -> {
         if (!this.audioPlayer.isPlaying()) {
            this.audioPlayer.close();
            new Thread(() -> {
               this.parent.remove(this.currentShowingPanel);
               this.currentShowingPanel = this.createRecordingPanel();
               this.parent.add(this.currentShowingPanel, 0);
               this.parent.revalidate();
            }).start();
         } else {
            this.audioPlayer.stop();
            SoundPanel.setButtonIcons(btnStop, this.iconsRecord);
         }
      });

      // LineListener를 통해서 오디오플레이어 상태에 따라 버튼 모양 갱신
      this.audioPlayer.getAudioClip().addLineListener(event -> {
         LineEvent.Type type = event.getType();
         if (type == LineEvent.Type.STOP) {
            SoundPanel.setButtonIcons(btnPlayPause, this.iconsPlay);
         } else if (type == LineEvent.Type.START) {
            SoundPanel.setButtonIcons(btnPlayPause, this.iconsPause);
            SoundPanel.setButtonIcons(btnStop, this.iconsStop);
         }
      });

      // 버튼 패널 자체 설정
      JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
      panelButtons.add(btnOpen);
      panelButtons.add(btnPlayPause);
      panelButtons.add(btnStop);


      /****************************************
       * 라벨 패널에 들어갈 요소들
       ****************************************/
      JLabel labelTitle, labelTimeElapsed, labelTimeLength;
      // 파일 제목 라벨
      labelTitle = new JLabel(this.fpAudio.getName());
      labelTitle.setAlignmentX(0.5f);
      labelTitle.setFont(AppFont.fontNanumGothic.deriveFont(18f));
      // 재생 시간 라벨
      labelTimeElapsed = new JLabel(toTimeString(0));
      labelTimeElapsed.setAlignmentX(0.1f);
      labelTimeElapsed.setFont(AppFont.fontSans.deriveFont(12f));
      // 재생 길이 라벨
      labelTimeLength = new JLabel(toTimeString(this.audioPlayer.getClipSecondLength()));
      labelTimeLength.setAlignmentX(0.9f);
      labelTimeLength.setFont(AppFont.fontSans.deriveFont(12f));

      // 라벨 제목 패널 자체 설정
      JPanel panelLabel = new JPanel();
      panelLabel.setLayout(new BoxLayout(panelLabel, BoxLayout.X_AXIS));
      panelLabel.setPreferredSize(panelButtons.getPreferredSize());
      panelLabel.add(Box.createHorizontalStrut(6));
      panelLabel.add(labelTimeElapsed);
      panelLabel.add(Box.createHorizontalGlue());
      panelLabel.add(labelTitle);
      panelLabel.add(Box.createHorizontalGlue());
      panelLabel.add(labelTimeLength);
      panelLabel.add(Box.createHorizontalStrut(6));


      /****************************************
       * 슬라이더 패널에 들어갈 요소들
       ****************************************/
      final long sliderUnit = 10_000L;
      JSlider sliderPlayProgress;
      // 재생 시간 표시 슬라이더
      sliderPlayProgress = new JSlider(SwingConstants.HORIZONTAL);
      Dimension preferred = this.parent.getSize();
      preferred.height = 20;
      sliderPlayProgress.setPreferredSize(preferred);
      sliderPlayProgress.setEnabled(false);
      sliderPlayProgress.setMaximum(
            (int) (this.audioPlayer.getAudioClip().getMicrosecondLength() / sliderUnit)); // 0.01sec

      sliderPlayProgress.addMouseMotionListener(new MouseMotionListener() {
         @Override
         public void mouseDragged(MouseEvent e) {
            float percentage = e.getX() / (float) sliderPlayProgress.getWidth();
            int value = (int) (sliderPlayProgress.getMaximum() * percentage);
            value = Math.max(value, 0);
            value = Math.min(value, sliderPlayProgress.getMaximum());
            SoundPanel.this.audioPlayer.getAudioClip().setMicrosecondPosition(value * sliderUnit);
            sliderPlayProgress.setValue(value);
         }

         @Override
         public void mouseMoved(MouseEvent e) {}
      });

      sliderPlayProgress.addMouseListener(new MouseInputAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            float percentage = e.getX() / (float) sliderPlayProgress.getWidth();
            int value = (int) (sliderPlayProgress.getMaximum() * percentage);
            SoundPanel.this.audioPlayer.getAudioClip().setMicrosecondPosition(value * sliderUnit);
            sliderPlayProgress.setValue(value);
         }

         @Override
         public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
            SoundPanel.this.audioPlayer.pause();
            SoundPanel.this.setSliderPressed(true);
         }

         @Override
         public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            SoundPanel.this.audioPlayer.resume();
            SoundPanel.this.setSliderPressed(false);
         }
      });

      sliderPlayProgress.addChangeListener(e -> {
         labelTimeElapsed.setText(toTimeString(this.audioPlayer.getClipSecondPosition()));
      });

      System.out.println(this.uiRefresher.toString());
      this.uiRefresher.scheduleAtFixedRate(() -> {
         sliderPlayProgress.setValue(
               (int) (this.audioPlayer.getAudioClip().getMicrosecondPosition() / sliderUnit));
      } , 500, 100, TimeUnit.MILLISECONDS);
      System.out.println(this.uiRefresher.toString());

      // 슬라이더 패널 자체 설정
      JPanel panelSlider = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
      panelSlider.add(sliderPlayProgress);

      JPanel panelPlayer = new JPanel();
      panelPlayer.setLayout(new BoxLayout(panelPlayer, BoxLayout.Y_AXIS));
      panelPlayer.add(Box.createVerticalStrut(2));
      panelPlayer.add(panelButtons);
      panelPlayer.add(Box.createVerticalStrut(5));
      panelPlayer.add(panelLabel);
      panelPlayer.add(Box.createVerticalStrut(5));
      panelPlayer.add(panelSlider);
      panelPlayer.add(Box.createVerticalStrut(2));

      return panelPlayer;
   }

   /**
    * 레코딩 패널을 생성해서 반환한다.
    *
    * @return 레코딩 패널, not null
    */
   private JPanel createRecordingPanel() {
      if (this.uiRefresher.getTaskCount() > 0)
         this.uiRefresher.purge();
      this.fpAudio = null; // 현재 열린 오디오 파일을 초기화 함.

      /* 버튼 패널 시작 */
      JButton btnOpen, btnRecordStop, btnPauseResume;
      /* 열기 버튼 */
      btnOpen = new JButton();
      setButtonIcons(btnOpen, this.iconsOpen);

      /* 녹음 시작/정지 버튼 */
      btnRecordStop = new JButton();
      setButtonIcons(btnRecordStop, this.iconsRecord);

      /* 녹음 일시정지/재개 버튼 */
      btnPauseResume = new JButton();
      setButtonIcons(btnPauseResume, this.iconsPause);
      btnPauseResume.setEnabled(false);

      /* 열기 버튼의 리스너를 등록한다. */
      btnOpen.addActionListener(e -> {
         this.openSoundFile();
      });

      /* 녹음 시작/정지 버튼의 리스너를 등록한다 */
      btnRecordStop.addActionListener(e -> {
         if (this.audioRecorder.isRecording()) {
            if (this.audioRecorder.isPaused()) {
               this.audioRecorder.resumeRecording();
               setButtonIcons(btnPauseResume, this.iconsPause);
            }

            /* 녹음을 종료하고 파일을 저장한다. */
            byte[] soundBinaries = this.audioRecorder.stopRecording();
            Thread saveThread = this.audioRecorder.save(this.fpAudio, soundBinaries);
            setButtonIcons(btnRecordStop, this.iconsRecord);
            btnPauseResume.setEnabled(false);
            System.out.println("RECORDING STOPPED!");
            try {
               saveThread.join();
               this.setFpAudio(this.fpAudio);
               this.openPlayingPanel();
            } catch (Exception e1) {
               e1.printStackTrace();
            }

         } else {
            /* 녹음을 시작한다. */
            this.audioRecorder.startRecording();
            setButtonIcons(btnRecordStop, this.iconsStop);
            btnPauseResume.setEnabled(true);
            System.out.println("RECORDING STARTED!");
            this.fpAudio = new File("records", SoundPanel.getNowTime() + ".wav");
         }
      });

      /* 녹음 일시중지/재개 버튼의 리스너를 등록한다. */
      btnPauseResume.addActionListener(e -> {
         if (this.audioRecorder.isPaused()) {
            /* 녹음을 재개한다. */
            this.audioRecorder.resumeRecording();
            setButtonIcons(btnPauseResume, this.iconsPause);
            System.out.println("RECORDING RESUMED!");

         } else {
            /* 녹음을 일시중지한다. */
            this.audioRecorder.pauseRecording();
            setButtonIcons(btnPauseResume, this.iconsPlay);
            System.out.println("RECORDING PAUSED!");
         }
      });

      JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
      panelButtons.add(btnOpen);
      panelButtons.add(btnRecordStop);
      panelButtons.add(btnPauseResume);

      /* 타이머 패널 시작 */
      JLabel labelRecordTimer = new JLabel(toTimeString(0));
      labelRecordTimer.setFont(AppFont.fontSans.deriveFont(12f));

      JPanel panelTimer = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
      panelTimer.add(labelRecordTimer);

      System.out.println(this.uiRefresher.toString());
      this.uiRefresher.scheduleAtFixedRate(() -> {
         if (this.audioRecorder.isRecording())
            labelRecordTimer.setText(toTimeString(this.audioRecorder.getSecondPosition()));
      } , 500, 100, TimeUnit.MICROSECONDS);
      System.out.println(this.uiRefresher.toString());

      /* 패널 자체 설정 */
      JPanel panelRecording = new JPanel();
      panelRecording.setLayout(new BoxLayout(panelRecording, BoxLayout.Y_AXIS));
      panelRecording.add(Box.createVerticalStrut(2));
      panelRecording.add(panelButtons);
      panelRecording.add(Box.createVerticalStrut(2));
      panelRecording.add(panelTimer);
      panelRecording.add(Box.createVerticalStrut(2));
      return panelRecording;
   }

   /**
    * 플레잉 패널로 교체한다.
    */
   private void openPlayingPanel() {
      if (this.fpAudio != null) {
         new Thread(() -> {
            this.parent.remove(this.currentShowingPanel);
            this.currentShowingPanel = this.createPlayingPanel();
            this.parent.add(this.currentShowingPanel, 0);
            this.parent.revalidate();
         }).start();
      }
   }

   /**
    * 녹음 파일을 연다.
    */
   private void openSoundFile() {
      String lastPath = null;
      if (this.fpAudio != null)
         lastPath = this.fpAudio.getParent();
      else
         lastPath = System.getProperty("user.dir") + "/records";
      JFileChooser fileChooser = new JFileChooser(lastPath);
      fileChooser.setFileFilter(FILE_WAV_FILTER);
      fileChooser.setAcceptAllFileFilterUsed(false);
      fileChooser.setDialogTitle("Open Audio File");

      int userChoice = fileChooser.showOpenDialog(this.parent);
      if (userChoice == JFileChooser.APPROVE_OPTION) {
         this.setFpAudio(fileChooser.getSelectedFile());
         this.openPlayingPanel();
      }
   }

   /**
    * fpAudio 객체를 설정하고 로드한다.
    *
    * @param fp 녹음 파일을 가리키는 파일 객체
    */
   private void setFpAudio(File fp) {
      this.fpAudio = fp;
      try {
         this.audioPlayer.load(this.fpAudio.getAbsolutePath());
      } catch (UnsupportedAudioFileException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (LineUnavailableException e) {
         e.printStackTrace();
      }
      System.out.println(this.fpAudio + " is loaded.");
   }
}
