package hjsi.posthangeul.window;

import hjsi.posthangeul.action.FileSaveAction;
import hjsi.posthangeul.editor.HangeulAssembler;
import hjsi.posthangeul.editor.SwiftEditor;
import hjsi.posthangeul.editor.VisibleCaretListener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class PostHangeulApp {
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

   SwiftEditor editor;
   /* config of app instance */
   File currentFile = new File("제목 없음.rtf");
   String[] defaultFontFamilies = {"나눔바른고딕", "나눔고딕", "맑은 고딕", "새굴림", "굴림"};
   int[] predefinedFontSizes = {9, 12, 16, 22};

   int defaultFontSize = predefinedFontSizes[1];

   public PostHangeulApp() {
      mainWindow = new JFrame();
      mainWindow.setTitle("Post Hangeul - " + this.currentFile.getName());

      topMenu = new JPanel();
      topMenu.setLayout(new BoxLayout(topMenu, BoxLayout.Y_AXIS));

      /* create menu */
      shortcuts = new Shortcut(this, 24);
      recorder = new Recorder(this, 24);
      topMenu.add(shortcuts);
      topMenu.add(recorder);

      mainWindow.getContentPane().add(topMenu, BorderLayout.NORTH);
      mainWindow.getContentPane().add(new FileTree(new File(".")), BorderLayout.WEST);

      /* add editor */
      editor = new SwiftEditor();
      editor.setFont(shortcuts.getFontFamily(), defaultFontSize);
      editor.addCaretListener(shortcuts);
      editor.addCaretListener(new VisibleCaretListener()); // for word wrapping
      mainWindow.getContentPane().add(editor, BorderLayout.CENTER);

      /* create main window */
      mainWindow.setBounds(0, 0, 800, 480);
      mainWindow.setMinimumSize(new Dimension(600, 480));
      mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      mainWindow.setLocationRelativeTo(null);
      mainWindow.setVisible(true);

      getWindow().addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent ev) {
            if (getEditor().getTextPane().getDocument().getLength() > 0) {
               FileSaveAction save = new FileSaveAction(PostHangeulApp.this, false);
               save.askSave(new ActionEvent(getWindow(), ActionEvent.ACTION_PERFORMED, "Exit"));
            }
         }
      });

      keyCodeViewer = new KeyCodeViewer(mainWindow, "KeyCode Viewer", false);
   }

   /**
    * @return the currentFile
    */
   public File getCurrentFile() {
      return this.currentFile;
   }

   public SwiftEditor getEditor() {
      return editor;
   }

   public JFrame getWindow() {
      return mainWindow;
   }

   /**
    * @param currentFile the currentFile to set
    */
   public void setCurrentFile(File currentDocument) {
      this.currentFile = currentDocument;
      mainWindow.setTitle("Post Hangeul - " + this.currentFile.getName());
   }

}
