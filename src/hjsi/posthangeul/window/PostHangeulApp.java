package hjsi.posthangeul.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import hjsi.posthangeul.action.FileSaveAction;
import hjsi.posthangeul.action.FileSaveAction;
import hjsi.posthangeul.editor.HangeulAssembler;
import hjsi.posthangeul.editor.HangeulAssembler;
import hjsi.posthangeul.editor.SwiftEditor;
import hjsi.posthangeul.editor.SwiftEditor;
import hjsi.posthangeul.editor.VisibleCaretListener;
import hjsi.posthangeul.editor.VisibleCaretListener;


public class PostHangeulApp {
   public static final String appPath;

   static {
      appPath = System.getProperty("user.dir");
      System.out.println(appPath);
   }

   public static void main(String[] args) {

      new HangeulAssembler(); // 테스트 코드 출력용
      PostHangeulApp app = new PostHangeulApp();
      app.editor.requestFocus();
   }

   JPanel topMenu;
   JFrame mainWindow;
   KeyCodeViewer keyCodeViewer;
   Shortcut shortcuts;
   Recorder recorder;
   Player player;

   SwiftEditor editor;
   /* config of app instance */
   File currentFile = new File("제목 없음.rtf");
   String[] defaultFontFamilies = {"나눔바른고딕", "나눔고딕", "맑은 고딕", "새굴림", "굴림"};
   int[] predefinedFontSizes = {9, 12, 16, 22};

   int defaultFontSize = this.predefinedFontSizes[1];

   public PostHangeulApp() {
      this.mainWindow = new JFrame();
      this.mainWindow.setTitle("Post Hangeul - " + this.currentFile.getName());
      this.mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      this.topMenu = new JPanel();
      this.topMenu.setLayout(new BoxLayout(this.topMenu, BoxLayout.Y_AXIS));

      /* create menu */
      this.shortcuts = new Shortcut(this, 24);
      this.recorder = new Recorder(24);
      this.topMenu.add(this.shortcuts);
      this.topMenu.add(this.recorder);
      SwingUtilities.invokeLater(() -> {
         PostHangeulApp.this.player = new Player(PostHangeulApp.this); // create player
         PostHangeulApp.this.topMenu.add(PostHangeulApp.this.player);
      });
      this.mainWindow.getContentPane().add(this.topMenu, BorderLayout.NORTH);

      /* add editor */
      this.editor = new SwiftEditor();
      this.editor.setFont(this.shortcuts.getFontFamily(), this.defaultFontSize);
      this.editor.addCaretListener(this.shortcuts);
      this.editor.addCaretListener(new VisibleCaretListener()); // for word wrapping
      this.mainWindow.getContentPane().add(this.editor, BorderLayout.CENTER);

      /* create main window */
      this.mainWindow.setBounds(0, 0, 900, 480);
      this.mainWindow.setMinimumSize(new Dimension(900, 480));
      this.mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.mainWindow.setLocationRelativeTo(null);
      this.mainWindow.setVisible(true);

      this.getWindow().addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent ev) {
            if (PostHangeulApp.this.getEditor().getTextPane().getDocument().getLength() > 0) {
               FileSaveAction save = new FileSaveAction(PostHangeulApp.this, false);
               save.askSave(new ActionEvent(PostHangeulApp.this.getWindow(),
                     ActionEvent.ACTION_PERFORMED, "Exit"));
            }
         }
      });

      this.keyCodeViewer = new KeyCodeViewer(this.mainWindow, "KeyCode Viewer", false);
   }

   /**
    * @return the currentFile
    */
   public File getCurrentFile() {
      return this.currentFile;
   }

   public SwiftEditor getEditor() {
      return this.editor;
   }

   public JFrame getWindow() {
      return this.mainWindow;
   }

   /**
    * @param currentFile the currentFile to set
    */
   public void setCurrentFile(File currentDocument) {
      this.currentFile = currentDocument;
      this.mainWindow.setTitle("Post Hangeul - " + this.currentFile.getName());
   }

}
