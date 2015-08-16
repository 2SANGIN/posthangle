package hjsi.posthangeul.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;
import hjsi.posthangeul.editor.SwiftEditor;

public class PostHangeulApp {
  JFrame mainWindow;
  KeyCodeViewer keyCodeViewer;
  Shortcut shortcuts;
  SwiftEditor editor;

  /* config of app instance */
  File currentFile = new File("제목 없음.rtf");
  String[] defaultFontFamilies = {"나눔바른고딕", "나눔고딕", "맑은 고딕", "새굴림", "굴림"};
  int[] predefinedFontSizes = {9, 12, 16, 22};
  int defaultFontSize = predefinedFontSizes[1];

  public PostHangeulApp() {
    mainWindow = new JFrame();
    mainWindow.setTitle("Post Hangeul - " + this.currentFile.getName());

    /* create menu */
    shortcuts = new Shortcut(this, 24);
    mainWindow.getContentPane().add(shortcuts, BorderLayout.NORTH);
    
    /* add editor */
    editor = new SwiftEditor();
    editor.setFont(shortcuts.getFontFamily(), defaultFontSize);
    editor.addCaretListener(shortcuts);
    mainWindow.getContentPane().add(editor, BorderLayout.CENTER);

    /* create main window */
    mainWindow.setBounds(0, 0, 800, 480);
    mainWindow.setMinimumSize(new Dimension(600, 480));
    mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainWindow.setLocationRelativeTo(null);
    mainWindow.setVisible(true);

    keyCodeViewer = new KeyCodeViewer(mainWindow, "KeyCode Viewer", false);
  }

  public static void main(String[] args) {
    PostHangeulApp app = new PostHangeulApp();
    app.editor.requestFocus();
    
  }

  /**
   * @return the currentFile
   */
  public File getCurrentFile() {
    return this.currentFile;
  }

  /**
   * @param currentFile the currentFile to set
   */
  public void setCurrentFile(File currentDocument) {
    this.currentFile = currentDocument;
    mainWindow.setTitle("Post Hangeul - " + this.currentFile.getName());
  }
  
  public JFrame getWindow() {
	  return mainWindow;
  }
}
